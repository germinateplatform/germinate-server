package jhi.germinate.server.resource.germplasm;

import org.jooq.*;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.Database;
import jhi.germinate.server.resource.*;

import static jhi.germinate.server.database.tables.ViewTableGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmTableExportResource extends PaginatedServerResource implements FilteredResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;

		FileRepresentation representation;
		try
		{
			File file = createTempFile("germplasm-table-", ".tsv");
			try (Connection conn = Database.getConnection();
				 DSLContext context = Database.getContext(conn);
				 PrintWriter bw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))))
			{
				SelectJoinStep<Record> from = context.select()
													 .from(VIEW_TABLE_GERMPLASM);

				// Filter here!
				filter(from, filters);

				exportToFile(bw, setPaginationAndOrderBy(from).fetch(), true);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
			}

			representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
			representation.setSize(file.length());
			representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
			// Remember to delete this after the call, we don't need it anymore
			representation.setAutoDeleting(true);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}

		return representation;
	}
}
