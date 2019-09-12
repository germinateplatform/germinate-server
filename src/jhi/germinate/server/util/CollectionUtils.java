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
 * {@link CollectionUtils} contains methods to manipulate/check {@link List}s. All methods can be used both on the client and server side.
 *
 * @author Sebastian Raubach
 */
public class CollectionUtils
{
	/**
	 * Joins the given input {@link Collection} with the given delimiter into a String
	 *
	 * @param input     The input {@link List}
	 * @param delimiter The delimiter to use
	 * @return The joined String
	 */
	public static <T> String join(Collection<T> input, String delimiter)
	{
		if (input == null || input.size() < 1)
			return "";

		StringBuilder builder = new StringBuilder();

		Iterator<T> it = input.iterator();

		builder.append(it.next());

		while (it.hasNext())
			builder.append(delimiter).append(it.next());

		return builder.toString();
	}

	/**
	 * Joins the given input {@link Collection} with the given delimiter into a String
	 *
	 * @param input     The input {@link List}
	 * @param delimiter The delimiter to use
	 * @return The joined String
	 */
	public static <T> String join(T[] input, String delimiter)
	{
		if (input == null || input.length < 1)
			return "";

		StringBuilder builder = new StringBuilder();

		builder.append(input[0]);

		for (int i = 1; i < input.length; i++)
			builder.append(delimiter)
				   .append(input[i]);

		return builder.toString();
	}

	/**
	 * Checks if AT LEAST ONE of the given {@link Collection}s is either <code>null</code> or empty.
	 *
	 * @param input The {@link Collection}s to check
	 * @return <code>true</code> if either <code>input == null</code> or <code>input.size() < 1</code> FOR AT LEAST ONE OF THE COLLECTIONS
	 */
	@SafeVarargs
	public static <T> boolean isEmpty(Collection<T>... input)
	{
		boolean result = false;
		for (Collection<T> coll : input)
		{
			result |= coll == null || coll.size() < 1;

			if (result)
				break;
		}

		return result;
	}

	public static <T> boolean isEmptyOrNull(Collection<T> input)
	{
		if (input == null || input.size() < 1)
			return true;
		else
		{
			for (T i : input)
			{
				if (i != null)
					return false;
			}

			return true;
		}
	}

	/**
	 * Creates a {@link List} from the given input {@link String} by first splitting it on the given splitter
	 *
	 * @param input    The input {@link String}
	 * @param splitter The splitter
	 * @return The parsed {@link List}
	 */
	public static List<String> parseStringList(String input, String splitter)
	{
		if (StringUtils.isEmpty(input))
			return new ArrayList<>();

		String[] parts = input.split(splitter);

		for (int i = 0; i < parts.length; i++)
			parts[i] = parts[i].trim();

		return new ArrayList<>(Arrays.asList(parts));
	}

	public static <T> List<T> combineList(Collection<T> first, Collection<T> second)
	{
		List<T> result = new ArrayList<>();

		if (!isEmpty(first))
			result.addAll(first);
		if (!isEmpty(second))
			result.addAll(second);

		return result;
	}
}
