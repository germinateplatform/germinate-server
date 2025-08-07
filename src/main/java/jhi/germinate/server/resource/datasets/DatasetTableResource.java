package jhi.germinate.server.resource.datasets;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.DATASETPERMISSIONS;
import static jhi.germinate.server.database.codegen.tables.Datasettypes.DATASETTYPES;
import static jhi.germinate.server.database.codegen.tables.Licenselogs.LICENSELOGS;
import static jhi.germinate.server.database.codegen.tables.Usergroupmembers.USERGROUPMEMBERS;
import static jhi.germinate.server.database.codegen.tables.Usergroups.USERGROUPS;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.VIEW_TABLE_DATASETS;

/**
 * @author Sebastian Raubach
 */
@Path("dataset/table")
@Secured
@PermitAll
public class DatasetTableResource extends BaseDatasetTableResource
{
	public static List<String> getDatasetTypes()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(DATASETTYPES).fetchInto(Datasettypes.class).stream().map(Datasettypes::getDescription).collect(Collectors.toList());
		}
		catch (SQLException e)
		{
			Logger.getLogger("").info(e.getMessage());
			return new ArrayList<>();
		}
	}

	public static List<ViewTableDatasets> getDatasetsForUser(AuthenticationFilter.UserDetails userDetails, String datasetType)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record> from = context.select()
												 .from(VIEW_TABLE_DATASETS);

			if (!StringUtils.isEmpty(datasetType))
				from.where(VIEW_TABLE_DATASETS.DATASET_TYPE.eq(datasetType));

			if (userDetails != null && !userDetails.isAtLeast(UserType.ADMIN))
			{
				// Check if the dataset is public or if the user is part of a group that has access or if the user has access themselves
				from.where(VIEW_TABLE_DATASETS.DATASET_STATE.eq("public")
															.orExists(context.selectOne().from(DATASETPERMISSIONS)
																			 .leftJoin(USERGROUPS).on(USERGROUPS.ID.eq(DATASETPERMISSIONS.GROUP_ID))
																			 .leftJoin(USERGROUPMEMBERS).on(USERGROUPMEMBERS.USERGROUP_ID.eq(USERGROUPS.ID))
																			 .where(DATASETPERMISSIONS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID))
																			 .and(USERGROUPMEMBERS.USER_ID.eq(userDetails.getId())
																										  .or(DATASETPERMISSIONS.USER_ID.eq(userDetails.getId())))));
			}

			return from.fetchInto(ViewTableDatasets.class);
		}
	}

	public static ViewTableDatasets getDatasetForId(Integer datasetId, HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, boolean checkIfLicenseAccepted)
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			SelectJoinStep<Record> from = context.select()
												 .from(VIEW_TABLE_DATASETS);

			if (!userDetails.isAtLeast(UserType.ADMIN))
			{
				// Check if the dataset is public or if the user is part of a group that has access or if the user has access themselves
				from.where(VIEW_TABLE_DATASETS.DATASET_STATE.eq("public")
															.orExists(context.selectOne().from(DATASETPERMISSIONS)
																			 .leftJoin(USERGROUPS).on(USERGROUPS.ID.eq(DATASETPERMISSIONS.GROUP_ID))
																			 .leftJoin(USERGROUPMEMBERS).on(USERGROUPMEMBERS.USERGROUP_ID.eq(USERGROUPS.ID))
																			 .where(DATASETPERMISSIONS.DATASET_ID.eq(VIEW_TABLE_DATASETS.DATASET_ID))
																			 .and(USERGROUPMEMBERS.USER_ID.eq(userDetails.getId())
																										  .or(DATASETPERMISSIONS.USER_ID.eq(userDetails.getId())))));
			}

			ViewTableDatasets dataset = from.where(VIEW_TABLE_DATASETS.DATASET_ID.eq(datasetId))
											.fetchAnyInto(ViewTableDatasets.class);

			if (dataset == null)
			{
				return null;
			}
			else
			{
				if (checkIfLicenseAccepted)
				{
					Set<Integer> licenseIds = new HashSet<>();
					AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
					if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
						licenseIds = context.select(LICENSELOGS.LICENSE_ID).from(LICENSELOGS).where(LICENSELOGS.USER_ID.eq(userDetails.getId())).fetchSet(LICENSELOGS.LICENSE_ID);
					else
						licenseIds = AuthenticationFilter.getAcceptedLicenses(req);
					List<ViewTableDatasets> ds = restrictBasedOnLicenseAgreement(Collections.singletonList(dataset), licenseIds, userDetails);

					return CollectionUtils.isEmpty(ds) ? null : ds.get(0);
				}
				else
				{
					return dataset;
				}
			}
		}
	}

	public static List<ViewTableDatasets> restrictBasedOnLicenseAgreement(List<ViewTableDatasets> datasets, Set<Integer> acceptedLicenses, AuthenticationFilter.UserDetails userDetails)
	{
		return new ArrayList<>(datasets.stream()
									   .filter(ds -> ds.getLicenseId() == null || acceptedLicenses.contains(ds.getLicenseId()))
									   .map(ds -> ds.setAcceptedBy(new Integer[]{userDetails.getId()}))
									   .toList());
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<ViewTableDatasets>> postDatasetsTable(UnacceptedLicenseRequest request)
			throws SQLException
	{
		AdjustQuery adjuster = null;

		if (request != null && request.getJustUnacceptedLicenses() != null && request.getJustUnacceptedLicenses())
		{
			AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) securityContext.getUserPrincipal();
			Set<Integer> ids = AuthenticationFilter.getAcceptedLicenses(req);
			adjuster = query -> {
				query.where(VIEW_TABLE_DATASETS.LICENSE_ID.isNotNull())
					 .and(DSL.notExists(DSL.selectOne()
										   .from(LICENSELOGS)
										   .where(LICENSELOGS.LICENSE_ID.eq(VIEW_TABLE_DATASETS.LICENSE_ID))
										   .and(LICENSELOGS.USER_ID.eq(userDetails.getId()))));

				if (!CollectionUtils.isEmpty(ids) && userDetails.getId() == -1000)
				{
					query.where(DSL.not(VIEW_TABLE_DATASETS.LICENSE_ID.in(ids)));
				}
			};
		}

		return runQuery(request, adjuster);
	}

	@POST
	@Path("/ids")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postDatasetTableIds(PaginatedRequest request)
			throws SQLException
	{
		return runQuery(request);
	}
}