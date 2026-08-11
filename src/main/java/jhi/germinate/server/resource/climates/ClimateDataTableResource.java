package jhi.germinate.server.resource.climates;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.core.Context;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Groups;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.GROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Groups.GROUPS;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimateData.VIEW_TABLE_CLIMATE_DATA;

@Path("dataset/data/climate/table")
@Secured
@PermitAll
public class ClimateDataTableResource extends ExportResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableClimateDataWithGroups>> postClimateDataTable(ClimateExportDatasetRequest request)
			throws IOException, SQLException
	{
		if (request == null)
			throw new BadRequestException();

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		requestedIds = AuthorizationFilter.restrictDatasetIds(req, userDetails, "climate", requestedIds, true);

		processRequest(request);
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_CLIMATE_DATA);

			from.where(VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds));

			Field<Integer> locationId = GROUPMEMBERS.FOREIGN_ID.as("locationId");
			Map<Integer, LocationGroups> locationGroups = new HashMap<>();
			context.select(
						   locationId,
						   DSL.jsonArrayAgg(DSL.jsonObject(DSL.key("id").value(GROUPS.ID), DSL.key("name").value(GROUPS.NAME))).as("groups")
				   )
			       .from(GROUPS)
			       .leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
			       .where(GROUPS.GROUPTYPE_ID.eq(1)).and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId())))
			       .groupBy(locationId)
			       .forEach(r -> {
					   locationGroups.put(r.get(locationId), r.into(LocationGroups.class));
				   });

			// Handle requested location ids or group ids
			Set<Integer> locationIds = new HashSet<>();
			if (!CollectionUtils.isEmpty(request.getLocationGroupIds()))
				locationIds.addAll(context.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.GROUPTYPE_ID.eq(1).and(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.ID.in(request.getLocationGroupIds())).fetchInto(Integer.class));
			if (!CollectionUtils.isEmpty(request.getLocationIds()))
				locationIds.addAll(Arrays.asList(request.getLocationIds()));
			if (!CollectionUtils.isEmpty(locationIds))
				from.where(VIEW_TABLE_CLIMATE_DATA.LOCATION_ID.in(locationIds));
			if (!CollectionUtils.isEmpty(request.getClimateIds()))
				from.where(VIEW_TABLE_CLIMATE_DATA.CLIMATE_ID.in(request.getClimateIds()));

			// Filter here!
			where(from, filters);

			List<ViewTableClimateDataWithGroups> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(ViewTableClimateDataWithGroups.class);

			result.forEach(r -> {
				if (locationGroups.containsKey(r.getLocationId()))
				{
					r.setGroups(locationGroups.get(r.getLocationId()).getGroups());
				}
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postClimateDataTableIds(PaginatedDatasetRequest request)
			throws SQLException
	{
		if (request == null)
			throw new BadRequestException();

		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", requestedIds, true);

		if (CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

		processRequest(request);
		currentPage = 0;
		pageSize = Integer.MAX_VALUE;
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record1<Integer>> from = context.selectDistinct(VIEW_TABLE_CLIMATE_DATA.LOCATION_ID)
			                                               .from(VIEW_TABLE_CLIMATE_DATA);

			from.where(VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds));

			// Filter here!
			where(from, filters);

			List<Integer> result = setPaginationAndOrderBy(from)
					.fetch()
					.into(Integer.class);

			return new PaginatedResult<>(result, result.size());
		}
	}

	@POST
	@Path("/export")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces("application/zip")
	public File postClimateDataTableExport(DatasetExportRequest request, @Context HttpServletResponse response)
			throws IOException, SQLException
	{
		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));
		requestedIds = AuthorizationFilter.restrictDatasetIds(req, (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal(), "climate", requestedIds, true);

		if (CollectionUtils.isEmpty(requestedIds))
			throw new NotFoundException();

		processRequest(request);

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds)};
		return toFileResult(export(VIEW_TABLE_CLIMATE_DATA, "climate-data-table-", settings), "application/zip", response);
	}

	private static class LocationGroups
	{
		private Integer      locationId;
		private List<Groups> groups;

		public Integer getLocationId()
		{
			return locationId;
		}

		public LocationGroups setLocationId(Integer locationId)
		{
			this.locationId = locationId;
			return this;
		}

		public List<Groups> getGroups()
		{
			return groups;
		}

		public LocationGroups setGroups(List<Groups> groups)
		{
			this.groups = groups;
			return this;
		}
	}
}
