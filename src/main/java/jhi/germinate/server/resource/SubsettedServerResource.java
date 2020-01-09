package jhi.germinate.server.resource;

import org.jooq.*;
import org.jooq.impl.TableImpl;

import java.util.*;

import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.server.util.CollectionUtils;

import static jhi.germinate.server.database.tables.Groupmembers.*;

/**
 * @author Sebastian Raubach
 */
public class SubsettedServerResource extends BaseServerResource
{
	protected Set<Integer> getYIds(DSLContext context, TableImpl<? extends Record> table, Field<Integer> field, SubsettedDatasetRequest request)
	{
		Set<Integer> result = new LinkedHashSet<>();

		if (!CollectionUtils.isEmpty(request.getyIds()))
		{
			result.addAll(context.selectDistinct(field)
								 .from(table)
								 .where(field.in(request.getyIds()))
								 .fetchInto(Integer.class));
		}
		if (!CollectionUtils.isEmpty(request.getyGroupIds()))
		{
			result.addAll(context.selectDistinct(field)
								 .from(table)
								 .leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(field))
								 .where(GROUPMEMBERS.GROUP_ID.in(request.getyGroupIds()))
								 .fetchInto(Integer.class));
		}

		return result;
	}
}
