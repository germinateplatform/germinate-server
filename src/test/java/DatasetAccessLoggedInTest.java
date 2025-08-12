import jakarta.ws.rs.core.MediaType;
import jhi.germinate.resource.SubsettedDatasetRequest;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.*;
import org.jooq.DSLContext;
import org.junit.jupiter.api.*;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatasetAccessLoggedInTest extends AuthUserTest
{
	@BeforeAll
	static void setUp()
	{
		loadProperties();
		RequestBuilder.resetClient();
		signIn(UserType.AUTH_USER);

		Assertions.assertNotNull(token);
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
																									  .mediaTypes(new String[]{MediaType.TEXT_PLAIN})
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

		// Now, request the same dataset again
		det = builder.build()
					 .post();

		// Succeeds
		Assertions.assertEquals(200, det.status);
	}

	@Order(3)
	@Test
	void tryAccessingDatasetAgainAfterDatasetPatch()
			throws SQLException
	{
		Database.init(properties.get("database.server"), properties.get("database.name"), properties.get("database.port"), properties.get("database.username"), properties.get("database.password"), false);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			Datasets dataset = context.selectFrom(DATASETS).where(DATASETS.ID.eq(4)).fetchAnyInto(Datasets.class);
			Assertions.assertNotNull(dataset);

			signIn(UserType.ADMIN);
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

			RequestBuilder.ApiResult<ViewTableDatasets> res = RequestBuilder.<ViewTableDatasets, Datasets>builder()
																			.path("dataset/4")
																			.clazz(ViewTableDatasets.class)
																			.token(token)
																			.body(dataset)
																			.build()
																			.patch();

			Assertions.assertEquals(200, res.status);
			Assertions.assertNotNull(res.data);

			Assertions.assertEquals(dataset.getId(), res.data.getDatasetId());

			signIn(UserType.AUTH_USER);

			SubsettedDatasetRequest req = new SubsettedDatasetRequest().setDatasetIds(new Integer[]{4});
			RequestBuilder.ApiResult<String> det = RequestBuilder.<String, SubsettedDatasetRequest>builder()
																 .path("dataset/export/trial")
																 .mediaTypes(new String[]{MediaType.TEXT_PLAIN})
																 .clazz(String.class)
																 .token(token)
																 .body(req)
																 .build()
																 .post();
			// Succeeds still
			Assertions.assertEquals(200, det.status);
		}
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
