package jhi.germinate.server;

import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.util.*;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;

/**
 * This filter makes sure that the {@link Secured} resources are only accessible by users with the correct user type.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter
{
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
}
