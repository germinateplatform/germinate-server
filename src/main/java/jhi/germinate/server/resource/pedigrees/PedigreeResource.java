package jhi.germinate.server.resource.pedigrees;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePedigrees;
import jhi.germinate.server.database.codegen.tables.records.ViewTablePedigreesRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.*;
import static jhi.germinate.server.database.codegen.tables.ViewTablePedigrees.*;

@Path("pedigree")
@Secured
@PermitAll
public class PedigreeResource extends ExportResource
{
	@Path("/table")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTablePedigrees>> postPedigreeTable(PaginatedRequest request)
		throws SQLException
	{
		List<Integer> datasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "pedigree", true);
		if (CollectionUtils.isEmpty(datasets))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(VIEW_TABLE_PEDIGREES)
													 .where(VIEW_TABLE_PEDIGREES.DATASET_ID.in(datasets));

			// Filter here!
			where(from, filters);

			List<ViewTablePedigrees> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTablePedigrees.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public StreamingOutput postPedigreeTableExport(ExportRequest request, @Context HttpServletResponse response)
			throws SQLException, IOException
	{
		processRequest(request);

		return toStreamingResult(export(VIEW_TABLE_PEDIGREES, "pedigree-table-", null), "application/zip", response);
	}

	public static File exportFlatFile(PedigreeRequest request, HttpServletRequest req, SecurityContext securityContext)
		throws IOException, SQLException, StatusException
	{
		if (request == null)
			throw new BadRequestException();

		List<Integer> datasets = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "pedigree", true);

		if (CollectionUtils.isEmpty(datasets))
			throw new NotFoundException();

