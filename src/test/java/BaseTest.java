import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.MediaType;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;

import java.io.IOException;
import java.text.SimpleDateFormat;

public abstract class BaseTest
{
	protected static String             URL = "http://localhost:8180/germinate-demo-api/v4.9.0/api/";
	protected static Client             client;
	protected static Invocation.Builder postBuilder;

	protected static void setUpClient(String url)
	{
		String finalUrl = StringUtils.isEmpty(url) ? URL : url;
		client = ClientBuilder.newBuilder()
							  .build();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX"));
		SimpleModule module = new SimpleModule();
		module.addDeserializer(ULong.class, new ULongDeserializer());
		objectMapper.registerModule(module);
		// Create a Jackson Provider
		JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
		client.register(jsonProvider);

		postBuilder = client.target(finalUrl)
							.request(MediaType.APPLICATION_JSON);
	}

	/**
	 * Custom deserializer for jOOQ's weird ULong data type.
	 * Since this class has no public no-args constructor, calling .valueOf is required.
	 */
	protected static class ULongDeserializer extends JsonDeserializer<ULong>
	{
		@Override
		public ULong deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException, JacksonException
		{
			JsonNode node = jsonParser.getCodec().readTree(jsonParser);

			return ULong.valueOf((node.get("value")).asLong());
		}
	}
}
