package jhi.germinate.resource;

import jhi.gatekeeper.server.database.tables.pojos.ViewUserDetails;
import jhi.germinate.resource.enums.UserType;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class ViewUserDetailsType extends ViewUserDetails
{
	private UserType userType       = UserType.UNKNOWN;
	private String   userTypeString = null;

	public static ViewUserDetailsType from(ViewUserDetails from)
	{
		if (from instanceof ViewUserDetailsType fr)
			return fr;
		else
			return new ViewUserDetailsType(from.getId(), from.getUsername(), from.getFullName(), from.getEmailAddress(), from.getLastLogin(), from.getCreatedOn(), from.getGatekeeperAccess(), from.getName(), from.getAcronym(), from.getAddress(), UserType.UNKNOWN, null);
	}

	public ViewUserDetailsType(Integer id, String username, String fullName, String emailAddress, Timestamp lastLogin, Timestamp createdOn, Byte gatekeeperAccess, String name, String acronym, String address, UserType userType, String userTypeString)
	{
		super(id, username, fullName, emailAddress, lastLogin, createdOn, gatekeeperAccess, name, acronym, address);
		this.userType = userType;
		this.userTypeString = userTypeString;
	}

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
