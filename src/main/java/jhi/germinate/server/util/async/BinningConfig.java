package jhi.germinate.server.util.async;

/**
 * @author Sebastian Raubach
 */
public class BinningConfig
{
	public static final BinningConfig DEFAULT = new BinningConfig("equal", 10, 0, 0f);

	private String  binningMethod;
	private Integer binsLeft;
	private Integer binsRight;
	private Float   splitPoint;

	public BinningConfig()
	{
	}

	public BinningConfig(String binningMethod, Integer binsLeft, Integer binsRight, Float splitPoint)
	{
		this.binningMethod = binningMethod;
		this.binsLeft = binsLeft;
		this.binsRight = binsRight;
		this.splitPoint = splitPoint;
	}

	public String getBinningMethod()
	{
		return binningMethod;
	}

	public BinningConfig setBinningMethod(String binningMethod)
	{
		this.binningMethod = binningMethod;
		return this;
	}

	public Integer getBinsLeft()
	{
		return binsLeft;
	}

	public BinningConfig setBinsLeft(Integer binsLeft)
	{
		this.binsLeft = binsLeft;
		return this;
	}

	public Integer getBinsRight()
	{
		return binsRight;
	}

	public BinningConfig setBinsRight(Integer binsRight)
	{
		this.binsRight = binsRight;
		return this;
	}

	public Float getSplitPoint()
	{
		return splitPoint;
	}

	public BinningConfig setSplitPoint(Float splitPoint)
	{
		this.splitPoint = splitPoint;
		return this;
	}
}