		File zipFile = ResourceUtils.createTempFile("pedigree", "zip");
		try
		{
			boolean includeAttributes = request.getIncludeAttributes() != null && request.getIncludeAttributes();
			String mt = MediaType.TEXT_PLAIN;
			File heliumFile = ResourceUtils.createTempFile("pedigree", "helium");
			File attributesFile = ResourceUtils.createTempFile("pedigree-attributes", "helium");

			try (Connection conn = Database.getConnection();
				 PrintWriter bwH = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(heliumFile), StandardCharsets.UTF_8)));
				 PrintWriter bwA = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(attributesFile), StandardCharsets.UTF_8))))
			{
				DSLContext context = Database.getContext(conn);
				Map<String, List<ViewTablePedigreesRecord>> parentToChildren = new HashMap<>();
				Map<String, List<ViewTablePedigreesRecord>> childrenToParents = new HashMap<>();
				context.selectFrom(VIEW_TABLE_PEDIGREES)
					   .where(VIEW_TABLE_PEDIGREES.DATASET_ID.in(datasets))
					   .forEach(r -> {
						   String child = r.getChildName();
						   String parent = r.getParentName();

						   List<ViewTablePedigreesRecord> childList = childrenToParents.get(child);
						   List<ViewTablePedigreesRecord> parentList = parentToChildren.get(parent);

						   if (childList == null)
							   childList = new ArrayList<>();
						   if (parentList == null)
							   parentList = new ArrayList<>();

						   childList.add(r);
						   parentList.add(r);

						   childrenToParents.put(child, childList);
						   parentToChildren.put(parent, parentList);
					   });

				export(context, bwH, parentToChildren, childrenToParents, request);

				if (includeAttributes)
				{
					mt = "application/zip";

					ExportPassportData procedure = new ExportPassportData();

					procedure.execute(context.configuration());

					ResourceUtils.exportToFile(bwA, procedure.getResults().get(0), true, null, "#heliumInput = PHENOTYPE");
				}
			}

			File target = includeAttributes ? zipFile : heliumFile;

			if (includeAttributes)
				FileUtils.zipUp(zipFile, Arrays.asList(heliumFile, attributesFile));

			return target;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new InternalServerErrorException();
		}
	}

	private static void export(DSLContext context, PrintWriter bw, Map<String, List<ViewTablePedigreesRecord>> down, Map<String, List<ViewTablePedigreesRecord>> up, PedigreeRequest request)
	{
		bw.write("# heliumInput = PEDIGREE" + CRLF);
		bw.write("LineName\tParent\tParentType" + CRLF);

		if (CollectionUtils.isEmpty(request.getGermplasmGroupIds()) && CollectionUtils.isEmpty(request.getGermplasmIds()))
		{
			if (down.size() < 1)
				throw new NotFoundException();
			else
				down.forEach((p, cs) -> cs.forEach(c -> bw.write(c.getChildName() + "\t" + c.getParentName() + "\t" + c.getRelationshipType().getLiteral() + CRLF)));
		}
		else
		{
			SelectConditionStep<Record1<String>> step = context.selectDistinct(GERMINATEBASE.NAME)
															   .from(GERMINATEBASE)
															   .where(DSL.exists(DSL.selectOne()
																					.from(PEDIGREES)
																					.where(PEDIGREES.GERMINATEBASE_ID.eq(GERMINATEBASE.ID)
																													 .or(PEDIGREES.PARENT_ID.eq(GERMINATEBASE.ID)))));

			if (!CollectionUtils.isEmpty(request.getGermplasmIds()))
				step.and(GERMINATEBASE.ID.in(request.getGermplasmIds()));
			if (!CollectionUtils.isEmpty(request.getGermplasmGroupIds()))
				step.and(DSL.exists(DSL.selectOne()
									   .from(GROUPMEMBERS)
									   .where(GROUPMEMBERS.GROUP_ID.in(request.getGermplasmGroupIds())
																   .and(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID)))));

			List<String> requestedNames = step.orderBy(GERMINATEBASE.NAME)
											  .fetchInto(String.class);

			if (CollectionUtils.isEmpty(requestedNames))
				throw new NotFoundException();

			int upLimit = requestedNames.size() == 1 ? 2 : 3;
			int downLimit = requestedNames.size() == 1 ? 1 : 3;

			// If specific limits have been requested, make sure they're in the interval [1, 5]
			if (request.getLevelsUp() != null)
				upLimit = Math.max(1, Math.min(5, request.getLevelsUp()));
			if (request.getLevelsDown() != null)
				downLimit = Math.max(1, Math.min(5, request.getLevelsDown()));

			PedigreeWriter downWriter = new PedigreeWriter(bw, down, false, downLimit);
			PedigreeWriter upWriter = new PedigreeWriter(bw, up, true, upLimit);

			for (String requested : requestedNames)
			{
				downWriter.run(requested, 0);
				upWriter.run(requested, 0);
			}
		}

	}

	public static class PedigreeWriter
	{
		private Set<String>                                 visitedNodes = new HashSet<>();
		private Set<Edge>                                   edges        = new HashSet<>();
		private Map<String, List<ViewTablePedigreesRecord>> mapping;
		private boolean                                     isUp;
		private int                                         maxLevels;
		private Writer                                      bw;

		public PedigreeWriter(Writer bw, Map<String, List<ViewTablePedigreesRecord>> mapping, boolean isUp, int maxLevels)
		{
			this.mapping = mapping;
			this.isUp = isUp;
			this.maxLevels = maxLevels;
			this.bw = bw;
		}

		public void run(String current, int level)
		{
			if (visitedNodes.contains(current))
				return;
			if (level == maxLevels)
				return;

			visitedNodes.add(current);

			List<ViewTablePedigreesRecord> nodes = mapping.get(current);

			if (nodes != null)
			{
				nodes.forEach(n -> {
					String child = n.getChildName();
					String parent = n.getParentName();

					Edge edge = new Edge(parent, child);

					if (!edges.contains(edge))
					{
						try
						{
							bw.write(child + "\t" + parent + "\t" + n.getRelationshipType().getLiteral() + CRLF);
							edges.add(edge);

							if (isUp)
								run(parent, level + 1);
							else
								run(child, level + 1);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	private static class Edge
	{
		private String from;
		private String to;

		public Edge(String from, String to)
		{
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof Edge)
			{
				Edge e = (Edge) obj;
				return Objects.equals(from, e.from) && Objects.equals(to, e.to);
			}
			else
			{
				return false;
			}
		}
	}
}
