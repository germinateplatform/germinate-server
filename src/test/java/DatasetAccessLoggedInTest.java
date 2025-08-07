import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.SubsettedDatasetRequest;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatasetAccessLoggedInTest extends AuthUserTest
{
	@BeforeAll
	static void setUp()
	{
		loadProperties();
		RequestBuilder.setUpClient(null);
		signIn();
	}

	@Order(1)
	@Test
	void removeAcceptedLicense()
	{
		// Now accept the license
		RequestBuilder.ApiResult<Boolean> ldr = RequestBuilder.<Boolean, Void>builder()
															  .path("license/34/accept")
															  .clazz(Boolean.class)
															  .token(token)
															  .build()
															  .delete();

		Assertions.assertEquals(200, ldr.status);
		Assertions.assertNotNull(ldr.data);
	}

	@Order(2)
	@Test
	void tryAccessOnLicensedDataset()
	{
		// Try and request a dataset protected with a license without having accepted said license
		SubsettedDatasetRequest req = new SubsettedDatasetRequest().setDatasetIds(new Integer[]{4});
		RequestBuilder.RequestBuilderBuilder<String, SubsettedDatasetRequest> builder = RequestBuilder.<String, SubsettedDatasetRequest>builder()
																									  .path("dataset/export/trial")
																									  .mediaType(MediaType.TEXT_PLAIN)
																									  .clazz(String.class)
																									  .token(token)
																									  .body(req);
		RequestBuilder.ApiResult<String> det = builder.build()
													  .post();

		// Fails
		Assertions.assertEquals(404, det.status);

		// Now accept the license
		RequestBuilder.ApiResult<Boolean> lar = RequestBuilder.<Boolean, Void>builder()
															  .path("license/34/accept")
															  .clazz(Boolean.class)
															  .token(token)
															  .build()
															  .get();

		// Succeeds
		Assertions.assertTrue(200 == lar.status || 204 == lar.status);
		if (lar.status != 204)
			Assertions.assertEquals(true, lar.data);

		// Now, request the same dataset again, just with the cookie set
		det = builder.build()
					 .post();

		// Succeeds
		Assertions.assertEquals(200, det.status);
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
