package jhi.germinate.server.resource.datasets;

import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.Secured;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.sql.SQLException;

@Path("dataset/{datasetId}/download-source")
@Secured
@PermitAll
public class DatasetSourceDownloadResource extends ContextResource
{
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("*/*")
	public Response getDatasetSourceDownload(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException
	{
		if (datasetId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, resp, userDetails, true);

		if (dataset == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		File file;
		String type;

		switch (dataset.getDatasetType())
		{
			case "allelefreq":
				file = ResourceUtils.getFromExternal(dataset.getSourceFile(), "data", "allelefreq");
				type = "text/plain";
				break;
			case "genotype":
				file = ResourceUtils.getFromExternal(dataset.getSourceFile(), "data", "genotypes");
				type = "application/x-hdf5";
				break;
			default:
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return null;
		}

		if (!file.exists() || !file.isFile())
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		// Prevent caching
		CacheControl cc = new CacheControl();
		cc.setNoCache(true);
		cc.setMaxAge(0);
		return Response.ok(file)
					   .type(type)
					   .cacheControl(cc)
					   .header("content-disposition", "attachment;filename= \"" + file.getName() + "\"")
					   .header("content-length", file.length())
					   .build();
	}
}
