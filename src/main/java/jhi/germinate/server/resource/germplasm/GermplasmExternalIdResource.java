package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.*;
import jhi.germinate.server.util.watcher.PropertyWatcher;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.util.List;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmExternalIdResource extends PaginatedServerResource
{
	@Post("json")
	public List<String> getJson(Integer[] ids)
	{
		String identifier = PropertyWatcher.get(ServerProperty.EXTERNAL_LINK_IDENTIFIER);

		if (StringUtils.isEmpty(identifier))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		if (CollectionUtils.isEmpty(ids))
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		try (DSLContext context = Database.getContext())
		{
			Field<?> field = DSL.field(identifier);

			return context.selectDistinct(field)
						  .from(GERMINATEBASE)
						  .where(GERMINATEBASE.ID.in(ids))
						  .fetchInto(String.class);
		}
	}
}
