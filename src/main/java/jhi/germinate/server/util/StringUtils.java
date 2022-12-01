/*
 *  Copyright 2017 Information and Computational Sciences,
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

package jhi.germinate.server.util;

import java.util.*;

/**
 * {@link StringUtils} contains methods to manipulate/check {@link String}s.
 *
 * @author Sebastian Raubach
 */
public class StringUtils
{
	/**
	 * Checks if the given {@link String} is either <code>null</code> or empty after calling {@link String#trim()}.
	 *
	 * @param input The {@link String} to check
	 * @return <code>true</code> if the given {@link String} is <code>null</code> or empty after calling {@link String#trim()}.
	 */
	public static boolean isEmpty(String input)
	{
		return input == null || input.strip().isEmpty();
	}

	/**
	 * Checks if the given {@link String} is either <code>null</code>, empty or contains solely of single or double quotes.
	 *
	 * @param input The input to check
	 * @return <code>true</code> if the input is either empty or consists solely of single or double quotes.
	 */
	public static boolean isEmptyOrQuotes(String input)
	{
		if (isEmpty(input))
			return true;
		else
			return isEmpty(input.replace("'", "").replace("\"", ""));
	}

	/**
	 * Returns either the original string (if it's not empty) or an empty string (if it is)
	 *
	 * @param input The input to check
	 * @return Either the original string (if it's not empty) or an empty string (if it is)
	 */
	public static String orEmpty(String input)
	{
		if (isEmpty(input))
			return "";
		else
			return input;
	}

	/**
	 * Returns either the original string (if it's not empty) or empty double quotes (if it is)
	 *
	 * @param input The input to check
	 * @return Either the original string (if it's not empty) or empty double quotes (if it is)
	 */
	public static String orEmptyQuotes(String input)
	{
		if (isEmpty(input))
			return "\"\"";
		else
			return input;
	}

	/**
	 * Null-safe toString method for Objects. Returns <code>null</code> if the input is <code>null</code>.
	 *
	 * @param value The input object to check
	 * @return Null-safe toString method for Objects. Returns <code>null</code> if the input is <code>null</code>.
	 */
	public static String toString(Object value)
	{
		return value == null ? null : Objects.toString(value);
	}

	public static String join(String separator, String... parts)
	{
		List<String> nonNull = new ArrayList<>();

		if (parts != null)
		{
			for (String part : parts)
			{
				if (!StringUtils.isEmpty(part))
					nonNull.add(part);
			}

			return String.join(separator, nonNull);
		}

		return null;
	}

	public static String truncate(String input, int limit) {
		if (input == null)
			return "";
		if (input.length() < limit)
			return input;
		return input.substring(0, limit) + "...";
	}
}
