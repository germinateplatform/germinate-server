package jhi.germinate.server.util;

import com.google.gson.*;

import java.sql.Timestamp;
import java.text.*;
import java.util.Date;

public class GsonUtil
{
	public static final String PATTERN_TIMESTAMP = "yyyy-MM-dd'T'HH:mm:ss.SSSXX";
	public static final String PATTERN_DATE      = "yyyy-MM-dd";

	private static Gson             gson;
	private static Gson             gsonExpose;
	private static SimpleDateFormat sdf;
	private static SimpleDateFormat sdfDate;

	public static Gson getInstance()
	{
		if (gson == null)
		{
			gson = getGsonBuilderInstance(false).create();
		}
		return gson;
	}

	public static Gson getExposeInstance()
	{
		if (gsonExpose == null)
		{
			gsonExpose = getGsonBuilderInstance(true).create();
		}
		return gsonExpose;
	}

	public static Gson getInstance(boolean onlyExpose)
	{
		if (!onlyExpose)
		{
			if (gson == null)
			{
				gson = getGsonBuilderInstance(false).create();
			}
			return gson;
		}
		else
		{
			if (gsonExpose == null)
			{
				gsonExpose = getGsonBuilderInstance(true).create();
			}
			return gsonExpose;
		}
	}

//	private static synchronized SimpleDateFormat getSDFInstance()
//	{
//		if (sdf == null)
//		{
//			sdf = new SimpleDateFormat(PATTERN_TIMESTAMP);
//		}
//		return sdf;
//	}

	public static synchronized Date parseDate(String date)
		throws ParseException
	{
		if (sdfDate == null)
			sdfDate = new SimpleDateFormat(PATTERN_TIMESTAMP);
		return sdfDate.parse(date);
	}

	private static synchronized String formatDate(Date date)
	{
		if (sdfDate == null)
			sdfDate = new SimpleDateFormat(PATTERN_TIMESTAMP);
		return sdfDate.format(date);
	}

	private static synchronized String formatDate(java.sql.Date date)
	{
		if (sdfDate == null)
			sdfDate = new SimpleDateFormat(PATTERN_TIMESTAMP);
		return sdfDate.format(date);
	}

	private static synchronized String formatDate(Timestamp date)
	{
		if (sdfDate == null)
			sdfDate = new SimpleDateFormat(PATTERN_TIMESTAMP);
		return sdfDate.format(date);
	}

	private static GsonBuilder getGsonBuilderInstance(boolean onlyExpose)
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		if (onlyExpose)
		{
			gsonBuilder.excludeFieldsWithoutExposeAnnotation();
		}
		gsonBuilder.registerTypeAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(formatDate(src)));
		gsonBuilder.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(formatDate(src)));
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, (JsonSerializer<java.sql.Date>) (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(formatDate(src)));
		gsonBuilder.registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, type, arg2) -> {
			try
			{
				return parseDate(json.getAsString());
			}
			catch (ParseException e)
			{
				return null;
			}
		});
		gsonBuilder.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, type, arg2) -> {
			try
			{
				return new Timestamp(parseDate(json.getAsString()).getTime());
			}
			catch (ParseException e)
			{
				return null;
			}
		});
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, (JsonDeserializer<java.sql.Date>) (json, type, arg2) -> {
			try
			{
				return new java.sql.Date(parseDate(json.getAsString()).getTime());
			}
			catch (ParseException e)
			{
				return null;
			}
		});

		return gsonBuilder;
	}

	public static <T> T fromJson(String json, Class<T> classOfT,
								 boolean onlyExpose)
	{
		try
		{
			return getInstance(onlyExpose).fromJson(json, classOfT);
		}
		catch (Exception ex)
		{
			// Log exception
			return null;
		}
	}
}