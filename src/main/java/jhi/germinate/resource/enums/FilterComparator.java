package jhi.germinate.resource.enums;

public enum FilterComparator
{
	isNull(0),
	isNotNull(0),
	equals,
	contains,
	between(2),
	greaterThan,
	greaterOrEquals,
	lessThan,
	startsWith,
	endsWith,
	lessOrEquals,
	jsonSearch,
	arrayContains,
	inSet;

	private int cardinality = 1;

	private FilterComparator() {
	}

	private FilterComparator(int cardinality) {
		this.cardinality = cardinality;
	}

	public int getCardinality()
	{
		return cardinality;
	}
}
