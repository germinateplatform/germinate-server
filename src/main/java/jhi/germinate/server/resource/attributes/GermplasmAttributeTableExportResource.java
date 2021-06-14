package jhi.germinate.server.resource.attributes;

import jhi.germinate.resource.PaginatedRequest;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.Secured;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.SQLException;

import static jhi.germinate.server.database.codegen.tables.ViewTableGermplasmAttributes.*;

@Path("germplasm/attribute/export")
@Secured
@PermitAll
public class GermplasmAttributeTableExportResource extends ExportResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public Response postDatasetAttributeExport(PaginatedRequest request)
		throws IOException, SQLException
	{
		processRequest(request);
		return export(VIEW_TABLE_GERMPLASM_ATTRIBUTES, "germplasm-attributes-table-", null);
	}
}
