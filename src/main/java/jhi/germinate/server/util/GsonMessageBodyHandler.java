package jhi.germinate.server.util;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonMessageBodyHandler implements MessageBodyWriter<Object>,
	MessageBodyReader<Object>
{

	private static final String UTF_8 = "UTF-8";

	@Override
	public boolean isReadable(Class<?> type, Type genericType,
							  java.lang.annotation.Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType,
						   Annotation[] annotations, MediaType mediaType,
						   MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
		throws IOException
	{
		InputStreamReader streamReader = new InputStreamReader(entityStream,
			UTF_8);
		try
		{
			return GsonUtil.getInstance().fromJson(streamReader, genericType);
		}
		catch (com.google.gson.JsonSyntaxException e)
		{
			// Log exception
		}
		finally
		{
			streamReader.close();
		}
		return null;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType,
							   Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public long getSize(Object object, Class<?> type, Type genericType,
						Annotation[] annotations, MediaType mediaType)
	{
		return -1;
	}

	@Override
	public void writeTo(Object object, Class<?> type, Type genericType,
						Annotation[] annotations, MediaType mediaType,
						MultivaluedMap<String, Object> httpHeaders,
						OutputStream entityStream)
		throws IOException,
		WebApplicationException
	{
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);
		try
		{
			GsonUtil.getInstance().toJson(object, genericType, writer);
		}
		finally
		{
			writer.close();
		}
	}
}