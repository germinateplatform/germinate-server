package jhi.germinate.server.resource.attributes;

import jakarta.servlet.http.HttpServletResponse;
import jhi.germinate.resource.*;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.Secured;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.io.*;
import java.nio.file.Files;
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
	public StreamingOutput postDatasetAttributeExport(ExportRequest request, @Context HttpServletResponse response)
		throws IOException, SQLException
	{
		processRequest(request);
		return toStreamingResult(export(VIEW_TABLE_GERMPLASM_ATTRIBUTES, "germplasm-attributes-table-", null), "application/zip", response);
	}
}
