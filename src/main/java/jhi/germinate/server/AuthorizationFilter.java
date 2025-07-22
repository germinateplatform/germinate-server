package jhi.germinate.server;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import jhi.germinate.server.resource.datasets.DatasetTableResource;
import jhi.germinate.server.util.*;

import jakarta.annotation.Priority;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This filter makes sure that the {@link Secured} resources are only accessible by users with the correct user type.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{
	public static final String GERMINATE_DS_ALL              = "germinate.datasets.all";
	public static final String GERMINATE_DS_LICENSE_ACCEPTED = "germinate.datasets.license.accepted";

	@Context
	private ResourceInfo resourceInfo;

	@Context
	private HttpServletRequest request;

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

		if (!Objects.equals(requestContext.getMethod(), HttpMethod.OPTIONS))
		{
			// Check whether the user requested access to datasets.
			try
			{
				Objects.requireNonNullElse(resourceMethod.getAnnotation(NeedsDatasets.class), resourceClass.getAnnotation(NeedsDatasets.class));

				// Get all the datasets for this user
				List<String> dsTypes = DatasetTableResource.getDatasetTypes();
				List<ViewTableDatasets> allDatasets = DatasetTableResource.getDatasetsForUser(request, userDetails, null, false);
				List<ViewTableDatasets> licenseAcceptedDatasets = DatasetTableResource.getDatasetsForUser(request, userDetails, null, true);

				// Store them in a place where they are accessible
				requestContext.setProperty(GERMINATE_DS_ALL, toMap(dsTypes, allDatasets));
				requestContext.setProperty(GERMINATE_DS_LICENSE_ACCEPTED, toMap(dsTypes, licenseAcceptedDatasets));
			}
			catch (SQLException e)
			{
				Logger.getLogger("").severe(e.getMessage());
				e.printStackTrace();
			}
			catch (NullPointerException e)
			{
				// Neither class nor method has the annotation -> do nothing
			}
		}
	}

	private Map<String, List<Integer>> toMap(List<String> types, List<ViewTableDatasets> datasets)
	{
		// Prepare the map
		Map<String, List<Integer>> result = new HashMap<>();
		result.put(null, new ArrayList<>());
		for (String type : types)
			result.put(type, new ArrayList<>());

		for (ViewTableDatasets dataset : datasets)
		{
			result.get(dataset.getDatasetType()).add(dataset.getDatasetId());
			result.get(null).add(dataset.getDatasetId());
		}

		return result;
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

	public static List<Integer> getDatasetIds(HttpServletRequest req, String dsType, boolean onlyLicenseAccepted)
	{
		Map<String, List<Integer>> map = Objects.requireNonNullElse((HashMap<String, List<Integer>>) req.getAttribute(onlyLicenseAccepted ? GERMINATE_DS_LICENSE_ACCEPTED : GERMINATE_DS_ALL), new HashMap<>());

		return Objects.requireNonNullElse(map.get(dsType), new ArrayList<>());
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, String dsType, List<Integer> requestedIds, boolean onlyLicenseAccepted)
	{
		List<Integer> availableIds = getDatasetIds(req, dsType, onlyLicenseAccepted);

		if (CollectionUtils.isEmpty(requestedIds))
			requestedIds = availableIds;
		else
			requestedIds.retainAll(availableIds);

		return requestedIds;
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, String dsType, Integer[] requestedIds, boolean onlyLicenseAccepted)
	{
		List<Integer> rIds = CollectionUtils.isEmpty(requestedIds) ? new ArrayList<>() : new ArrayList<>(Arrays.asList(requestedIds));

		return restrictDatasetIds(req, dsType, rIds, onlyLicenseAccepted);
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, String dsType, Integer requestedId, boolean onlyLicenseAccepted)
	{
		List<Integer> rIds = requestedId == null ? new ArrayList<>() : new ArrayList<>(List.of(requestedId));

		return restrictDatasetIds(req, dsType, rIds, onlyLicenseAccepted);
	}

	public static List<Integer> restrictDatasetIds(HttpServletRequest req, String dsType, String requestedId, boolean onlyLicenseAccepted)
	{
		try
		{
			return restrictDatasetIds(req, dsType, Integer.parseInt(requestedId), onlyLicenseAccepted);
		}
		catch (Exception e)
		{
			return restrictDatasetIds(req, dsType, (Integer) null, onlyLicenseAccepted);
		}
	}
}
