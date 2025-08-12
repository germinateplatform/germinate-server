import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import org.junit.jupiter.api.*;
import uk.ac.hutton.ics.brapi.resource.base.*;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatasetAccessTest
{
	private Map<String, NewCookie> cookies = new HashMap<>();

	@BeforeEach
	void resetClient()
	{
		// We're using @BeforeEach here to make sure the client is properly reset between tests.
		// When we weren't doing this, there were remnants of cookies etc that couldn't be removed otherwise.
		RequestBuilder.resetClient();
	}

	/**
	 * Try getting datasets.
	 */
	@Order(1)
	@Test
	void getAllDatasets()
	{
		// Try to access all publicly visible datasets
		PaginatedRequest req = new PaginatedRequest()
				.setPage(0)
				.setLimit(Integer.MAX_VALUE);

		RequestBuilder.ApiResult<PaginatedResult<List<ViewTableDatasets>>> resp = RequestBuilder.<PaginatedResult<List<ViewTableDatasets>>, PaginatedRequest>builder()
																								.path("dataset/table")
																								.gt(new GenericType<>()
																								{
																								})
																								.body(req)
																								.build()
																								.post();
		Assertions.assertEquals(200, resp.status);
		Assertions.assertEquals(11, resp.data.getData().size());
	}

	@Order(2)
	@Test
	void tryAccessOnLicensedDataset()
	{
		// Try and request a dataset protected with a license without having accepted said license
		SubsettedDatasetRequest req = new SubsettedDatasetRequest().setDatasetIds(new Integer[]{4});
		RequestBuilder.RequestBuilderBuilder<String, SubsettedDatasetRequest> builder = RequestBuilder.<String, SubsettedDatasetRequest>builder()
																									  .path("dataset/export/trial")
																									  .mediaTypes(new String[]{MediaType.TEXT_PLAIN})
																									  .clazz(String.class)
																									  .body(req);
		RequestBuilder.ApiResult<String> det = builder.build()
													  .post();

		// Fails
		Assertions.assertEquals(404, det.status);

		acceptLicense();

		// Now, request the same dataset again, just with the cookie set
		det = builder.cookies(cookies)
					 .build()
					 .post();

		// Succeeds
		Assertions.assertEquals(200, det.status);
	}

	@Order(3)
	@Test
	void tryAccessOnLicensedDatasetBrapi()
	{
		// Now let's try to do the same with BrAPI.
		Map<String, String> params = new HashMap<>();
		params.put("studyDbId", "4");
		params.put("page", "0");
		params.put("pageSize", "2000");
		RequestBuilder.RequestBuilderBuilder<BaseResult<TableResult<List<String>>>, SubsettedDatasetRequest> builder = RequestBuilder.<BaseResult<TableResult<List<String>>>, SubsettedDatasetRequest>builder()
																																	 .path("brapi/v2/observations/table")
																																	 .params(params)
																																	 .gt(new GenericType<>()
																																	 {
																																	 });
		RequestBuilder.ApiResult<BaseResult<TableResult<List<String>>>> ot = builder
				.build()
				.get();

		// Succeeds, but empty
		Assertions.assertEquals(200, ot.status);
		Assertions.assertEquals(0, ot.data.getResult().getData().size());

		acceptLicense();

		// Now, request the same dataset again, just with the cookie set
		ot = builder.cookies(cookies)
					.build()
					.get();

		// Succeeds
		Assertions.assertEquals(200, ot.status);
		Assertions.assertEquals(2000, ot.data.getResult().getData().size());
	}

	private void acceptLicense()
	{
		// Now accept the license
		RequestBuilder.ApiResult<Boolean> lar = RequestBuilder.<Boolean, Void>builder()
															  .path("license/34/accept")
															  .clazz(Boolean.class)
															  .build()
															  .get();

		// Succeeds
		Assertions.assertEquals(200, lar.status);
		Assertions.assertEquals(true, lar.data);

		// Get the cookie response (set by server for non-auth sessions) and check them
		cookies = lar.cookies;
		Assertions.assertNotNull(cookies);
		Assertions.assertEquals(1, cookies.size());
		Assertions.assertEquals("accepted-licenses", cookies.get("accepted-licenses").getName());
		Assertions.assertEquals("34", cookies.get("accepted-licenses").getValue());
		Assertions.assertTrue(RequestBuilder.URL.contains(cookies.get("accepted-licenses").getPath()));
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
