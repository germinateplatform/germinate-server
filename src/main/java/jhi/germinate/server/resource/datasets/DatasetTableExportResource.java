package jhi.germinate.server.resource.datasets;

import org.jooq.Field;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.Post;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.PaginatedServerResource;

import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTableExportResource extends PaginatedServerResource
{
	@Post("json")
	public FileRepresentation getJson(PaginatedRequest request)
	{
		processRequest(request);

		ExportSettings settings = new ExportSettings();
		settings.fieldsToNull = new Field[] { VIEW_TABLE_DATASETS.ACCEPTED_BY };
		return export(VIEW_TABLE_DATASETS, "dataset-table-", settings);
	}
}
