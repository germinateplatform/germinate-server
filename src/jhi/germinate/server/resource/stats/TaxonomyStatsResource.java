package jhi.germinate.server.resource.stats;

import org.jooq.Result;
import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import jhi.germinate.server.Database;
import jhi.germinate.server.resource.BaseServerResource;

import static jhi.germinate.server.database.tables.ViewStatsTaxonomy.*;

/**
 * @author Sebastian Raubach
 */
public class TaxonomyStatsResource extends BaseServerResource
{
	@Get
	public FileRepresentation getJson()
	{
		FileRepresentation representation;
		try
		{
			File file = createTempFile("taxonomy", ".tsv");

			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				Result<? extends Record> result = context.selectFrom(VIEW_STATS_TAXONOMY)
														 .fetch();
				exportToFile(bw, result, true, null);
			}
			catch (SQLException | IOException e)
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
