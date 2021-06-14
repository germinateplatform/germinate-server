package jhi.germinate.server.resource.importers;

import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.DataImportJobs;
import jhi.germinate.server.database.codegen.tables.records.DataImportJobsRecord;
import jhi.germinate.server.resource.ContextResource;
import jhi.germinate.server.resource.datasets.export.AsyncResource;
import jhi.germinate.server.util.*;
import org.jooq.*;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.DataImportJobs.*;

@Path("import/template")
@Secured
public class ImportJobResource extends ContextResource implements AsyncResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured
	@PermitAll
	public List<DataImportJobs> postImportJob(UuidRequest request)
		throws SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (CollectionUtils.isEmpty(request.getUuids()) && (userDetails.getId() == -1000))
			return new ArrayList<>();

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectConditionStep<?> step = context.selectFrom(DATA_IMPORT_JOBS)
												 .where(DATA_IMPORT_JOBS.VISIBILITY.eq(true));

			if (userDetails.getId() != -1000)
				step.and(DATA_IMPORT_JOBS.USER_ID.eq(userDetails.getId()));
			else
				step.and(DATA_IMPORT_JOBS.UUID.in(request.getUuids()));

			return step.orderBy(DATA_IMPORT_JOBS.UPDATED_ON.desc())
					   .fetchInto(DataImportJobs.class);
		}
	}

	@DELETE
	@Path("/{jobUuid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public boolean deleteImportJob(@PathParam("jobUuid") String jobUuid)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		if (StringUtils.isEmpty(jobUuid))
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return false;
		}

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			DataImportJobsRecord record = context.selectFrom(DATA_IMPORT_JOBS)
												 .where(DATA_IMPORT_JOBS.UUID.in(jobUuid))
												 .fetchAnyInto(DataImportJobsRecord.class);

			boolean isCancelRequest = record.getStatus() == DataImportJobsStatus.running;

			// If the user is logged in
			if (userDetails.getId() != -1000)
			{
				if (Objects.equals(record.getUserId(), userDetails.getId()))
				{
					if (isCancelRequest)
					{
						record.setStatus(DataImportJobsStatus.cancelled);
						cancelJob(record.getUuid(), record.getJobId());
					}
					record.setVisibility(false);
					record.store();
					return true;
				}
				else
				{
					resp.sendError(Response.Status.FORBIDDEN.getStatusCode());
					return false;
				}
			}
			else
			{
				if (isCancelRequest)
				{
					record.setStatus(DataImportJobsStatus.cancelled);
					cancelJob(record.getUuid(), record.getJobId());
				}
				record.setVisibility(false);
				record.store();
				return true;
			}
		}
	}

	@GET
	@Path("/{jobUuid}/import")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Secured({UserType.DATA_CURATOR})
	public List<AsyncExportResult> getImportJob(@PathParam("jobUuid") String jobUuid)
		throws SQLException, IOException
	{
		if (StringUtils.isEmpty(jobUuid))
			return new ArrayList<>();

		try
		{
			return new DataImportRunner().importData(jobUuid);
		}
		catch (GerminateException e)
		{
			e.printStackTrace();
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}

	@POST
	@Path("/file")
	@Secured({UserType.DATA_CURATOR})
	public List<AsyncExportResult> accept(@QueryParam("update") Boolean isUpdate, @QueryParam("type") String type)
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
			return DataImportRunner.checkData(dataType, userDetails, req, isUpdate);
		}
		catch (GerminateException e)
		{
			resp.sendError(e.getStatus().getStatusCode(), e.getMessage());
			return null;
		}
	}
}
