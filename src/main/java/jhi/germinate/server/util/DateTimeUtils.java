package jhi.germinate.server.util;

import java.text.*;
import java.util.Date;

public class DateTimeUtils
{
	private static final SimpleDateFormat SDF      = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyy-MM-dd");

	public static synchronized String getFormattedDateTime(Date date)
	{
		return SDF.format(date);
	}

	public static synchronized String getFormattedDate(Date date)
	{
		return SDF_DATE.format(date);
	}

	public static synchronized Date parseDate(String date)
		throws ParseException
	{
		return SDF_DATE.parse(date);
	}
}
