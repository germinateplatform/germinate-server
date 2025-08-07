import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableDatasets;
import org.junit.jupiter.api.*;

import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatasetAccessTest extends BaseTest
{
	@BeforeAll
	static void setUp()
	{
		setUpClient(null);
	}

	/**
	 * Try getting datasets.
	 */
	@Order(1)
	@Test
	void getAllDatasets()
	{
		// Try to access all publicly visible datasets
		WebTarget target = client.target(URL).path("dataset/table");

		PaginatedRequest req = new PaginatedRequest()
				.setPage(0)
				.setLimit(Integer.MAX_VALUE);

		Response resp = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(req, MediaType.APPLICATION_JSON));

		Assertions.assertEquals(200, resp.getStatus());
		PaginatedResult<List<ViewTableDatasets>> result = resp.readEntity(new GenericType<>()
		{
		});

		Assertions.assertEquals(11, result.getData().size());
	}

	@Order(2)
	@Test
	void tryAccessOnLicensedDataset()
	{
		// Try and request a dataset protected with a license without having accepted said license
		WebTarget target = client.target(URL).path("dataset/export/trial");
		SubsettedDatasetRequest req = new SubsettedDatasetRequest().setDatasetIds(new Integer[]{4});
		Response resp = target.request(MediaType.TEXT_PLAIN)
							  .post(Entity.entity(req, MediaType.APPLICATION_JSON));

		// Fails
		Assertions.assertEquals(404, resp.getStatus());

		// Now accept the license
		target = client.target(URL).path("license/34/accept");
		resp = target.request(MediaType.APPLICATION_JSON).get();

		// Succeeds
		Assertions.assertEquals(200, resp.getStatus());

		// Get the cookie response (set by server for non-auth sessions) and check them
		Map<String, NewCookie> cookies = resp.getCookies();
		Assertions.assertNotNull(cookies);
		Assertions.assertEquals(1, cookies.size());
		Assertions.assertEquals("accepted-licenses", cookies.get("accepted-licenses").getName());
		Assertions.assertEquals("34", cookies.get("accepted-licenses").getValue());
		Assertions.assertTrue(URL.contains(cookies.get("accepted-licenses").getPath()));

		// Now, request the same dataset again, just with the cookie set
		target = client.target(URL).path("dataset/export/trial");
		Invocation.Builder request = target.request(MediaType.TEXT_PLAIN);
		for (NewCookie cookie : cookies.values())
			request.cookie(cookie);
		resp = request.post(Entity.entity(req, MediaType.APPLICATION_JSON));

		// Succeeds
		Assertions.assertEquals(200, resp.getStatus());
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
