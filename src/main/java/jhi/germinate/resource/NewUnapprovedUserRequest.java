package jhi.germinate.resource;

import jhi.gatekeeper.resource.NewUnapprovedUser;

/**
 * @author Sebastian Raubach
 */
public class NewUnapprovedUserRequest
{
	private NewUnapprovedUser user;
	private String            locale;

	public NewUnapprovedUser getUser()
	{
		return user;
	}

	public NewUnapprovedUserRequest setUser(NewUnapprovedUser user)
	{
		this.user = user;
		return this;
	}

	public String getLocale()
	{
		return locale;
	}

	public NewUnapprovedUserRequest setLocale(String locale)
	{
		this.locale = locale;
		return this;
	}
}
