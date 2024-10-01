package jhi.germinate.resource;

import java.math.BigDecimal;

public class GermplasmStats
{
	private Integer    germplasmId;
	private String     germplasmName;
	private Integer    traitId;
	private String     traitName;
	private String traitNameShort;
	private BigDecimal min;
	private BigDecimal avg;
	private BigDecimal max;
	private Integer    count;

	public Integer getGermplasmId()
	{
		return germplasmId;
	}

	public void setGermplasmId(Integer germplasmId)
	{
		this.germplasmId = germplasmId;
	}

	public String getGermplasmName()
	{
		return germplasmName;
	}

	public void setGermplasmName(String germplasmName)
	{
		this.germplasmName = germplasmName;
	}

	public Integer getTraitId()
	{
		return traitId;
	}

	public void setTraitId(Integer traitId)
	{
		this.traitId = traitId;
	}

	public String getTraitName()
	{
		return traitName;
	}

	public void setTraitName(String traitName)
	{
		this.traitName = traitName;
	}

	public BigDecimal getMin()
	{
		return min;
	}

	public void setMin(BigDecimal min)
	{
		this.min = min;
	}

	public BigDecimal getAvg()
	{
		return avg;
	}

	public void setAvg(BigDecimal avg)
	{
		this.avg = avg;
	}

	public BigDecimal getMax()
	{
		return max;
	}

	public void setMax(BigDecimal max)
	{
		this.max = max;
	}

	public Integer getCount()
	{
		return count;
	}

	public void setCount(Integer count)
	{
		this.count = count;
	}

	public String getTraitNameShort()
	{
		return traitNameShort;
	}

	public GermplasmStats setTraitNameShort(String traitNameShort)
	{
		this.traitNameShort = traitNameShort;
		return this;
	}
}
