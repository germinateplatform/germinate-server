package jhi.germinate.server;

import jhi.germinate.brapi.server.Brapi;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.PropertyWatcher;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import jakarta.ws.rs.ApplicationPath;

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
