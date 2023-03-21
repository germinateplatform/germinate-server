package jhi.germinate.server.resource.importers;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.AsyncExportResult;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.AuthenticationFilter;
import jhi.germinate.server.database.codegen.enums.DataImportJobsDatatype;
import jhi.germinate.server.database.pojo.DataOrientation;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.glassfish.jersey.media.multipart.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@Path("import/template/file")
@Secured
@MultipartConfig
public class ImportJobUploadResource extends ContextResource
{
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public List<AsyncExportResult> accept(@QueryParam("update") Boolean isUpdate, @QueryParam("dataOrientation") @DefaultValue("GENOTYPE_GERMPLASM_BY_MARKER") DataOrientation dataOrientation, @QueryParam("type") String type, @QueryParam("datasetId") Integer datasetId, @QueryParam("datasetStateId") Integer datasetStateId, @FormDataParam("fileToUpload") InputStream fileIs, @FormDataParam("fileToUpload") FormDataContentDisposition fileDetails)
		throws IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		DataImportJobsDatatype dataType;

		try
		{
			dataType = DataImportJobsDatatype.valueOf(type);
		}
		catch (Exception e)
		{
			dataType = null;
		}

		try
		{
			String uuid = UUID.randomUUID().toString();
			// Get the target folder for all generated files
			File asyncFolder = ResourceUtils.getFromExternal(null, uuid, "async");
			asyncFolder.mkdirs();
			String itemName = fileDetails.getFileName();
			String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
			File targetFile = new File(asyncFolder, uuid + "." + extension);

			if (!FileUtils.isSubDirectory(asyncFolder, targetFile)) {
				resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
				return null;
			}

			Files.copy(fileIs, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

			return DataImportRunner.checkData(dataType, userDetails, uuid, targetFile, itemName, isUpdate, dataOrientation, datasetId, datasetStateId);
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}
}
