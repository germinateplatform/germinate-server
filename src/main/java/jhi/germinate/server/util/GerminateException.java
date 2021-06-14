package jhi.germinate.server.util;

import javax.ws.rs.core.Response;

public class GerminateException extends Exception
{
	private Response.Status status;

	public GerminateException(String message)
	{
		super(message);
		this.status = Response.Status.INTERNAL_SERVER_ERROR;
	}

	public GerminateException(Response.Status status)
	{
		super(status.getReasonPhrase());
		this.status = status;
	}

	public GerminateException(Response.Status status, String message)
	{
		super(message);
		this.status = status;
	}

	public Response.Status getStatus()
	{
		return status;
	}
}
