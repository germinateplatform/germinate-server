package jhi.germinate.server.resource.fileresource;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.*;

public class MediaStreamer implements StreamingOutput
{
	private int              length;
	private RandomAccessFile raf;
	final   byte[]           buf = new byte[4096];

	public MediaStreamer(int length, RandomAccessFile raf)
	{
		this.length = length;
		this.raf = raf;
	}

	@Override
	public void write(OutputStream outputStream)
		throws IOException, WebApplicationException
	{
		try
		{
			while (length != 0)
			{
				try
				{
					int read = raf.read(buf, 0, Math.min(buf.length, length));
					outputStream.write(buf, 0, read);
					length -= read;
				}
				catch (IOException e)
				{
					// Silently fail
				}
			}
		}
		finally
		{
			raf.close();
		}
	}

	public int getLenth()
	{
		return length;
	}
}