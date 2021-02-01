package jhi.germinate.server.resource.germplasm;

import jhi.germinate.resource.*;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.routines.ExportPassportData;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;

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

				return export(procedure.getResults().get(0), "germplasm-table-");
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

						SelectJoinStep<?> from = getGermplasmQueryWrapped(context, null);

						// Filter here!
						filter(from, filters);

						return export(from.fetch(), "germplasm-table-");
					}
					else if (request.getGroupIds() != null)
					{
						processRequest(request);

						Field<Integer> fieldGroupId = DSL.field("group_id", Integer.class);
						List<Join<Integer>> joins = new ArrayList<>();
						joins.add(new Join<>(GROUPMEMBERS, GROUPMEMBERS.FOREIGN_ID, GERMINATEBASE.ID));
						SelectJoinStep<?> from = getGermplasmQueryWrapped(context, joins, fieldGroupId);
						from.where(fieldGroupId.in(request.getGroupIds()));

						return export(from.fetch(), "germplasm-table-");
					}
				}

				// We get here if nothing specific was specified
				processRequest(request);
				return export(getGermplasmQueryWrapped(context, null).fetch(), "germplasm-table-");
			}
		}
	}
}
