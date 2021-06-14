package jhi.germinate.server.util;

/**
 * @author Sebastian Raubach
 */
public enum StatusMessage
{
	FORBIDDEN_ACCESS_TO_OTHER_USER("Access to other user not allowed."),
	FORBIDDEN_INSUFFICIENT_PERMISSIONS("Operation not allowed for current user."),
	FORBIDDEN_INVALID_CREDENTIALS("Invalid username or password."),
	NOT_FOUND_TOKEN("Token not provided.");

	private String description;

	StatusMessage(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}
}
