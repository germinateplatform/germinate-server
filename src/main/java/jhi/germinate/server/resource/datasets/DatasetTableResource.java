package jhi.germinate.server.resource.datasets;

import jakarta.ws.rs.Path;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.util.*;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;

import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Datasetpermissions.*;
import static jhi.germinate.server.database.codegen.tables.Datasettypes.DATASETTYPES;
import static jhi.germinate.server.database.codegen.tables.Licenselogs.*;
import static jhi.germinate.server.database.codegen.tables.Usergroupmembers.*;
import static jhi.germinate.server.database.codegen.tables.Usergroups.*;
import static jhi.germinate.server.database.codegen.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
@Path("dataset/table")
@Secured
@PermitAll
public class DatasetTableResource extends BaseDatasetTableResource
{
	public static List<String> getDatasetTypes()
			throws SQLException
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			return context.selectFrom(DATASETTYPES).fetchInto(Datasettypes.class).stream().map(Datasettypes::getDescription).collect(Collectors.toList());
		}
	}

	public static List<Integer> getDatasetIdsForUser(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String datasetType, boolean checkIfLicenseAccepted)
			throws SQLException
	{
		return getDatasetsForUser(req, userDetails, datasetType, checkIfLicenseAccepted).stream()
																						.map(ViewTableDatasets::getDatasetId)
																						.collect(Collectors.toList());
	}

	public static List<ViewTableDatasets> getDatasetsForUser(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String datasetType)
			throws SQLException
	{
		return getDatasetsForUser(req, userDetails, datasetType, true);
	}

	public static List<ViewTableDatasets> getDatasetsForUser(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String datasetType, boolean checkIfLicenseAccepted)
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

			if (checkIfLicenseAccepted)
				return restrictBasedOnLicenseAgreement(from.fetchInto(ViewTableDatasets.class), req, userDetails);
			else
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
					List<ViewTableDatasets> ds = restrictBasedOnLicenseAgreement(Collections.singletonList(dataset), req, userDetails);

					return CollectionUtils.isEmpty(ds) ? null : ds.get(0);
				}
				else
				{
					return dataset;
				}
			}
		}
	}

	private static List<ViewTableDatasets> restrictBasedOnLicenseAgreement(List<ViewTableDatasets> datasets, HttpServletRequest request, AuthenticationFilter.UserDetails userDetails)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		Set<Integer> acceptedLicenses = AuthenticationFilter.getAcceptedLicenses(request);

		List<ViewTableDatasets> result = new ArrayList<>(datasets);
		List<ViewTableDatasets> toRemove = datasets.stream()
												   .filter(d -> d.getLicenseId() != null)
												   .filter(d -> {
													   Integer[] acceptedBy = d.getAcceptedBy();

													   if (mode == AuthenticationMode.NONE)
													   {
														   // If there's no authentication, check if the license is in the cookie
														   if (acceptedLicenses.contains(d.getLicenseId()))
														   {
															   acceptedBy = new Integer[1];
															   acceptedBy[0] = userDetails.getId();
															   d.setAcceptedBy(acceptedBy);
															   return false;
														   }
														   else
														   {
															   d.setAcceptedBy(new Integer[0]);
															   return true;
														   }
													   }
													   else if (mode == AuthenticationMode.SELECTIVE)
													   {
														   if (userDetails.getId() == -1000)
														   {
															   // If we offer login, but the user hasn't logged in, check the cookie
															   if (acceptedLicenses.contains(d.getLicenseId()))
															   {
																   acceptedBy = new Integer[1];
																   acceptedBy[0] = userDetails.getId();
																   d.setAcceptedBy(acceptedBy);
																   return false;
															   }
															   else
															   {
																   d.setAcceptedBy(new Integer[0]);
																   return true;
															   }
														   }
														   else
														   {

															   if (!CollectionUtils.isEmpty(acceptedBy))
															   {
																   List<Integer> ids = Arrays.asList(acceptedBy);
																   if (ids.contains(userDetails.getId()))
																   {
																	   // If the user accepted the license, set his/her id to indicate this
																	   acceptedBy = new Integer[1];
																	   acceptedBy[0] = userDetails.getId();
																	   d.setAcceptedBy(acceptedBy);
																	   return false;
																   }
																   else
																   {
																	   // Else, clear this information
																	   d.setAcceptedBy(new Integer[0]);
																	   return true;
																   }
															   }
															   else
															   {
																   // Else, clear this information
																   d.setAcceptedBy(new Integer[0]);
																   return true;
															   }
														   }
													   }
													   else
													   {
														   if (!CollectionUtils.isEmpty(acceptedBy))
														   {
															   List<Integer> ids = Arrays.asList(acceptedBy);
															   if (ids.contains(userDetails.getId()))
															   {
																   // If the user accepted the license, set his/her id to indicate this
																   acceptedBy = new Integer[1];
																   acceptedBy[0] = userDetails.getId();
																   d.setAcceptedBy(acceptedBy);
																   return false;
															   }
															   else
															   {
																   // Else, clear this information
																   d.setAcceptedBy(new Integer[0]);
																   return true;
															   }
														   }
														   else
														   {
															   // Else, clear this information
															   d.setAcceptedBy(new Integer[0]);
															   return true;
														   }
													   }
												   })
												   .collect(Collectors.toList());

		result.removeAll(toRemove);
		return result;
	}


	@POST
	@NeedsDatasets
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
	@NeedsDatasets
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PaginatedResult<List<Integer>> postDatasetTableIds(PaginatedRequest request)
			throws SQLException
	{
		return runQuery(request);
	}
}