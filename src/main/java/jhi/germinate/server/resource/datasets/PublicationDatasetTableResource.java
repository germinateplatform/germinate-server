package jhi.germinate.server.resource.datasets;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.UnacceptedLicenseRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;
import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.*;

@Path("publication/{publicationId}/dataset")
@Secured
@PermitAll
public class PublicationDatasetTableResource extends BaseDatasetTableResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postPublicationDatasetTable(UnacceptedLicenseRequest request, @PathParam("publicationId") Integer publicationId)
		throws SQLException, IOException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications pub = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
											   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
												.and(VIEW_TABLE_PUBLICATIONS.DATASET_IDS.isNotNull())
											   .fetchAnyInto(ViewTablePublications.class);

			if (pub == null)
			{
				resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
				return null;
			}

			Integer[] ids = pub.getDatasetIds();

			return runQuery(request, query -> query.where(VIEW_TABLE_DATASETS.DATASET_ID.in(ids)));
		}
	}
}
