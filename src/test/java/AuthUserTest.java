import jhi.germinate.resource.*;

public class AuthUserTest extends PropertyTest
{
	protected static Token token;

	protected static void signIn()
	{
		// Now use the correct password
		LoginDetails details = new LoginDetails()
				.setUsername(properties.get("germinate.username"))
				.setPassword(properties.get("germinate.password"));

		RequestBuilder.ApiResult<Token> resp = RequestBuilder.<Token, LoginDetails>builder()
															 .path("token")
															 .clazz(Token.class)
															 .body(details)
															 .build()
															 .post();

		token = resp.data;
	}
}
