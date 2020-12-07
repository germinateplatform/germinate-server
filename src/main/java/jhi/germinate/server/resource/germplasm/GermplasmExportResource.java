package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;

/**
 * @author Sebastian Raubach
 */
public class GermplasmExportResource extends GermplasmBaseResource
{
	@Post("json")
	public FileRepresentation postJson(GermplasmExportRequest request)
	{
		try (DSLContext context = Database.getContext())
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

				return export(procedure.getResults().get(0), "germplasm-table-" + getFormattedDateTime(new Date()) + "-");
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
						filters[0] = new Filter(GERMPLASM_ID, "inSet", "and", ids);
						request.setFilter(filters);

						processRequest(request);

						SelectJoinStep<?> from = getGermplasmQuery(context);

						// Filter here!
						filter(from, adjustFilter(filters));

						return export(from.fetch(), "germplasm-table-" + getFormattedDateTime(new Date()) + "-");
					}
					else if (request.getGroupIds() != null)
					{
						processRequest(request);

						SelectConditionStep<?> from = getGermplasmQuery(context)
							.leftJoin(GROUPMEMBERS).on(GROUPMEMBERS.FOREIGN_ID.eq(GERMINATEBASE.ID))
							.where(GROUPMEMBERS.GROUP_ID.in(request.getGroupIds()));

						return export(from.fetch(), "germplasm-table-" + getFormattedDateTime(new Date()) + "-");
					}
				}

				// We get here if nothing specific was specified
				processRequest(request);
				return export(getGermplasmQuery(context).fetch(), "germplasm-table-" + getFormattedDateTime(new Date()) + "-");
			}
		}
	}
}
