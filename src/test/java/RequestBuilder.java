import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import jhi.germinate.resource.Token;
import lombok.*;
import lombok.experimental.Accessors;
import org.glassfish.jersey.apache5.connector.Apache5ConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.jooq.tools.StringUtils;
import org.jooq.types.ULong;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.logging.*;

@Builder
public class RequestBuilder<T, U>
{
	protected static String URL = "http://localhost:8180/germinate-demo-api/v4.9.0/api/";
	protected static Client client;

	@Builder.Default
	private String                 url        = URL;
	@Builder.Default
	private String[]               mediaTypes = {MediaType.APPLICATION_JSON};
	private String                 path;
	private Class<T>               clazz;
	private GenericType<T>         gt;
	private U                      body;
	private Map<String, NewCookie> cookies;
	private Map<String, String>    params;
	private Token                  token;

	protected static void resetClient()
	{
		ClientConfig config = new ClientConfig();
		config.connectorProvider(new Apache5ConnectorProvider());
		client = ClientBuilder.newClient(config);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX"));
		SimpleModule module = new SimpleModule();
		module.addDeserializer(ULong.class, new ULongDeserializer());
		objectMapper.registerModule(module);
		// Create a Jackson Provider
		JacksonJaxbJsonProvider jsonProvider = new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS);
		client.register(jsonProvider);
//		client.register(new LoggingFilter());
	}

	private ApiResult<T> process(Response response)
	{
		int code = response.getStatus();
		T result = code == 200 ? (clazz != null ? response.readEntity(clazz) : response.readEntity(gt)) : null;

		return new ApiResult<T>().setData(result)
								 .setStatus(code)
								 .setHeaders(response.getStringHeaders())
								 .setResponseContent(result == null ? response.readEntity(String.class) : null)
								 .setCookies(response.getCookies());
	}

	public ApiResult<T> delete()
	{
		return process(addToken(addCookies(addParams(client.target(url)
														   .path(path))
				.request(mediaTypes)))
				.delete());
	}

	public ApiResult<T> get()
	{
		return process(addToken(addCookies(addParams(client.target(url)
														   .path(path))
				.request(mediaTypes)))
				.get());
	}

	public ApiResult<T> patch()
	{
		return process(addToken(addCookies(addParams(client.target(url)
														   .path(path))
				.request(mediaTypes)))
				.method(HttpMethod.PATCH, Entity.entity(body, MediaType.APPLICATION_JSON)));
	}

	private Invocation.Builder addCookies(Invocation.Builder request)
	{
		if (cookies != null)
		{
			for (NewCookie cookie : cookies.values())
				request.cookie(cookie);
		}

		return request;
	}

	private Invocation.Builder addToken(Invocation.Builder request)
	{
		if (token != null && !StringUtils.isEmpty(token.getToken()))
			request = request.header("Authorization", "Bearer " + token.getToken());

		return request;
	}

	private WebTarget addParams(WebTarget target)
	{
		if (params != null)
		{
			for (Map.Entry<String, String> param : params.entrySet())
				target = target.queryParam(param.getKey(), param.getValue());
		}

		return target;
	}

	public ApiResult<T> post()
	{
		return process(addToken(addCookies(addParams(client.target(url)
														   .path(path))
				.request(mediaTypes)))
				.post(Entity.entity(body, MediaType.APPLICATION_JSON)));
	}

	/**
	 * Custom deserializer for jOOQ's weird ULong data type.
	 * Since this class has no public no-args constructor, calling .valueOf is required.
	 */
	public static class ULongDeserializer extends JsonDeserializer<ULong>
	{
		@Override
		public ULong deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
				throws IOException, JacksonException
		{
			JsonNode node = jsonParser.getCodec().readTree(jsonParser);

			return ULong.valueOf((node.get("value")).asLong());
		}
	}

	@NoArgsConstructor
	@Setter
	@Accessors(chain = true)
	public static class ApiResult<T>
	{
		public T                              data;
		public int                            status;
		public String                         responseContent;
		public Map<String, NewCookie>         cookies;
		public MultivaluedMap<String, String> headers;
	}

	public static class LoggingFilter implements ClientRequestFilter
	{
		private static final Logger LOG = Logger.getLogger(LoggingFilter.class.getName());

		@Override
		public void filter(ClientRequestContext requestContext)
				throws IOException
		{
			LOG.log(Level.INFO, "PATH: " + requestContext.getUri().toString() + "\n" + "COOKIES: " + requestContext.getCookies().toString());

		}
	}
}
