package jhi.germinate.server.resource.germplasm;

import org.jooq.DSLContext;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.Date;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.routines.ExportPassportData;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.*;

import static jhi.germinate.server.database.tables.ViewTableGermplasm.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation postJson(GermplasmExportRequest request)
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			if (request != null && request.getIncludeAttributes() != null && request.getIncludeAttributes())
			{
				String individualIdString = CollectionUtils.join(request.getIndividualIds(), ",");
				String groupIdString = CollectionUtils.join(request.getGroupIds(), ",");

				ExportPassportData procedure = new ExportPassportData();

				if (!StringUtils.isEmpty(individualIdString))
					procedure.setGermplasmids(individualIdString);
				if (!StringUtils.isEmpty(groupIdString))
					procedure.setGroupids(groupIdString);

				procedure.execute(context.configuration());

				return export(procedure.getResults().get(0), "germplasm-table-" + getFormatted(new Date()) + "-");
			}
			else
			{
				if (request != null)
				{
					if (request.getIndividualIds() != null)
					{
						String[] ids = Arrays.stream(request.getIndividualIds())
											 .map(i -> Integer.toString(i))
											 .toArray(String[]::new);

						Filter[] filters = new Filter[1];
						filters[0] = new Filter(VIEW_TABLE_GERMPLASM.GERMPLASM_ID.getName(), "inSet", "and", ids);
						request.setFilter(filters);

						processRequest(request);
						return export(VIEW_TABLE_GERMPLASM, "germplasm-table-", null);
					}
					else if (request.getGroupIds() != null)
					{
						String[] ids = Arrays.stream(request.getGroupIds())
											 .map(i -> Integer.toString(i))
											 .toArray(String[]::new);

						Filter[] filters = new Filter[1];
						filters[0] = new Filter("groupId", "inSet", "and", ids);
						request.setFilter(filters);

						processRequest(request);
						return export(VIEW_TABLE_GERMPLASM, "germplasm-table-" + getFormatted(new Date()) + "-", null);
					}
				}

				// We get here if nothing specific was specified
				processRequest(request);
				return export(VIEW_TABLE_GERMPLASM, "germplasm-table-" + getFormatted(new Date()) + "-", null);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
