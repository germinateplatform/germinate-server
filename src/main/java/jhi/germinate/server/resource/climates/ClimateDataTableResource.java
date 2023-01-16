package jhi.germinate.server.resource.climates;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.resource.ExportResource;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.resource.traits.*;
import jhi.germinate.server.util.*;
import org.jooq.*;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableClimateData.*;

@Path("dataset/data/climate/table")
@Secured
@PermitAll
public class ClimateDataTableResource extends ExportResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableClimateDataWithGroups>> postClimateDataTable(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "climate");
		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		// If nothing specific was requested, just return everything, else restrict to available datasets
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = datasets;
		else
			requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
			return new PaginatedResult<>(new ArrayList<>(), 0);

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
			if (!CollectionUtils.isEmpty(request.getyGroupIds()))
				locationIds.addAll(context.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.GROUPTYPE_ID.eq(1).and(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.ID.in(request.getyGroupIds())).fetchInto(Integer.class));
			if (!CollectionUtils.isEmpty(request.getyIds()))
				locationIds.addAll(Arrays.asList(request.getyIds()));
			if (!CollectionUtils.isEmpty(locationIds))
				from.where(VIEW_TABLE_CLIMATE_DATA.LOCATION_ID.in(locationIds));
			if (!CollectionUtils.isEmpty(request.getxIds()))
				from.where(VIEW_TABLE_CLIMATE_DATA.CLIMATE_ID.in(request.getxIds()));

			// Filter here!
			filter(from, filters);

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
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "climate");
		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		// If nothing specific was requested, just return everything, else restrict to available datasets
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = datasets;
		else
			requestedIds.retainAll(datasets);

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
			filter(from, filters);

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
	public Response postClimateDataTableExport(PaginatedDatasetRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "climate");
		List<Integer> requestedIds = request.getDatasetIds() == null ? null : new ArrayList<>(Arrays.asList(request.getDatasetIds()));

		// If nothing specific was requested, just return everything, else restrict to available datasets
		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = datasets;
		else
			requestedIds.retainAll(datasets);

		if (CollectionUtils.isEmpty(requestedIds))
		{
			resp.sendError(Response.Status.NOT_FOUND.getStatusCode());
			return null;
		}

		processRequest(request);

		ExportSettings settings = new ExportSettings();
		settings.conditions = new Condition[]{VIEW_TABLE_CLIMATE_DATA.DATASET_ID.in(requestedIds)};
		return export(VIEW_TABLE_CLIMATE_DATA, "climate-data-table-", settings);
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
