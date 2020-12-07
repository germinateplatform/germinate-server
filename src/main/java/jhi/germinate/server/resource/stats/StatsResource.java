package jhi.germinate.server.resource.stats;

import jhi.germinate.server.Database;
import jhi.germinate.server.resource.BaseServerResource;
import org.jooq.*;
import org.jooq.impl.TableImpl;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.ResourceException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Sebastian Raubach
 */
public class StatsResource extends BaseServerResource
{
	protected FileRepresentation export(String filename, TableImpl<? extends Record> table)
	{
		FileRepresentation representation;
		try
		{
			File file = createTempFile(filename, ".tsv");

			try (DSLContext context = Database.getContext();
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				Result<? extends Record> result = context.selectFrom(table)
														 .fetch();
				exportToFile(bw, result, true, null);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
