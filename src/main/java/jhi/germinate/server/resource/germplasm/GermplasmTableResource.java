package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

@Path("germplasm/table")
@Secured
@PermitAll
public class GermplasmTableResource extends GermplasmBaseResource
{
	@DefaultValue("")
	@QueryParam("namesFromFile")
	private String namesFromFile;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableGermplasm>> postGermplasmTable(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, null);

			// Add an additional filter based on the names in the file uploaded from CurlyWhirly
			if (!StringUtils.isEmpty(namesFromFile))
			{
				Field<Integer> fieldId = DSL.field(GermplasmBaseResource.GERMPLASM_ID, Integer.class);
				try
				{
					List<String> names = Files.readAllLines(ResourceUtils.getTempDir(namesFromFile).toPath());

					if (!CollectionUtils.isEmpty(names))
						from.where(fieldId.in(names));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			// Filter here!
			filter(from, filters, true);

			List<ViewTableGermplasm> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableGermplasm.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postGermplasmTableIds(PaginatedRequest request)
		throws SQLException
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record1<Integer>> from = getGermplasmIdQueryWrapped(context, null);

			// Add an additional filter based on the names in the file uploaded from CurlyWhirly
			if (!StringUtils.isEmpty(namesFromFile))
			{
				Field<Integer> fieldId = DSL.field(GermplasmBaseResource.GERMPLASM_ID, Integer.class);
				try
				{
					List<String> names = Files.readAllLines(ResourceUtils.getTempDir(namesFromFile).toPath());

					if (!CollectionUtils.isEmpty(names))
						from.where(fieldId.in(names));
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}

			// Filter here!
			filter(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postGermplasmTableExport(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from = getGermplasmQueryWrapped(context, null);

			// Filter here!
			filter(from, filters);

			return ResourceUtils.exportToZip(from.fetch(), resp, "germplasm-table-");
		}
	}

	@GET
	@Path("/columns")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<String> getGermplasmTableColumns()
	{
		return COLUMNS;
	}
}
