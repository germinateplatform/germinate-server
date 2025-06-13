package jhi.germinate.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.*;
import io.swagger.v3.oas.annotations.servers.*;
import jakarta.ws.rs.ApplicationPath;
import jhi.germinate.brapi.server.Brapi;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.PropertyWatcher;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@OpenAPIDefinition(
		info = @Info(
				title = "Swagger Germinate API implementation documentation",
				version = "1.0",
				description = "This is the Swagger API implementation documentation for Germinate.",
				license = @License(name = "Apache 2.0", url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
				contact = @Contact(url = "https://germinate.hutton.ac.uk", email = "germinate@hutton.ac.uk")
		),
		servers = {
				@Server(
						description = "Germinate Databases",
						url = "https://germinate.hutton.ac.uk/{instance}/api/",
						variables = @ServerVariable(
								defaultValue = "demo",
								name = "instance",
								allowableValues = {"demo"}
						)
				)
		}
)
@ApplicationPath("/api/")
public class Germinate extends ResourceConfig
{
	public Germinate()
	{
		PropertyWatcher.initialize();

		boolean brapiEnabled = PropertyWatcher.getBoolean(ServerProperty.BRAPI_ENABLED);

		if (brapiEnabled)
		{
			// Include the BrAPI package
			packages("jhi.germinate.server", "jhi.germinate.brapi.server");

			// And initialise the BrAPI code
			new Brapi(PropertyWatcher.get(ServerProperty.GERMINATE_CLIENT_URL), "/brapi/v2", PropertyWatcher.get(ServerProperty.DATA_DIRECTORY_EXTERNAL) + "/data/genotypes/");
		}
		else
		{
			// Otherwise, just load the main stuff
			packages("jhi.germinate.server");
		}

		register(MultiPartFeature.class);
	}
}
