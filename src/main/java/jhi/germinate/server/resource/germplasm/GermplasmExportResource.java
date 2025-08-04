package jhi.germinate.server.resource.germplasm;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;

@Path("germplasm/export")
@Secured
@PermitAll
public class GermplasmExportResource extends GermplasmBaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postGermplasmExport(GermplasmExportRequest request)
		throws IOException, SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			List<Integer> datasetIds = AuthorizationFilter.getDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), null, true);

			DSLContext context = Database.getContext(conn);
			if (request != null && request.getIncludeAttributes() != null && request.getIncludeAttributes())
			{
				String individualIdString = CollectionUtils.join(request.getIndividualIds(), ",");
				String groupIdString = CollectionUtils.join(request.getGroupIds(), ",");

				ExportPassportData procedure = new ExportPassportData();

				if (!StringUtils.isEmpty(individualIdString))
					procedure.setGermplasmids(individualIdString);
				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);

				procedure.execute(context.configuration());

				return ResourceUtils.exportToZip(procedure.getResults().get(0), resp, "germplasm-table-");
			}
			else
			{
				if (request != null)
				{
					if (request.getIndividualIds() != null)
					{
						String[] ids = Arrays.stream(request.getIndividualIds())
											 .map(i -> Integer.toString(i))
											 .toArray(String[]::new);

						Filter[] filters = new Filter[1];
						filters[0] = new Filter(GERMPLASM_ID, "inSet", "and", ids);
						request.setFilter(filters);

						processRequest(request);

						SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, null);

						// Filter here!
						having(from, filters);

						return ResourceUtils.exportToZip(from.fetch(), resp, "germplasm-table-");
					}
					else if (request.getGroupIds() != null)
					{
						processRequest(request);

						Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
						List<GermplasmBaseResource.Join<Integer>> joins = new ArrayList<>();
						joins.add(new GermplasmBaseResource.Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
						SelectJoinStep<?> from = getGermplasmQueryWrapped(context, datasetIds, joins, fieldGroupId);
						from.having(fieldGroupId.in(request.getGroupIds()));

						return ResourceUtils.exportToZip(from.fetch(), resp, "germplasm-table-");
					}
				}

				// We get here if nothing specific was specified
				processRequest(request);
				return ResourceUtils.exportToZip(getGermplasmQueryWrapped(context, datasetIds, null).fetch(), resp, "germplasm-table-");
			}
		}
	}
}
