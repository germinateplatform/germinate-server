package jhi.germinate.server.resource;

import org.jooq.*;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.stream.Collectors;

import jhi.germinate.resource.Filter;
import jhi.germinate.server.util.*;

/**
 * @author Sebastian Raubach
 */
public interface FilteredResource
{
	default <T extends Record> void filter(SelectJoinStep<T> step, Filter[] filters)
	{
		if (filters != null && filters.length > 0)
		{
			SelectConditionStep<T> where = step.where(filterIndividual(filters[0]));

			for (int i = 1; i < filters.length; i++)
			{
				Condition condition = filterIndividual(filters[i]);

				if (condition != null)
				{
					switch (filters[i - 1].getOperator())
					{
						case "and":
							where.and(condition);
							break;
						case "or":
							where.or(condition);
							break;
					}
				}
			}
		}
	}

	default Condition filterIndividual(Filter filter)
	{
		Field<Object> field = DSL.field(filter.getSafeColumn());

		List<String> values = Arrays.stream(filter.getValues())
									.filter(v -> !StringUtils.isEmpty(v))
									.map(String::trim)
									.collect(Collectors.toList());

		if(CollectionUtils.isEmpty(values))
			return null;

		String first = values.get(0);
		String second = values.size() > 1 ? values.get(1) : null;

		switch (filter.getComparator())
		{
			case "equals":
				return field.eq(first);
			case "like":
				return field.like("%" + first + "%");
			case "between":
				return field.between(first, second);
			case "greaterThan":
				return field.greaterThan(first);
			case "greaterOrEquals":
				return field.greaterOrEqual(first);
			case "lessThan":
				return field.lessThan(first);
			case "lessOrEquals":
				return field.lessOrEqual(first);
			case "inSet":
				List<String> temp;

				if (values.size() > 1)
				{
					// If there are multiple values, just use them
					temp = values;
				}
				else
				{
					// Otherwise, try and split the first one on commas and then use the individual entries
					temp = Arrays.stream(first.split(","))
								 .filter(v -> !StringUtils.isEmpty(v))
								 .map(String::trim)
								 .collect(Collectors.toList());
				}

				return field.in(temp);
		}

		return DSL.condition("1=1");
	}
}
