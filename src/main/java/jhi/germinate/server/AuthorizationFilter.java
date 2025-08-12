package jhi.germinate.server;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;
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
import java.util.stream.Collectors;

import static jhi.germinate.server.database.codegen.tables.Licenselogs.LICENSELOGS;

/**
 * This filter makes sure that the {@link Secured} resources are only accessible by users with the correct user type.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{
	private static final ConcurrentHashMap<AuthenticationFilter.UserDetails, Map<String, List<ViewTableDatasets>>> DATASET_ACCESS_INFO                  = new ConcurrentHashMap<>();
	private static final ConcurrentHashMap<AuthenticationFilter.UserDetails, Map<String, List<ViewTableDatasets>>> DATASET_ACCESS_LICENSE_ACCEPTED_INFO = new ConcurrentHashMap<>();

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
				if (allowedRoles.stream().noneMatch(userDetails::isAtLeast))
				{
					throw new IOException();
				}
			}
		}
	}

	public static List<ViewTableDatasets> getDatasets(HttpServletRequest req, AuthenticationFilter.UserDetails userDetails, String dsType, boolean onlyLicenseAccepted)
	{
		List<ViewTableDatasets> ds;
		if (onlyLicenseAccepted)
		{
			AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

			Set<Integer> licenseIds;
			if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && userDetails.getId() != -1000))
			{
				// If we're authenticated, then use our cached info
				Map<String, List<ViewTableDatasets>> map = Objects.requireNonNullElse(DATASET_ACCESS_LICENSE_ACCEPTED_INFO.get(userDetails), new HashMap<>());
				ds = Objects.requireNonNullElse(map.get(dsType), new ArrayList<>());
				// Get the license ids from the datasets for which we already know the user has access to
				licenseIds = ds.stream().map(ViewTableDatasets::getLicenseId).filter(Objects::nonNull).collect(Collectors.toSet());
				ds = DatasetTableResource.restrictBasedOnLicenseAgreement(Objects.requireNonNullElse(map.get(dsType), new ArrayList<>()), licenseIds, userDetails);
			}
			else
			{
				// Else, we're not authenticated, hence need to rely on all datasets filtered down by cookie value
				Map<String, List<ViewTableDatasets>> map = Objects.requireNonNullElse(DATASET_ACCESS_INFO.get(userDetails), new HashMap<>());
				// Get the license ids from the cookie
				licenseIds = AuthenticationFilter.getAcceptedLicenses(req);
				ds = DatasetTableResource.restrictBasedOnLicenseAgreement(Objects.requireNonNullElse(map.get(dsType), new ArrayList<>()), licenseIds, userDetails);
			}
		}
		else
		{
			// No license checking required, just return all datasets that are visible
			Map<String, List<ViewTableDatasets>> map = Objects.requireNonNullElse(DATASET_ACCESS_INFO.get(userDetails), new HashMap<>());
			ds = Objects.requireNonNullElse(map.get(dsType), new ArrayList<>());
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

	public static void ensureUserDatasetsAvailable(HttpServletRequest req, AuthenticationFilter.UserDetails user)
	{
		try
		{
			List<String> dsTypes = DatasetTableResource.getDatasetTypes();
			List<ViewTableDatasets> ds = DatasetTableResource.getDatasetsForUser(user, null);
			DATASET_ACCESS_INFO.put(user, toMap(dsTypes, ds));

			AuthenticationMode mode = PropertyWatcher.get(ServerProperty.AUTHENTICATION_MODE, AuthenticationMode.class);

			Set<Integer> licenseIds = new HashSet<>();
			if (mode == AuthenticationMode.FULL || (mode == AuthenticationMode.SELECTIVE && user.getId() != -1000))
			{
				try (Connection conn = Database.getConnection())
				{
					DSLContext context = Database.getContext(conn);
					licenseIds = context.select(LICENSELOGS.LICENSE_ID).from(LICENSELOGS).where(LICENSELOGS.USER_ID.eq(user.getId())).fetchSet(LICENSELOGS.LICENSE_ID);
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

			ds = DatasetTableResource.restrictBasedOnLicenseAgreement(ds, licenseIds, user);
			DATASET_ACCESS_LICENSE_ACCEPTED_INFO.put(user, toMap(dsTypes, ds));
		}
		catch (SQLException e)
		{
			Logger.getLogger("").info(e.getMessage());
		}
	}

	public static void refreshUserDatasetInfo(boolean force)
	{
		synchronized (DATASET_ACCESS_INFO)
		{
			Set<AuthenticationFilter.UserDetails> users = new HashSet<>();
			users.addAll(DATASET_ACCESS_INFO.keySet());
			users.addAll(DATASET_ACCESS_LICENSE_ACCEPTED_INFO.keySet());

			DATASET_ACCESS_INFO.clear();
			DATASET_ACCESS_LICENSE_ACCEPTED_INFO.clear();
			try
			{
				List<String> dsTypes = DatasetTableResource.getDatasetTypes();
				AuthenticationFilter.UserDetails ud = new AuthenticationFilter.UserDetails(-1000, null, null, UserType.UNKNOWN, AuthenticationFilter.AGE);
				DATASET_ACCESS_INFO.put(ud, toMap(dsTypes, DatasetTableResource.getDatasetsForUser(ud, null)));

				if (force)
				{
					for (AuthenticationFilter.UserDetails user : users)
						ensureUserDatasetsAvailable(null, user);
				}
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
