package jhi.germinate.server;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;
import jhi.germinate.resource.ViewUserDetailsType;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.IOException;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Licenselogs.LICENSELOGS;

/**
 * This filter makes sure that the {@link Secured} resources are only accessible by users with the correct user type.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{
	private static final ConcurrentHashMap<Integer, Map<String, List<ViewTableDatasets>>> DATASET_ACCESS_INFO = new ConcurrentHashMap<>();

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException
	{
		AuthenticationFilter.UserDetails userDetails = (AuthenticationFilter.UserDetails) requestContext.getSecurityContext().getUserPrincipal();

		// Get the resource class which matches with the requested URL
		// Extract the roles declared by it
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<UserType> classRoles = extractRoles(resourceClass);

		// Get the resource method which matches with the requested URL
		// Extract the roles declared by it
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<UserType> methodRoles = extractRoles(resourceMethod);

		try
		{

			// Check if the user is allowed to execute the method
			// The method annotations override the class annotations
			if (methodRoles.isEmpty())
			{
				checkPermissions(classRoles, userDetails);
			}
			else
			{
				checkPermissions(methodRoles, userDetails);
			}

		}
		catch (Exception e)
		{
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
		}
	}

	// Extract the roles from the annotated element
	private List<UserType> extractRoles(AnnotatedElement annotatedElement)
	{
		if (annotatedElement == null)
		{
			return new ArrayList<>();
		}
		else
		{
			Secured secured = annotatedElement.getAnnotation(Secured.class);
			if (secured == null)
			{
				return new ArrayList<>();
			}
			else
			{
				UserType[] allowedRoles = secured.value();
				return Arrays.asList(allowedRoles);
			}
		}
	}

	private void checkPermissions(List<UserType> allowedRoles, AuthenticationFilter.UserDetails userDetails)
			throws Exception
	{
		if (userDetails != null)
		{
			// Check if the user contains one of the allowed roles
			// Throw an Exception if the user has not permission to execute the method

			if (!CollectionUtils.isEmpty(allowedRoles))
			{
				// TODO: implement
				if (allowedRoles.stream().filter(userDetails::isAtLeast).count() < 1)
				{
					throw new IOException();
				}
			}
		}
	}

	public static List<ViewTableDatasets> getDatasets(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, boolean onlyLicenseAccepted)
	{
		Map<String, List<ViewTableDatasets>> map = Objects.requireNonNullElse(DATASET_ACCESS_INFO.get(userDetails.getId()), new HashMap<>());
		List<ViewTableDatasets> ds = Objects.requireNonNullElse(map.get(dsType), new ArrayList<>());

		if (onlyLicenseAccepted)
		{
			AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

			Set<Integer> licenseIds = new HashSet<>();
			if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
			{
				try (Connection conn = Database.getConnection())
				{
					DSLContext context = Database.getContext(conn);
					licenseIds = context.select(LICENSELOGS.LICENSE_ID).from(LICENSELOGS).where(LICENSELOGS.USER_ID.eq(userDetails.getId())).fetchSet(LICENSELOGS.LICENSE_ID);
				}
				catch (SQLException e)
				{
					e.printStackTrace();
					Logger.getLogger("").severe(e.getMessage());
				}
			}
			else
			{
				licenseIds = AuthenticationFilter.getAcceptedLicenses(req);
			}

			ds = DatasetTableResource.restrictBasedOnLicenseAgreement(ds, licenseIds, userDetails);
		}

		return ds;
	}

	public static List<Integer> getDatasetIds(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, boolean onlyLicenseAccepted)
	{
		return new ArrayList<>(getDatasets(req, userDetails, dsType, onlyLicenseAccepted).stream().map(ViewTableDatasets::getDatasetId).toList());
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, List<Integer> requestedIds, boolean onlyLicenseAccepted)
	{
		List<Integer> availableIds = getDatasetIds(req, userDetails, dsType, onlyLicenseAccepted);

		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = availableIds;
		else
			requestedIds.retainAll(availableIds);

		return requestedIds;
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, Integer[] requestedIds, boolean onlyLicenseAccepted)
	{
		List<Integer> rIds = CollectionUtils.isEmpty(requestedIds) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(requestedIds));

		return restrictDatasetIds(req, userDetails, dsType, rIds, onlyLicenseAccepted);
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, Integer requestedId, boolean onlyLicenseAccepted)
	{
		List<Integer> rIds = requestedId == null ? new ArrayList<>() : new ArrayList<>(List.of(requestedId));

		return restrictDatasetIds(req, userDetails, dsType, rIds, onlyLicenseAccepted);
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, String requestedId, boolean onlyLicenseAccepted)
	{
		try
		{
			return restrictDatasetIds(req, userDetails, dsType, Integer.parseInt(requestedId), onlyLicenseAccepted);
		}
		catch (Exception e)
		{
			return restrictDatasetIds(req, userDetails, dsType, (Integer) null, onlyLicenseAccepted);
		}
	}

	public static void ensureUserDatasetsAvailable(List<String> dsTypes, AuthenticationFilter.UserDetails user)
	{
		if (!DATASET_ACCESS_INFO.containsKey(user.getId()))
		{
			try
			{
				DATASET_ACCESS_INFO.put(user.getId(), toMap(dsTypes, DatasetTableResource.getDatasetsForUser(user, null)));
			}
			catch (SQLException e)
			{
				Logger.getLogger("").info(e.getMessage());
			}
		}
	}

	public static void refreshUserDatasetInfo()
	{
		synchronized (DATASET_ACCESS_INFO)
		{
			DATASET_ACCESS_INFO.clear();
			GatekeeperClient.getUsersFromGatekeeper();

			try
			{
				List<String> dsTypes = DatasetTableResource.getDatasetTypes();
				DATASET_ACCESS_INFO.put(-1000, toMap(dsTypes, DatasetTableResource.getDatasetsForUser(new AuthenticationFilter.UserDetails(-1000, null, null, UserType.UNKNOWN, AuthenticationFilter.AGE), null)));
			}
			catch (SQLException e)
			{
				Logger.getLogger("").info(e.getMessage());
			}
		}
	}

	private static Map<String, List<ViewTableDatasets>> toMap(List<String> types, List<ViewTableDatasets> datasets)
	{
		// Prepare the map
		Map<String, List<ViewTableDatasets>> result = new HashMap<>();
		result.put(null, new ArrayList<>());
		for (String type : types)
			result.put(type, new ArrayList<>());

		for (ViewTableDatasets dataset : datasets)
		{
			result.get(dataset.getDatasetType()).add(dataset);
			result.get(null).add(dataset);
		}

		return result;
	}
}
