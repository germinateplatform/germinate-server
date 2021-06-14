package jhi.germinate.server.util;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.*;

public class FileUploadHandler
{
	public static List<String> handleMultiple(HttpServletRequest req, String key, File folder)
		throws GerminateException
	{
		//checks whether there is a file upload request or not
		if (ServletFileUpload.isMultipartContent(req))
		{
			final ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
			try
			{
				List<String> filenames = new ArrayList<>();

				final List<FileItem> items = fileUpload.parseRequest(req);

				if (!CollectionUtils.isEmpty(items))
				{
					for (FileItem item : items)
					{
						final String itemName = item.getName();

						if (!item.isFormField() && Objects.equals(item.getFieldName(), key))
						{
							String uuid = UUID.randomUUID().toString();
							String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
							item.write(new File(folder, uuid + "." + extension));
							filenames.add(uuid + "." + extension);
						}

					}
				}

				return filenames;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}
		else
		{
			throw new GerminateException(Response.Status.BAD_REQUEST);
		}
	}

	public static File handle(HttpServletRequest req, String key, File targetFile)
		throws GerminateException
	{
		final ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
		try
		{
			final List<FileItem> items = fileUpload.parseRequest(req);

			if (!CollectionUtils.isEmpty(items))
			{
				for (FileItem item : items)
				{
					final String itemName = item.getName();

					if (!item.isFormField() && Objects.equals(item.getFieldName(), key))
					{
						String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
						targetFile = new File(targetFile.getParentFile(), targetFile.getName() + "." + extension);
						item.write(targetFile);
						return targetFile;
					}
				}
			}

			throw new GerminateException(Response.Status.BAD_REQUEST);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}

	public static String handle(HttpServletRequest req, String key, File folder, String uuid)
		throws GerminateException
	{
		final ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
		try
		{
			final List<FileItem> items = fileUpload.parseRequest(req);

			if (!CollectionUtils.isEmpty(items))
			{
				for (FileItem item : items)
				{
					final String itemName = item.getName();

					if (!item.isFormField() && Objects.equals(item.getFieldName(), key))
					{
						String extension = itemName.substring(itemName.lastIndexOf(".") + 1);
						item.write(new File(folder, uuid + "." + extension));
						return itemName;
					}
				}
			}

			throw new GerminateException(Response.Status.BAD_REQUEST);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new GerminateException(Response.Status.INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
}
