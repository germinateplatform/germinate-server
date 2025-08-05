package jhi.germinate.resource;

import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.enums.UserType;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewUserDetailsType extends ViewUserDetails
{
	private UserType userType       = UserType.UNKNOWN;
	private String   userTypeString = null;

	public ViewUserDetailsType setUserTypeString(String userTypeString)
	{
		this.userTypeString = userTypeString;

		if (userTypeString != null)
		{
			switch (userTypeString)
			{
				case "Administrator":
					userType = UserType.ADMIN;
					break;
				case "Data Curator":
					userType = UserType.DATA_CURATOR;
					break;
				case "Regular User":
					userType = UserType.AUTH_USER;
					break;
				case "Suspended User":
				default:
					userType = UserType.UNKNOWN;
			}
		}

		return this;
	}
}
