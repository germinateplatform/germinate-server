package jhi.germinate.server.resource.pedigrees;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;

import jhi.germinate.resource.PedigreeRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.records.ViewTablePedigreesRecord;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.*;
import static jhi.germinate.server.database.codegen.tables.ViewTablePedigrees.*;

/**
 * @author Sebastian Raubach
 */
public class PedigreeExportResource extends BaseServerResource
{
	@Post
	public FileRepresentation postJson(PedigreeRequest request)
	{
		if (request == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		FileRepresentation representation;

		try
		{
			File file = createTempFile("pedigree", "helium");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				Map<String, List<ViewTablePedigreesRecord>> parentToChildren = new HashMap<>();
				Map<String, List<ViewTablePedigreesRecord>> childrenToParents = new HashMap<>();
				context.selectFrom(VIEW_TABLE_PEDIGREES)
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

				export(context, bw, parentToChildren, childrenToParents, request);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
			disp.setFilename(file.getName());
			disp.setSize(file.length());
			representation.setDisposition(disp);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}

	private void export(DSLContext context, PrintWriter bw, Map<String, List<ViewTablePedigreesRecord>> down, Map<String, List<ViewTablePedigreesRecord>> up, PedigreeRequest request)
	{
		bw.write("# heliumInput = PEDIGREE" + CRLF);
		bw.write("LineName\tParent\tParentType" + CRLF);

		if (CollectionUtils.isEmpty(request.getGroupIds()) && CollectionUtils.isEmpty(request.getIndividualIds()))
		{
			if (down.size() < 1)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
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

			if (!CollectionUtils.isEmpty(request.getIndividualIds()))
				step.and(GERMINATEBASE.ID.in(request.getIndividualIds()));
			if (!CollectionUtils.isEmpty(request.getGroupIds()))
				step.and(DSL.exists(DSL.selectOne()
									   .from(GROUPMEMBERS)
									   .where(GROUPMEMBERS.GROUP_ID.in(request.getGroupIds())
																   .and(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID)))));

			List<String> requestedNames = step.orderBy(GERMINATEBASE.NAME)
											  .fetchInto(String.class);

			if (CollectionUtils.isEmpty(requestedNames))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

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

	private class PedigreeWriter
	{
		private Set<String>                                 visitedNodes = new HashSet<>();
		private Set<Edge>                                   edges        = new HashSet<>();
		private Map<String, List<ViewTablePedigreesRecord>> mapping;
		private boolean                                     isUp;
		private int                                         maxLevels;
		private PrintWriter                                 bw;

		public PedigreeWriter(PrintWriter bw, Map<String, List<ViewTablePedigreesRecord>> mapping, boolean isUp, int maxLevels)
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
						edges.add(edge);
						bw.write(child + "\t" + parent + "\t" + n.getRelationshipType().getLiteral() + CRLF);

						if (isUp)
							run(parent, level + 1);
						else
							run(child, level + 1);
					}
				});
			}
		}
	}

	private class Edge
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
