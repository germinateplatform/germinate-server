package jhi.germinate.server.resource.maps.writer;

import org.jooq.Record;

import java.io.*;

import jhi.germinate.server.database.tables.pojos.Maps;

/**
 * @author Sebastian Raubach
 */
public abstract class AbstractMapWriter
{
	public static final String CRLF = "\r\n";

	protected BufferedWriter bw;

	public AbstractMapWriter(BufferedWriter bw)
	{
		this.bw = bw;
	}

	public abstract void writeHeader(Maps map)
		throws IOException;

	public abstract void writeRow(Record record)
		throws IOException;

	public abstract void writeFooter()
		throws IOException;
}
