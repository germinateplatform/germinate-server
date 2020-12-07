package jhi.germinate.server.resource.markers;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.auth.CustomVerifier;
import jhi.germinate.server.resource.PaginatedServerResource;
import org.jooq.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import static jhi.germinate.server.database.codegen.tables.ViewTableMapdefinitions.*;

/**
 * @author Sebastian Raubach
 */
public class MapMarkerDefinitionTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());
		processRequest(request);

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_MAPDEFINITIONS.VISIBILITY.eq(true).or(VIEW_TABLE_MAPDEFINITIONS.USER_ID.eq(userDetails.getId()))};
		settings.fieldsToNull = new Field[]{VIEW_TABLE_MAPDEFINITIONS.USER_ID, VIEW_TABLE_MAPDEFINITIONS.VISIBILITY};
		return export(VIEW_TABLE_MAPDEFINITIONS, "map-definition-table-", settings);
	}
}
