import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.UserType;

public class AuthUserTest extends PropertyTest
{
	protected static Token token;

	protected static void signIn(UserType userType)
	{
		// Now use the correct password
		LoginDetails details = new LoginDetails();

		if (userType.equals(UserType.ADMIN))
		{
			details.setUsername(properties.get("germinate.admin.username"))
				   .setPassword(properties.get("germinate.admin.password"));
		}
		else if (userType.equals(UserType.AUTH_USER))
		{
			details.setUsername(properties.get("germinate.regular.username"))
				   .setPassword(properties.get("germinate.regular.password"));
		}

		RequestBuilder.ApiResult<Token> resp = RequestBuilder.<Token, LoginDetails>builder()
															 .path("token")
															 .clazz(Token.class)
															 .body(details)
															 .build()
															 .post();

		token = resp.data;
	}
}
