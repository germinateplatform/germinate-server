package jhi.germinate.server.resource.importer;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.ext.fileupload.RestletFileUpload;
import org.restlet.representation.Representation;
import org.restlet.resource.*;

import java.io.*;
import java.util.UUID;

import jhi.germinate.server.auth.*;
import jhi.germinate.server.util.importer.McpdImporter;

/**
 * @author Sebastian Raubach
 */
public class McpdImporterResource extends ServerResource
{
	@Post
	@MinUserType(UserType.AUTH_USER)
	public String accept(Representation entity)
	{
		if (entity != null)
		{
			if (MediaType.MULTIPART_FORM_DATA.equals(entity.getMediaType(), true))
			{
				// 1/ Create a factory for disk-based file items
				DiskFileItemFactory factory = new DiskFileItemFactory();
				factory.setSizeThreshold(1000240);

				// 2/ Create a new file upload handler based on the Restlet
				// FileUpload extension that will parse Restlet requests and
				// generates FileItems.
				RestletFileUpload upload = new RestletFileUpload(factory);

				try
				{
					// 3/ Request is parsed by the handler which generates a
					// list of FileItems
					FileItemIterator fileIterator = upload.getItemIterator(entity);

					// Process only the uploaded item called "fileToUpload"
					// and return back
					while (fileIterator.hasNext())
					{
						FileItemStream fi = fileIterator.next();
						if (fi.getFieldName().equals("fileToUpload"))
						{
							// consume the stream immediately, otherwise the stream
							// will be closed.
							File tempFile = File.createTempFile("germinate", UUID.randomUUID().toString() + ".xlsx");
							FileUtils.copyInputStreamToFile(fi.openStream(), tempFile);

							return new McpdImporter(tempFile).run();
						}
					}

					// If we get here, the file wasn't found
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
				}
				catch (IOException | FileUploadException e)
				{
					e.printStackTrace();
					throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
				}
			}
			else
			{
				// POST request with no entity.
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}
		else
		{
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
