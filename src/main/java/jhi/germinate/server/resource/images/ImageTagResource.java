package jhi.germinate.server.resource.images;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.ImageTag;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.StringUtils;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.ImageToTags.*;
import static jhi.germinate.server.database.codegen.tables.Images.*;
import static jhi.germinate.server.database.codegen.tables.Imagetags.*;
import static jhi.germinate.server.database.codegen.tables.Imagetypes.*;

/**
 * @author Sebastian Raubach
 */
public class ImageTagResource extends PaginatedServerResource
{
	private String  referenceTable;
	private Integer foreignId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.referenceTable = getRequestAttributes().get("referenceTable").toString();
		}
		catch (NullPointerException e)
		{
		}
		try
		{
			this.foreignId = Integer.parseInt(getRequestAttributes().get("foreignId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public PaginatedResult<List<ImageTag>> getJson()
	{
		try (DSLContext context = Database.getContext())
		{
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
