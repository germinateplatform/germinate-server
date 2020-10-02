package jhi.germinate.server.resource.datasets;

import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;
import java.util.List;

import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;

/**
 * @author Sebastian Raubach
 */
public class DatasetSourceDownloadResource extends BaseServerResource
{
	private Integer datasetId;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.datasetId = Integer.parseInt(getRequestAttributes().get("datasetId").toString());
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get
	public FileRepresentation getJson()
	{
		if (datasetId == null)
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);

		List<ViewTableDatasets> datasets = DatasetTableResource.getDatasetForId(datasetId, getRequest(), getResponse(), true);

		if (CollectionUtils.isEmpty(datasets))
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		File file;
		MediaType type;

		ViewTableDatasets dataset = datasets.get(0);
		switch (dataset.getDatasetType()) {
			case "allelefreq":
				 file = getFromExternal(dataset.getSourceFile(), "data", "allelefreq");
				 type = MediaType.TEXT_PLAIN;
				 break;
			case "genotype":
				file = getFromExternal(dataset.getSourceFile(), "data", "genotypes");
				type = MediaType.register("application/x-hdf5", "HDF5");
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}

		if (!file.exists() || !file.isFile())
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);

		// Prevent caching
		getResponse().setAccessControlMaxAge(0);

		// Send the file
		FileRepresentation representation = new FileRepresentation(file, type);
		representation.setSize(file.length());
		Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT);
		disp.setFilename(file.getName());
		disp.setSize(file.length());
		representation.setDisposition(disp);
		return representation;
	}
}
