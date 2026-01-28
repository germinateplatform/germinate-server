package jhi.germinate.server.resource;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Sebastian Raubach
 */
public interface IFilteredResource
{
	default <T extends Record> void having(SelectHavingConditionStep<T> step, FilterGroup[] filters)
	{
		having(step, filters, false);
	}

	default <T extends Record> void having(SelectJoinStep<T> step, FilterGroup[] filters)
	{
		having(step, filters, false);
	}

	default Condition recursive(FilterGroup[] filters, boolean jsonOperationAllowed)
	{
		Condition overall = null;
		if (!CollectionUtils.isEmpty(filters))
		{
			for (FilterGroup filterGroup : filters)
			{
				Condition child = recursive(filterGroup, jsonOperationAllowed);

				if (child != null)
				{
					if (overall == null)
					{
						overall = child;
					}
					else
					{
						if (filterGroup.getOperator() == FilterOperator.and)
							overall = overall.and(child);
						else
							overall = overall.or(child);
					}
				}
			}
		}
		return overall;
	}

	default <T extends Record> void having(SelectHavingConditionStep<T> step, FilterGroup[] filters, boolean jsonOperationAllowed)
	{
		Condition overall = recursive(filters, jsonOperationAllowed);
		if (overall != null)
			step.and(overall);
	}

	default <T extends Record> void having(SelectJoinStep<T> step, FilterGroup[] filters, boolean jsonOperationAllowed)
	{
		Condition overall = recursive(filters, jsonOperationAllowed);
		if (overall != null)
			step.having(overall);
	}

	default <T extends Record> void where(SelectConditionStep<T> step, FilterGroup[] filters)
	{
		where(step, filters, false);
	}

	default <T extends Record> void where(SelectJoinStep<T> step, FilterGroup[] filters)
	{
		where(step, filters, false);
	}

	default <T extends Record> void where(SelectConditionStep<T> step, FilterGroup[] filters, boolean jsonOperationAllowed)
	{
		Condition overall = recursive(filters, jsonOperationAllowed);
		if (overall != null)
			step.and(overall);
	}

	default <T extends Record> void where(SelectJoinStep<T> step, FilterGroup[] filters, boolean jsonOperationAllowed)
	{
		Condition overall = recursive(filters, jsonOperationAllowed);
		if (overall != null)
			step.where(overall);
	}

	default List<Filter> findFiltersWithColumn(FilterGroup[] filterGroups, String columnName)
	{
		List<Filter> filters = new ArrayList<>();

		for (FilterGroup filterGroup : filterGroups)
			filters.addAll(findFiltersWithColumnRecursive(filterGroup, columnName));

		return filters;
	}

	default List<Filter> findFiltersWithColumnRecursive(FilterGroup group, String columnName)
	{
		List<Filter> filters = new ArrayList<>();

		if (!CollectionUtils.isEmpty(group.getFilterGroups()))
		{
			for (FilterGroup filterGroup : group.getFilterGroups())
				filters.addAll(findFiltersWithColumnRecursive(filterGroup, columnName));
		}
		if (!CollectionUtils.isEmpty(group.getFilters()))
		{
			for (Filter filter : group.getFilters())
			{
				if (Objects.equals(filter.getColumn(), columnName))
					filters.add(filter);
			}
		}

		return filters;
	}

	default Condition recursive(FilterGroup filterGroup, boolean jsonOperationAllowed)
	{
		Condition overall = null;
		if (!CollectionUtils.isEmpty(filterGroup.getFilterGroups()))
		{
			for (FilterGroup filterGroupChild : filterGroup.getFilterGroups())
			{
				Condition child = recursive(filterGroupChild, jsonOperationAllowed);

				if (overall == null)
				{
					overall = child;
				}
				else
				{
					if (filterGroup.getOperator() == FilterOperator.and)
						overall = overall.and(child);
					else
						overall = overall.or(child);
				}
			}
		}
		if (!CollectionUtils.isEmpty(filterGroup.getFilters()))
		{
			for (Filter filter : filterGroup.getFilters())
			{
				Condition child = filterIndividual(filter, jsonOperationAllowed);

				if (overall == null)
				{
					overall = child;
				}
				else
				{
					if (filterGroup.getOperator() == FilterOperator.and)
						overall = overall.and(child);
					else
						overall = overall.or(child);
				}
			}
		}

		return overall;
	}

	default Condition filterIndividual(Filter filter, boolean jsonOperationAllowed)
	{
		Field<String> field = DSL.field(filter.getSafeColumn(), String.class);
		List<String> values = new ArrayList<>();

		if (!CollectionUtils.isEmpty(filter.getValues()))
			values = Arrays.stream(filter.getValues())
						   .filter(v -> !StringUtils.isEmpty(v))
						   .map(String::strip)
						   .collect(Collectors.toList());

		if (CollectionUtils.isEmpty(values))
			values.add("");

		String first = values.get(0);
		String second = values.size() > 1 ? values.get(1) : null;

		switch (filter.getComparator())
		{
			case FilterComparator.isNull:
				return field.isNull();
			case FilterComparator.isNotNull:
				return field.isNotNull();
			case FilterComparator.equals:
				return field.eq(first);
			case FilterComparator.contains:
				return DSL.lower(field).like("%" + (first == null ? "" : first.toLowerCase()) + "%");
			case FilterComparator.startsWith:
				return DSL.lower(field).startsWith(first == null ? "" : first.toLowerCase());
			case FilterComparator.endsWith:
				return DSL.lower(field).endsWith(first == null ? "" : first.toLowerCase());
			case FilterComparator.between:
				return field.between(first, second);
			case FilterComparator.greaterThan:
				return field.greaterThan(first);
			case FilterComparator.greaterOrEquals:
				return field.greaterOrEqual(first);
			case FilterComparator.lessThan:
				return field.lessThan(first);
			case FilterComparator.lessOrEquals:
				return field.lessOrEqual(first);
			case FilterComparator.jsonSearch:
				if (jsonOperationAllowed)
				{
					List<Condition> conditions = values.stream()
													   .filter(v -> !StringUtils.isEmpty(v))
													   .map(v -> v.replaceAll("[^a-zA-Z0-9_-]", "")) // Replace all non letters and numbers
													   .map(v -> DSL.condition("JSON_SEARCH(LOWER(" + field.getName() + "), 'one', LOWER('%" + v + "%')) IS NOT NULL"))
													   .toList();

					Condition result = conditions.get(0);

					for (int i = 1; i < conditions.size(); i++)
					{
						result = result.or(conditions.get(i));
					}

					return result;
				}
				else
				{
					Logger.getLogger("").warning("Trying to use a json operation, but not allowed: " + filter);
					return null;
				}
			case FilterComparator.arrayContains:
				if (jsonOperationAllowed)
				{
					List<Condition> conditions = values.stream()
													   .map(v -> v.replaceAll("[^a-zA-Z0-9_-]", "")) // Replace all non letters and numbers
													   .map(v -> DSL.condition("JSON_CONTAINS(" + field.getName() + ", '" + v + "')"))
													   .toList();

					Condition result = conditions.get(0);

					for (int i = 1; i < conditions.size(); i++)
					{
						result = result.or(conditions.get(i));
					}

					return result;
				}
				else
				{
					Logger.getLogger("").warning("Trying to use a json operation, but not allowed: " + filter);
					return null;
				}
			case FilterComparator.inSet:
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
								 .map(String::strip)
								 .collect(Collectors.toList());
				}

				return field.in(temp);
		}

		return null;
	}
}
