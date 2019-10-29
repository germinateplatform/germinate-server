/*
 *  Copyright 2019 Information and Computational Sciences,
 *  The James Hutton Institute.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package jhi.germinate.server.util.watcher;

import org.apache.commons.io.monitor.*;

import java.io.*;
import java.net.URL;
import java.util.*;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.Database;
import jhi.germinate.server.auth.AuthenticationMode;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.resource.TokenResource;
import jhi.germinate.server.util.*;

/**
 * {@link PropertyWatcher} is a wrapper around {@link Properties} to readAll properties.
 *
 * @author Sebastian Raubach
 */
public class PropertyWatcher
{
	/** The name of the properties file */
	private static final String PROPERTIES_FILE = "config.properties";

	private static Properties properties = new Properties();

	private static FileAlterationMonitor monitor;
	private static File                  config = null;

	/**
	 * Attempts to reads the properties file and then checks the required properties.
	 */
	public static void initialize()
	{
		/* Start to listen for file changes */
		try
		{
			// We have to load the internal one initially to figure out where the external data directory is...
			URL resource = PropertyWatcher.class.getClassLoader().getResource(PROPERTIES_FILE);
			if (resource != null)
			{
				config = new File(resource.toURI());
				loadProperties(false);

				// Then check if there's another version in the external data directory
				String path = get(ServerProperty.DATA_DIRECTORY_EXTERNAL);
				if (path != null)
				{
					File folder = new File(path);
					if (folder.exists() && folder.isDirectory())
					{
						File potential = new File(folder, PROPERTIES_FILE);

						if (potential.exists() && potential.isFile())
						{
							// Use it
							config = potential;
						}
					}
				}

				// Finally, load it properly. This is either the original file or the external file.
				loadProperties(true);

				// Then watch whichever file exists for changes
				FileAlterationObserver observer = new FileAlterationObserver(config.getParentFile());
				monitor = new FileAlterationMonitor(1000L);
				observer.addListener(new FileAlterationListenerAdaptor()
				{
					@Override
					public void onFileChange(File file)
					{
						if (file.equals(config))
						{
							loadProperties(true);
						}
					}
				});
				monitor.addObserver(observer);
				monitor.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static void loadProperties(boolean checkAndInit)
	{
		try (FileInputStream stream = new FileInputStream(config))
		{
			properties.load(stream);
		}
		catch (IOException | NullPointerException e)
		{
			throw new RuntimeException(e);
		}

		if (checkAndInit)
		{
			checkRequiredProperties();

			Database.init(get(ServerProperty.DATABASE_SERVER), get(ServerProperty.DATABASE_NAME), get(ServerProperty.DATABASE_PORT), get(ServerProperty.DATABASE_USERNAME), get(ServerProperty.DATABASE_PASSWORD));
			GatekeeperClient.init(get(ServerProperty.GATEKEEPER_URL), get(ServerProperty.GATEKEEPER_USERNAME), get(ServerProperty.GATEKEEPER_PASSWORD));
			TokenResource.SALT = getInteger(ServerProperty.BCRYPT_SALT);
		}
	}

	public static void stopFileWatcher()
	{
		try
		{
			if (monitor != null)
				monitor.stop();

			monitor = null;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Checks the required properties
	 */
	private static void checkRequiredProperties()
	{
		for (ServerProperty prop : ServerProperty.values())
		{
			if (prop.isRequired())
			{
				// TODO
//				switch (prop)
//				{
//					case GERMINATE_AVAILABLE_PAGES:
//						Set<Page> availablePages = getSet(prop, Page.class);
//						if (CollectionUtils.isEmpty(availablePages))
//							throwException(prop);
//						break;
//
//					case GERMINATE_USE_AUTHENTICATION:
//						boolean useAuthentication = getBoolean(prop);
//						if (useAuthentication)
//						{
//							if (StringUtils.isEmpty(get(ServerProperty.GERMINATE_GATEKEEPER_SERVER)))
//								throwException(ServerProperty.GERMINATE_GATEKEEPER_SERVER);
//							if (StringUtils.isEmpty(get(ServerProperty.GERMINATE_GATEKEEPER_NAME)))
//								throwException(ServerProperty.GERMINATE_GATEKEEPER_NAME);
//						}
//						break;
//
//					case GERMINATE_GATEKEEPER_REGISTRATION_ENABLED:
//						boolean registrationNeedsGatekeeper = getBoolean(prop);
//
//						if (registrationNeedsGatekeeper)
//						{
//							String gatekeeperUrl = get(ServerProperty.GERMINATE_GATEKEEPER_URL);
//
//							if (StringUtils.isEmpty(gatekeeperUrl))
//								throwException(ServerProperty.GERMINATE_GATEKEEPER_URL);
//						}
//						break;
//
//					default:
				if (StringUtils.isEmpty(get(prop)))
					throwException(prop);
//				}
			}
		}
	}

	/**
	 * Throws a {@link RuntimeException} for the given property
	 *
	 * @param property The name of the property.
	 */
	private static void throwException(ServerProperty property)
	{
		throw new RuntimeException("Germinate failed to start: Non-optional property not set: '" + property.getKey() + "'");
	}

	/**
	 * Reads a property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The property or <code>null</code> if the property is not found
	 */
	public static String get(ServerProperty property)
	{
		String value = properties.getProperty(property.getKey());

		return StringUtils.isEmpty(value) ? property.getDefaultValue() : value;
	}

	/**
	 * Writes a property to the .properties file
	 *
	 * @param property The property to write
	 * @value The property value
	 */
	public static void set(ServerProperty property, String value)
	{
		if (value == null)
			properties.remove(property.getKey());
		else
			properties.setProperty(property.getKey(), value);
	}

	/**
	 * Reads an {@link Integer} property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The {@link Integer} property
	 */
	public static Integer getInteger(ServerProperty property)
	{
		try
		{
			return Integer.parseInt(get(property));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Writes an {@link Integer} property to the .properties file
	 *
	 * @param property The property to readAll
	 * @param value    The integer value to write
	 */
	public static void setInteger(ServerProperty property, Integer value)
	{
		if (value != null)
			set(property, Integer.toString(value));
		else
			set(property, null);
	}

	/**
	 * Reads a {@link Boolean} property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The {@link Boolean} property
	 */
	public static Boolean getBoolean(ServerProperty property)
	{
		return Boolean.parseBoolean(get(property));
	}

	/**
	 * Writes a {@link Boolean} property to the .properties file
	 *
	 * @param property The property to write
	 * @param value    The {@link Boolean} value
	 */
	public static void setBoolean(ServerProperty property, Boolean value)
	{
		if (value != null)
			set(property, Boolean.toString(value));
		else
			set(property, null);
	}

	/**
	 * Reads an {@link Long} property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The {@link Long} property
	 */
	public static Long getLong(ServerProperty property)
	{
		try
		{
			return Long.parseLong(get(property));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Reads an {@link Double} property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The {@link Double} property
	 */
	public static Double getDouble(ServerProperty property)
	{
		try
		{
			return Double.parseDouble(get(property));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Reads a {@link Float} property from the .properties file
	 *
	 * @param property The property to readAll
	 * @return The {@link Float} property
	 */
	public static Float getFloat(ServerProperty property)
	{
		try
		{
			return Float.parseFloat(get(property));
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}

	/**
	 * Writes a {@link Float} property to the .properties file
	 *
	 * @param property The property to write
	 * @param value    The value to write
	 */
	public static void setFloat(ServerProperty property, Float value)
	{
		if (value != null)
			set(property, Float.toString(value));
		else
			set(property, null);
	}

	/**
	 * Writes a {@link Double} property to the .properties file
	 *
	 * @param property The property to write
	 * @param value    The value to write
	 */
	public static void setDouble(ServerProperty property, Double value)
	{
		if (value != null)
			set(property, Double.toString(value));
		else
			set(property, null);
	}

	/**
	 * Reads a property from the .properties file. The fallback will be used if there is no such property.
	 *
	 * @param property The property to readAll
	 * @param fallback The value that is returned if the property isn't set
	 * @return The property or the fallback if the property is not found
	 */
	public static String get(ServerProperty property, String fallback)
	{
		String value = get(property);

		return StringUtils.isEmpty(value) ? fallback : value;
	}

	/**
	 * Reads a property from the .properties file and substitutes parameters
	 *
	 * @param property   The property to readAll
	 * @param parameters The parameters to substitute
	 * @return The property or null if the property is not found
	 */
	public static String get(ServerProperty property, Object... parameters)
	{
		String value = get(property);
		if (parameters.length > 0)
			return String.format(value, parameters);
		else
			return value;
	}

	public static <T> T get(ServerProperty property, Class<T> type)
	{
		String value = get(property);

		if (StringUtils.isEmpty(value))
		{
			return null;
		}
		else
		{
			if (type.equals(AuthenticationMode.class))
				return type.cast(AuthenticationMode.valueOf(value));
			else
				return null;
		}
	}

	public static <T> List<T> getPropertyList(ServerProperty property, Class<T> type)
	{
		List<T> result = new ArrayList<>();

		String line = get(property);

		if (!StringUtils.isEmpty(line))
		{
			for (String part : line.split(","))
			{
				if (type.equals(Integer.class))
					result.add(type.cast(Integer.parseInt(part)));
				else if (type.equals(String.class))
					result.add(type.cast(part));
				else if (type.equals(Double.class))
					result.add(type.cast(Double.parseDouble(part)));
				else if (type.equals(Float.class))
					result.add(type.cast(Float.parseFloat(part)));
			}
		}

		return result;
	}

	public static <T> void setSet(ServerProperty property, Set<T> values, Class<T> type)
	{
		StringBuilder line = new StringBuilder();

		if (!CollectionUtils.isEmpty(values))
		{
			boolean first = true;
			for (T item : values)
			{
				String value = toString(item, type);

				if (StringUtils.isEmpty(value))
					continue;

				if (first)
				{
					first = false;
				}
				else
				{
					line.append(",");
				}

				line.append(value);
			}
		}

		set(property, line.toString());
	}

	private static <T> String toString(T value, Class<T> type)
	{
		if (value == null)
			return null;

		if (type.equals(Integer.class))
			return Integer.toString((Integer) value);
		else if (type.equals(String.class))
			return (String) value;
		else if (type.equals(Double.class))
			return Double.toString((Double) value);
		else if (type.equals(Float.class))
			return Float.toString((Float) value);
		else if (type.equals(Long.class))
			return Long.toString((Long) value);

		return null;
	}

	public static <T> Set<T> getSet(ServerProperty property, Class<T> type)
	{
		Set<T> result = new HashSet<>();

		String line = get(property);

		if (!StringUtils.isEmpty(line))
		{
			for (String part : line.split(","))
			{
				if (type.equals(Integer.class))
					result.add(type.cast(Integer.parseInt(part)));
				else if (type.equals(String.class))
					result.add(type.cast(part));
				else if (type.equals(Double.class))
					result.add(type.cast(Double.parseDouble(part)));
				else if (type.equals(Float.class))
					result.add(type.cast(Float.parseFloat(part)));
			}
		}

		return result;
	}

	/**
	 * Stores changes to the properties persistantly in the config.properties file
	 *
	 * @throws IOException          Thrown if the file interaction fails
	 * @throws NullPointerException Thrown if the config.properties file URL cannot be converted to a URI
	 */
	public static synchronized void store()
		throws IOException, NullPointerException
	{
		//		URL url = PropertyWatcher.class.getClassLoader().getResource(PROPERTIES_FILE);
		try (FileOutputStream stream = new FileOutputStream(config))
		{
			properties.store(stream, null);
		}
	}
}
