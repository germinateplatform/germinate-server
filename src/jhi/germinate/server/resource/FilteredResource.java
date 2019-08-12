package jhi.germinate.server.resource;

import org.jooq.*;
import org.jooq.impl.*;

import jhi.germinate.resource.*;

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
				switch (filters[i - 1].getOperator())
				{
					case "and":
						where.and(filterIndividual(filters[i]));
						break;
					case "or":
						where.or(filterIndividual(filters[i]));
						break;
				}
			}
		}
	}

	default Condition filterIndividual(Filter filter)
	{
		Field<Object> field = DSL.field(filter.getColumn());

		String[] values = filter.getValues();
		String first = values[0].trim();
		String second = values.length > 1 ? values[1].trim() : null;

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
				String[] temp = values.length > 1 ? values : first.split(",");
				for (int i = 0; i < temp.length; i++)
					temp[i] = temp[i].trim();

				return field.in(temp);
		}

		return DSL.condition("1=1");
	}
}
