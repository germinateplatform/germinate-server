import jakarta.ws.rs.core.GenericType;
import jhi.gatekeeper.resource.PaginatedResult;
import jhi.germinate.resource.*;
import jhi.germinate.resource.enums.UserType;
import jhi.germinate.server.database.codegen.tables.pojos.ViewTableImages;
import jhi.germinate.server.resource.images.ImageResource;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageTokenTest extends AuthUserTest
{
	private static ViewTableImages image;

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
	void getImages()
	{
		PaginatedRequest req = new PaginatedDatasetRequest()
				.setLimit(10)
				.setPage(0);
		RequestBuilder.ApiResult<PaginatedResult<List<ViewTableImages>>> imgRes = RequestBuilder.<PaginatedResult<List<ViewTableImages>>, PaginatedRequest>builder()
																								.path("image/table")
																								.gt(new GenericType<>()
																								{
																								})
																								.body(req)
																								.token(token)
																								.build()
																								.post();

		Assertions.assertEquals(200, imgRes.status);
		Assertions.assertNotNull(imgRes.data);
		Assertions.assertNotNull(imgRes.data.getData());
		Assertions.assertNotEquals(0, imgRes.data.getData().size());

		image = imgRes.data.getData().getFirst();
	}

	@Order(2)
	@Test
	void requestImage()
	{
		Map<String, String> params = new HashMap<>();
		params.put("type", ImageResource.ImageType.database.name());
		params.put("name", image.getImagePath());
		params.put("token", token.getImageToken());
		RequestBuilder.ApiResult<InputStream> srcRes = RequestBuilder.<InputStream, Void>builder()
																	 .path("image/" + image.getImageId() + "/src")
																	 .params(params)
																	 .mediaTypes(new String[]{"image/png", "image/jpeg", "image/*"})
																	 .clazz(InputStream.class)
																	 .build()
																	 .get();

		Assertions.assertEquals(200, srcRes.status);
		Assertions.assertNotNull(srcRes.data);
		Assertions.assertDoesNotThrow(() -> {
			// Try and read the image using ImageIO. This will fail if it's not a valid image
			BufferedImage img = ImageIO.read(srcRes.data);

			// Then check some fields
			Assertions.assertNotNull(img);
			Assertions.assertNotEquals(0, img.getHeight());
			Assertions.assertNotEquals(0, img.getWidth());
		});
	}

	@AfterAll
	static void breakDown()
			throws Exception
	{
	}
}
