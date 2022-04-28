package jhi.germinate.server.util;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.*;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public final class GsonMessageBodyHandler implements MessageBodyWriter<Object>,
	MessageBodyReader<Object>
{
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
	{
		return true;
	}

	@Override
	public Object readFrom(Class<Object> type, Type genericType,
						   Annotation[] annotations, MediaType mediaType,
						   MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
		throws IOException
	{
		InputStreamReader streamReader = new InputStreamReader(entityStream, StandardCharsets.UTF_8);
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
		OutputStreamWriter writer = new OutputStreamWriter(entityStream, StandardCharsets.UTF_8);
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