package jhi.germinate.server.util;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

@Provider
@CompressGzip
public class GZipWriterInterceptor implements WriterInterceptor
{

	@Override
	public void aroundWriteTo(WriterInterceptorContext context)
		throws IOException, WebApplicationException
	{

		MultivaluedMap<String,Object> headers = context.getHeaders();
		headers.add("Content-Encoding", "gzip");

		final OutputStream outputStream = context.getOutputStream();
		context.setOutputStream(new GZIPOutputStream(outputStream));
		context.proceed();
	}
}
