import de.poiu.apron.PropertyFile;
import jhi.germinate.server.util.PropertyWatcher;

import java.io.*;
import java.net.URL;

public abstract class PropertyTest
{
	protected static PropertyFile properties;

	protected static void loadProperties()
	{
		URL resource = PropertyWatcher.class.getClassLoader().getResource("test.properties");
		if (resource != null)
		{
			try
			{
				File config = new File(resource.toURI());
				try (FileInputStream stream = new FileInputStream(config))
				{
					properties = PropertyFile.from(stream);
				}
				catch (IOException | NullPointerException e)
				{
					throw new RuntimeException(e);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
