package jhi.germinate.resource;

/**
 * @author Sebastian Raubach
 */
public class ResultFile
{
	private String name;
	private Long size;

	public String getName()
	{
		return name;
	}

	public ResultFile setName(String name)
	{
		this.name = name;
		return this;
	}

	public Long getSize()
	{
		return size;
	}

	public ResultFile setSize(Long size)
	{
		this.size = size;
		return this;
	}
}
