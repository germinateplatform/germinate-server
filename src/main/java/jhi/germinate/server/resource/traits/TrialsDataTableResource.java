package jhi.germinate.server.resource.traits;

import jakarta.annotation.security.PermitAll;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.Groups;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static jhi.germinate.server.database.codegen.tables.Groupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Groups.*;


@Path("dataset/data/trial/table")
@Secured
@PermitAll
public class TrialsDataTableResource extends TrialsDataBaseResource
{
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableTrialsData>> postTrialsDataTable(SubsettedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "trials");
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
			SelectJoinStep<?> from = getTrialsDataQueryWrapped(context, minimal, null);

			from.where(DSL.field(TrialsDataBaseResource.DATASET_ID, Integer.class).in(requestedIds));

			Field<Integer> germplasmId = GROUPMEMBERS.FOREIGN_ID.as("germplasmId");
			Map<Integer, GermplasmGroups> germplasmGroups = new HashMap<>();
			context.select(
					   germplasmId,
					   DSL.jsonArrayAgg(DSL.jsonObject(DSL.key("id").value(GROUPS.ID), DSL.key("name").value(GROUPS.NAME))).as("groups")
				   )
				   .from(GROUPS)
				   .leftJoin(GROUPMEMBERS).on(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))
				   .where(GROUPS.GROUPTYPE_ID.eq(3)).and(GROUPS.VISIBILITY.eq(true).or(GROUPS.CREATED_BY.eq(userDetails.getId())))
				   .groupBy(germplasmId)
				   .forEach(r -> {
					   germplasmGroups.put(r.get(germplasmId), r.into(GermplasmGroups.class));
				   });

			// Handle requested germplasm ids or group ids
			Set<Integer> germplasmIds = new HashSet<>();
			if (!CollectionUtils.isEmpty(request.getyGroupIds()))
				germplasmIds.addAll(context.select(GROUPMEMBERS.FOREIGN_ID).from(GROUPMEMBERS).leftJoin(GROUPS).on(GROUPS.GROUPTYPE_ID.eq(3).and(GROUPS.ID.eq(GROUPMEMBERS.GROUP_ID))).where(GROUPS.ID.in(request.getyGroupIds())).fetchInto(Integer.class));
			if (!CollectionUtils.isEmpty(request.getyIds()))
				germplasmIds.addAll(Arrays.asList(request.getyIds()));
			if (!CollectionUtils.isEmpty(germplasmIds))
				from.where(DSL.field(TrialsDataBaseResource.GERMPLASM_ID, Integer.class).in(germplasmIds));
			if (!CollectionUtils.isEmpty(request.getxIds()))
				from.where(DSL.field(TrialsDataBaseResource.TRAIT_ID, Integer.class).in(request.getxIds()));

			// Filter here!
			filter(from, filters);

			List<ViewTableTrialsData> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableTrialsData.class);

			result.forEach(r -> {
				if (germplasmGroups.containsKey(r.getGermplasmId()))
				{
					r.setGroups(germplasmGroups.get(r.getGermplasmId()).getGroups());
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
	public PaginatedResult<List<Integer>> postTrialsDataTableIds(PaginatedDatasetRequest request)
		throws IOException, SQLException
	{
		if (request == null)
		{
			resp.sendError(Response.Status.BAD_REQUEST.getStatusCode());
			return null;
		}

		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "trials");
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
			SelectJoinStep<Record1<Integer>> from = getTrialsDataIdQueryWrapped(context, minimal, null);

			from.where(DSL.field(TrialsDataBaseResource.DATASET_ID, Integer.class).in(requestedIds));

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
	public Response postTrialsDataTableExport(PaginatedDatasetRequest request)
		throws IOException, SQLException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();

		List<Integer> datasets = DatasetTableResource.getDatasetIdsForUser(req, userDetails, "trials");
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

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<?> from = getTrialsDataQueryWrapped(context, minimal, null);

			from.where(DSL.field(TrialsDataBaseResource.DATASET_ID, Integer.class).in(requestedIds));

			// Filter here!
			filter(from, filters);

			return ResourceUtils.exportToZip(from.fetch(), resp, "trials-data-table-");
		}
	}

	private static class GermplasmGroups
	{
		private Integer      germplasmId;
		private List<Groups> groups;

		public Integer getGermplasmId()
		{
			return germplasmId;
		}

		public GermplasmGroups setGermplasmId(Integer germplasmId)
		{
			this.germplasmId = germplasmId;
			return this;
		}

		public List<Groups> getGroups()
		{
			return groups;
		}

		public GermplasmGroups setGroups(List<Groups> groups)
		{
			this.groups = groups;
			return this;
		}
	}
}
