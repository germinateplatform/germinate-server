package jhi.germinate.server.resource.datasets;

import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.sql.SQLException;

@Path("dataset/{datasetId}/download-source")
@Secured
@PermitAll
public class DatasetSourceDownloadResource extends ContextResource
{
	@GET
	@Produces("*/*")
	public Response getDatasetSourceDownload(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException, StatusException
	{
		if (datasetId == null)
			return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
		ViewTableDatasets dataset = DatasetTableResource.getDatasetForId(datasetId, req, userDetails, true);

		if (dataset == null)
			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();

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
				return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
		}

		if (!file.exists() || !file.isFile())
			return Response.status(Response.Status.NOT_FOUND.getStatusCode()).build();

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
