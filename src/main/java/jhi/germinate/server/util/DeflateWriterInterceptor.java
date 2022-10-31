package jhi.germinate.server.util;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.*;

import java.io.*;
import java.util.zip.DeflaterOutputStream;

@Provider
@CompressDeflate
public class DeflateWriterInterceptor implements WriterInterceptor
{
	@Override
	public void aroundWriteTo(WriterInterceptorContext context)
		throws IOException, WebApplicationException
	{
		MultivaluedMap<String, Object> headers = context.getHeaders();
		headers.add("Content-Encoding", "deflate");

		final OutputStream outputStream = context.getOutputStream();
		context.setOutputStream(new DeflaterOutputStream(outputStream));
		context.proceed();
	}
}
