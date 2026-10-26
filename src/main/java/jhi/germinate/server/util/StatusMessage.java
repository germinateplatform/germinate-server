package jhi.germinate.server.util;

import lombok.Getter;

/**
 * @author Sebastian Raubach
 */
@Getter
public enum StatusMessage
{
	FORBIDDEN_ACCESS_TO_OTHER_USER("Access to other user not allowed."),
	FORBIDDEN_INSUFFICIENT_PERMISSIONS("Operation not allowed for current user."),
	FORBIDDEN_INVALID_CREDENTIALS("Invalid username or password."),
	NOT_FOUND_TOKEN("Token not provided.");

	private final String description;

	StatusMessage(String description)
	{
		this.description = description;
	}
}
