package jhi.germinate.server.resource.images;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Imagetags;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.Secured;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ImageToTags.*;
import static jhi.germinate.server.database.codegen.tables.Imagetags.*;

@Path("image/{imageId}/tag")
@Secured
@PermitAll
public class ImageSpecificTagResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Imagetags>> getSpecificImageTag(@PathParam("imageId") Integer imageId)
		throws IOException, SQLException
	{
		if (imageId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode(), "Missing image id");
			return null;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectConditionStep<Record> from = select.from(IMAGE_TO_TAGS)
													 .leftJoin(IMAGETAGS).on(IMAGETAGS.ID.eq(IMAGE_TO_TAGS.IMAGE_ID))
													 .where(IMAGE_TO_TAGS.IMAGE_ID.eq(imageId));

			List<Imagetags> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(Imagetags.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
