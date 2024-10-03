package jhi.germinate.resource;

import java.util.*;

public class TraitStats
{
	private Integer            traitId;
	private String             traitName;
	private String             traitNameShort;
	private Double             min;
	private Double             avg;
	private Double             max;
	private Integer            count;
	private List<List<String>> categories;
	private String dataType;

	public Integer getTraitId()
	{
		return traitId;
	}

	public TraitStats setTraitId(Integer traitId)
	{
		this.traitId = traitId;
		return this;
	}

	public String getTraitName()
	{
		return traitName;
	}

	public TraitStats setTraitName(String traitName)
	{
		this.traitName = traitName;
		return this;
	}

	public String getTraitNameShort()
	{
		return traitNameShort;
	}

	public TraitStats setTraitNameShort(String traitNameShort)
	{
		this.traitNameShort = traitNameShort;
		return this;
	}

	public Double getMin()
	{
		return min;
	}

	public TraitStats setMin(Double min)
	{
		this.min = min;
		return this;
	}

	public Double getAvg()
	{
		return avg;
	}

	public TraitStats setAvg(Double avg)
	{
		this.avg = avg;
		return this;
	}

	public Double getMax()
	{
		return max;
	}

	public TraitStats setMax(Double max)
	{
		this.max = max;
		return this;
	}

	public Integer getCount()
	{
		return count;
	}

	public TraitStats setCount(Integer count)
	{
		this.count = count;
		return this;
	}

	public List<List<String>> getCategories()
	{
		return categories;
	}

	public TraitStats setCategories(String[][] categories)
	{
		List<List<String>> result = new ArrayList<>();

		for (String[] cat : categories)
			result.add(new ArrayList<>(Arrays.asList(cat)));

		this.categories = result;
		return this;
	}

	public String getDataType()
	{
		return dataType;
	}

	public TraitStats setDataType(String dataType)
	{
		this.dataType = dataType;
		return this;
	}

	@Override
	public String toString()
	{
		return "TraitStats{" +
				"traitId=" + traitId +
				", traitName='" + traitName + '\'' +
				", traitNameShort='" + traitNameShort + '\'' +
				", min=" + min +
				", avg=" + avg +
				", max=" + max +
				", count=" + count +
				", categories=" + categories +
				'}';
	}
}
