import de.poiu.apron.PropertyFile;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.*;
import jhi.germinate.server.util.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Logger;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokenTest extends PropertyTest
{
	@BeforeAll
	static void setUp()
	{
		loadProperties();
		RequestBuilder.setUpClient(null);
	}

	/**
	 * Try getting datasets.
	 */
	@Order(1)
	@Test
	void getToken()
	{
		// Try to log in
		LoginDetails details = new LoginDetails()
				.setUsername("admin")
				.setPassword(UUID.randomUUID().toString());

		RequestBuilder.RequestBuilderBuilder<Token, LoginDetails> builder = RequestBuilder.<Token, LoginDetails>builder()
																						  .path("token")
																						  .clazz(Token.class);

		RequestBuilder.ApiResult<Token> resp = builder.body(details)
													  .build()
													  .post();

		// Login fails, wrong user details
		Assertions.assertEquals(403, resp.status);
		Assertions.assertTrue(resp.responseContent.contains(StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name()));

		// Update the username to the correct one, but leave it on the old/wrong password.
		details.setUsername(properties.get("germinate.username"));

		resp = builder.body(details)
					  .build()
					  .post();

		// Login fails, wrong user details
		Assertions.assertEquals(403, resp.status);
		Assertions.assertTrue(resp.responseContent.contains(StatusMessage.FORBIDDEN_INVALID_CREDENTIALS.name()));

		// Now use the correct password
		details.setPassword(properties.get("germinate.password"));

		resp = builder.body(details)
					  .build()
					  .post();

		// Login succeeds
		Assertions.assertEquals(200, resp.status);
		Assertions.assertNotNull(resp.data);
		Assertions.assertNotNull(resp.data.getId());
		Assertions.assertNotNull(resp.data.getUserType());
		Assertions.assertNotEquals(UserType.UNKNOWN.name(), resp.data.getUserType());
		Assertions.assertFalse(StringUtils.isEmpty(resp.data.getUsername()));
		Assertions.assertFalse(StringUtils.isEmpty(resp.data.getToken()));
		Assertions.assertFalse(StringUtils.isEmpty(resp.data.getImageToken()));
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
