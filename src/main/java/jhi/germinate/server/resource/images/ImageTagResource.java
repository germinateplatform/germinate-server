package jhi.germinate.server.resource.images;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.ImageTag;
import jhi.germinate.server.resource.BaseResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.sql.*;
import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ImageToTags.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetags.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;

@Path("imagetag")
@Secured
@PermitAll
public class ImageTagResource extends BaseResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ImageTag>> getImageTag()
		throws SQLException
	{
		return getImageTag(null);
	}

	@GET
	@Path("/{referenceTable}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ImageTag>> getImageTag(@PathParam("referenceTable") String referenceTable)
		throws SQLException
	{
		return getImageTag(referenceTable, null);
	}

	@GET
	@Path("/{referenceTable}/{foreignId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ImageTag>> getImageTag(@PathParam("referenceTable") String referenceTable, @PathParam("foreignId") Integer foreignId)
		throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record2<Integer, String>> select = context.select(
				IMAGETAGS.ID.as("tagId"),
				IMAGETAGS.TAG_NAME.as("tagName")
			);

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record2<Integer, String>> from = select.from(IMAGETAGS);

			if (!StringUtils.isEmpty(referenceTable))
			{
				Condition where = IMAGETYPES.REFERENCE_TABLE.eq(referenceTable)
															.and(IMAGE_TO_TAGS.IMAGETAG_ID.eq(IMAGETAGS.ID));

				if (foreignId != null)
					where = where.and(IMAGES.FOREIGN_ID.eq(foreignId));

				from.where(DSL.exists(DSL.selectOne().from(IMAGES)
										 .leftJoin(IMAGETYPES).on(IMAGETYPES.ID.eq(IMAGES.IMAGETYPE_ID))
										 .leftJoin(IMAGE_TO_TAGS).on(IMAGE_TO_TAGS.IMAGE_ID.eq(IMAGES.ID))
										 .where(where)));
			}

			List<ImageTag> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ImageTag.class);

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}
}
