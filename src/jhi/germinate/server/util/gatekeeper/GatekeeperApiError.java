package jhi.germinate.server.util.gatekeeper;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperApiError
{
	private int code;
	private String description;

	public GatekeeperApiError()
	{
	}

	public int getCode()
	{
		return code;
	}

	public GatekeeperApiError setCode(int code)
	{
		this.code = code;
		return this;
	}

	public String getDescription()
	{
		return description;
	}

	public GatekeeperApiError setDescription(String description)
	{
		this.description = description;
		return this;
	}
}
