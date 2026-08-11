package jhi.germinate.server.util;

public class StatusException extends RuntimeException
{
	private int statusCode;
	private Object entity;

	public StatusException(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public StatusException(int statusCode, Object entity) {
		this(statusCode);
		this.entity = entity;
	}

	public StatusException(int statusCode, String message)
	{
		super(message);
		this.statusCode = statusCode;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public Object getEntity()
	{
		return entity;
	}
}
