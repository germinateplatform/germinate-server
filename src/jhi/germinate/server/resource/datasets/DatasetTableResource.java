package jhi.germinate.server.resource.datasets;

import com.google.gson.*;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.restlet.*;
import org.restlet.data.Status;
import org.restlet.resource.*;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.*;
import jhi.germinate.server.database.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.PaginatedServerResource;
import jhi.germinate.server.util.CollectionUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

import static jhi.germinate.server.database.tables.Datasetpermissions.*;
import static jhi.germinate.server.database.tables.Licenselogs.*;
import static jhi.germinate.server.database.tables.Usergroupmembers.*;
import static jhi.germinate.server.database.tables.Usergroups.*;
import static jhi.germinate.server.database.tables.ViewTableDatasets.*;

/**
 * @author Sebastian Raubach
 */
public class DatasetTableResource extends PaginatedServerResource
{
	public static List<Integer> getDatasetIdsForUser(Request req, Response resp)
	{
		return getDatasetsForUser(req, resp).stream()
											.map(ViewTableDatasets::getDatasetId)
											.collect(Collectors.toList());
	}

	public static List<ViewTableDatasets> getDatasetsForUser(Request req, Response resp)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(req, resp);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
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

			return restrictBasedOnLicenseAgreement(from.fetchInto(ViewTableDatasets.class), req, userDetails);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	public static List<ViewTableDatasets> getDatasetForId(Integer datasetId, Request req, Response resp, boolean checkIfLicenseAccepted)
	{
		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(req, resp);

		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
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
				return new ArrayList<>();
			}
			else
			{
				if (checkIfLicenseAccepted)
					return restrictBasedOnLicenseAgreement(Collections.singletonList(dataset), req, userDetails);
				else
					return Collections.singletonList(dataset);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private static List<ViewTableDatasets> restrictBasedOnLicenseAgreement(List<ViewTableDatasets> datasets, Request request, CustomVerifier.UserDetails userDetails)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);
		Set<Integer> acceptedLicenses = CustomVerifier.getAcceptedLicenses(request);

		List<ViewTableDatasets> result = new ArrayList<>(datasets);
		List<ViewTableDatasets> toRemove = datasets.stream()
												   .filter(d -> d.getLicenseId() != null)
												   .filter(d -> {
													   JsonArray acceptedBy = d.getAcceptedBy();
													   JsonElement userId = new JsonParser().parse(Integer.toString(userDetails.getId()));

													   if (mode == AuthenticationMode.NONE)
													   {
														   // If there's no authentication, check if the license is in the cookie
														   if (acceptedLicenses.contains(d.getLicenseId()))
														   {
															   acceptedBy = new JsonArray();
															   acceptedBy.add(userId);
															   d.setAcceptedBy(acceptedBy);
															   return false;
														   }
														   else
														   {
															   d.setAcceptedBy(new JsonArray());
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
																   acceptedBy = new JsonArray();
																   acceptedBy.add(userId);
																   d.setAcceptedBy(acceptedBy);
																   return false;
															   }
															   else
															   {
																   d.setAcceptedBy(new JsonArray());
																   return true;
															   }
														   }
														   else
														   {
															   if (acceptedBy != null && acceptedBy.contains(userId))
															   {
																   // If the user accepted the license, set his/her id to indicate this
																   acceptedBy = new JsonArray();
																   acceptedBy.add(userId);
																   d.setAcceptedBy(acceptedBy);
																   return false;
															   }
															   else
															   {
																   // Else, clear this information
																   d.setAcceptedBy(new JsonArray());
																   return true;
															   }
														   }
													   }
													   else
													   {
														   if (acceptedBy != null && acceptedBy.contains(userId))
														   {
															   // If the user accepted the license, set his/her id to indicate this
															   acceptedBy = new JsonArray();
															   acceptedBy.add(userId);
															   d.setAcceptedBy(acceptedBy);
															   return false;
														   }
														   else
														   {
															   // Else, clear this information
															   d.setAcceptedBy(new JsonArray());
															   return true;
														   }
													   }
												   })
												   .collect(Collectors.toList());

		result.removeAll(toRemove);
		return result;
	}

	@Post("json")
	public PaginatedResult<List<ViewTableDatasets>> getJson(UnacceptedLicenseRequest request)
	{
		AdjustQuery adjuster = null;

		if (request.getJustUnacceptedLicenses() != null && request.getJustUnacceptedLicenses())
		{
			CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());
			Set<Integer> ids = CustomVerifier.getAcceptedLicenses(getRequest());
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

	public PaginatedResult<List<ViewTableDatasets>> runQuery(PaginatedRequest request, AdjustQuery optionalAdjuster)
	{
		AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

		CustomVerifier.UserDetails userDetails = CustomVerifier.getFromSession(getRequest(), getResponse());

		processRequest(request);
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			SelectSelectStep<Record> select = context.select();

			if (previousCount == -1)
				select.hint("SQL_CALC_FOUND_ROWS");

			SelectJoinStep<Record> from = select.from(VIEW_TABLE_DATASETS);

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

			if (optionalAdjuster != null)
				optionalAdjuster.adjustQuery(from);

			// Filter here!
			filter(from, filters);

			List<ViewTableDatasets> result = setPaginationAndOrderBy(from)
				.fetch()
				.into(ViewTableDatasets.class);

			Set<Integer> acceptedLicenses = CustomVerifier.getAcceptedLicenses(getRequest());

			result.forEach(d -> {
				JsonArray acceptedBy = d.getAcceptedBy();
				JsonElement userId = new JsonParser().parse(Integer.toString(userDetails.getId()));
				if (mode == AuthenticationMode.NONE)
				{
					// If there's no authentication, check if the license is in the cookie
					if (acceptedLicenses.contains(d.getLicenseId()))
					{
						acceptedBy = new JsonArray();
						acceptedBy.add(userId);
						d.setAcceptedBy(acceptedBy);
					}
					else
					{
						d.setAcceptedBy(new JsonArray());
					}
				}
				else if (mode == AuthenticationMode.SELECTIVE)
				{
					if (userDetails.getId() == -1000)
					{
						// If we offer login, but the user hasn't logged in, check the cookie
						if (acceptedLicenses.contains(d.getLicenseId()))
						{
							acceptedBy = new JsonArray();
							acceptedBy.add(userId);
							d.setAcceptedBy(acceptedBy);
						}
						else
						{
							d.setAcceptedBy(new JsonArray());
						}
					}
					else
					{
						if (acceptedBy != null && acceptedBy.contains(userId))
						{
							// If the user accepted the license, set their id to indicate this
							acceptedBy = new JsonArray();
							acceptedBy.add(userId);
							d.setAcceptedBy(acceptedBy);
						}
						else
						{
							// Else, clear this information
							d.setAcceptedBy(new JsonArray());
						}
					}
				}
				else
				{
					if (acceptedBy != null && acceptedBy.contains(userId))
					{
						// If the user accepted the license, set their id to indicate this
						acceptedBy = new JsonArray();
						acceptedBy.add(userId);
						d.setAcceptedBy(acceptedBy);
					}
					else
					{
						// Else, clear this information
						d.setAcceptedBy(new JsonArray());
					}
				}
			});

			long count = previousCount == -1 ? context.fetchOne("SELECT FOUND_ROWS()").into(Long.class) : previousCount;

			return new PaginatedResult<>(result, count);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	public interface AdjustQuery
	{
		void adjustQuery(SelectJoinStep<Record> query);
	}
}