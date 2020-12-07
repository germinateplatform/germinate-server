/*
 * Copyright 2018 Information & Computational Sciences, The James Hutton Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jhi.germinate.server.resource;

import jhi.germinate.server.util.StringUtils;
import org.restlet.data.Status;
import org.restlet.data.*;
import org.restlet.representation.FileRepresentation;
import org.restlet.resource.*;

import java.io.File;

/**
 * {@link ServerResource} handling {@link ClientLocaleResource} requests.
 *
 * @author Sebastian Raubach
 */
public class ClientLocaleResource extends BaseServerResource
{
	private String locale;

	@Override
	protected void doInit()
		throws ResourceException
	{
		super.doInit();

		try
		{
			this.locale = getRequestAttributes().get("locale").toString();
		}
		catch (NullPointerException | NumberFormatException e)
		{
		}
	}

	@Get("json")
	public FileRepresentation getJson()
	{
		try
		{
			File file;

			if (StringUtils.isEmpty(locale))
			{
				file = getFromExternal("locales.json", "template");
			}
			else
			{
				file = getFromExternal(locale + ".json", "template");
			}

			if (file != null && file.exists() && file.isFile())
			{
				FileRepresentation representation = new FileRepresentation(file, MediaType.TEXT_PLAIN);
				representation.setSize(file.length());
				representation.setDisposition(new Disposition(Disposition.TYPE_ATTACHMENT));
				return representation;
			}
			else
			{
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
}
