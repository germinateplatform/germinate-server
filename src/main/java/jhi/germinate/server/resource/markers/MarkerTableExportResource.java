package jhi.germinate.server.resource.markers;

import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.ViewTableMarkers.*;

/**
 * @author Sebastian Raubach
 */
public class MarkerTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		return export(VIEW_TABLE_MARKERS, "marker-table-", null);
	}
}
