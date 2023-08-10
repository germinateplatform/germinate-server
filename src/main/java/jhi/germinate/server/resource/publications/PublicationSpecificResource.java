package jhi.germinate.server.resource.publications;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTablePublications;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.util.Secured;
import org.jooq.DSLContext;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.ViewTablePublications.VIEW_TABLE_PUBLICATIONS;

@Path("publication/{publicationId:\\d+}")
@Secured
@PermitAll
public class PublicationSpecificResource extends ContextResource
{
	@PathParam("publicationId")
	Integer publicationId;

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPublicationById()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			ViewTablePublications publication = context.selectFrom(VIEW_TABLE_PUBLICATIONS)
													   .where(VIEW_TABLE_PUBLICATIONS.PUBLICATION_ID.eq(publicationId))
													   .fetchAnyInto(ViewTablePublications.class);

			if (publication == null)
				return Response.status(Response.Status.NOT_FOUND).build();
			else
				return Response.ok(publication).build();
		}
	}
}
