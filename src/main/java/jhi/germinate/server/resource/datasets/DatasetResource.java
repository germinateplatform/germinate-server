package jhi.germinate.server.resource.datasets;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.DatasetsRecord;
import jhi.germinate.server.resource.*;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.io.*;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Climatedata.*;
import static jhi.germinate.server.database.codegen.tables.Datasetmembers.*;
import static jhi.germinate.server.database.codegen.tables.Datasets.*;
import static jhi.germinate.server.database.codegen.tables.Phenotypedata.*;

@Path("dataset")
@Secured(UserType.DATA_CURATOR)
public class DatasetResource extends ContextResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchDatasetById(Datasets newDataset)
		throws SQLException, IOException
	{
		if (newDataset == null || StringUtils.isEmpty(newDataset.getName()) || newDataset.getExperimentId() == null || newDataset.getDatasettypeId() == null || newDataset.getDatasetStateId() == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			DatasetsRecord dataset = context.newRecord(DATASETS, newDataset);
			dataset.setCreatedBy(userDetails.getId());
			return dataset.store() > 0;
		}
	}

	@PATCH
	@Path("/{datasetId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean patchDatasetById(@PathParam("datasetId") Integer datasetId, Datasets updatedDataset)
		throws SQLException, IOException
	{
		if (updatedDataset == null || StringUtils.isEmpty(updatedDataset.getName()))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetId, req, userDetails, false);

		if (ds == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return false;
		}
		else
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				DatasetsRecord dataset = context.selectFrom(DATASETS).where(DATASETS.ID.eq(ds.getDatasetId())).fetchAny();

				if (dataset == null)
				{
					resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
					return false;
				}

				dataset.setName(updatedDataset.getName());
				dataset.setDescription(updatedDataset.getDescription());
				dataset.setLicenseId(updatedDataset.getLicenseId());
				dataset.setExperimentId(updatedDataset.getExperimentId());
				dataset.setDateStart(updatedDataset.getDateStart());
				dataset.setDateEnd(updatedDataset.getDateEnd());
				dataset.setDatasetStateId(updatedDataset.getDatasetStateId());
				return dataset.store() > 0;
			}
		}
	}

	@DELETE
	@Path("/{datasetId:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public boolean deleteDatasetById(@PathParam("datasetId") Integer datasetId)
		throws IOException, SQLException
	{
		if (datasetId == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		ViewTableDatasets ds = DatasetTableResource.getDatasetForId(datasetId, req, userDetails, false);

		if (ds == null)
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return false;
		}
		else
		{
			try (Connection conn = Database.getConnection())
			{
				DSLContext context = Database.getContext(conn);

				context.deleteFrom(DATASETS).where(DATASETS.ID.eq(datasetId)).execute();

				ResourceUtils.resetAutoincrement(context, DATASETMEMBERS);

				// Get the source file
				File sourceFile = null;

				switch (ds.getDatasetType())
				{
					case "genotype":
						if (ds.getSourceFile() != null)
						{
							sourceFile = ResourceUtils.getFromExternal(resp, ds.getSourceFile(), "data", "genotypes");
							File transposed = new File(sourceFile.getParentFile(), "transposed-" + sourceFile.getName());

							if (transposed.exists() && transposed.isFile())
								transposed.delete();
						}
						break;
					case "allelefreq":
						if (ds.getSourceFile() != null)
						{
							sourceFile = ResourceUtils.getFromExternal(resp, ds.getSourceFile(), "data", "allelefreq");
						}
						break;
					case "climate":
						ResourceUtils.resetAutoincrement(context, CLIMATEDATA);
						break;
					case "trials":
						ResourceUtils.resetAutoincrement(context, PHENOTYPEDATA);
						break;
				}

				if (sourceFile != null && sourceFile.exists() && sourceFile.isFile())
					sourceFile.delete();

				return true;
			}
		}
	}
}
