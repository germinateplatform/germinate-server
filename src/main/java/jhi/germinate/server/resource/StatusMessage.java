package jhi.germinate.server.resource;

/**
 * @author Sebastian Raubach
 */
public interface StatusMessage
{
	String BAD_REQUEST_MISSING_FIELDS             = "Required fields not provided.";
	String CONFLICT_USER_ALREADY_HAS_ACCESS       = "User already has access.";
	String CONFLICT_USER_ALREADY_REQUESTED_ACCESS = "User has already requested access.";
	String CONFLICT_USERNAME_EMAIL_ALREADY_IN_USE = "Username or email address already in use.";
	String FORBIDDEN_ACCESS_TO_OTHER_USER         = "Access to other user not allowed.";
	String FORBIDDEN_INSUFFICIENT_PERMISSIONS     = "Operation not allowed for current user.";
	String FORBIDDEN_INVALID_CREDENTIALS          = "Invalid username or password.";
	String NOT_FOUND_ACTIVATION_KEY               = "Invalid activation key.";
	String NOT_FOUND_ACTIVATION_REQUEST           = "No request with the given activation key found.";
	String NOT_FOUND_ID                           = "Id not provided.";
	String NOT_FOUND_ID_OR_PAYLOAD                = "Id or payload not provided.";
	String NOT_FOUND_INSTITUTION                  = "Institution not found.";
	String NOT_FOUND_PAYLOAD                      = "Payload not provided.";
	String NOT_FOUND_TOKEN                        = "Token not provided.";
	String NOT_FOUND_USER                         = "User not found.";
	String UNAVAILABLE_EMAIL                      = "Failed to send emails.";
}
