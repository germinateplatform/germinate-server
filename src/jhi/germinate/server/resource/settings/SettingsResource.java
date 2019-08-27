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

package jhi.germinate.server.resource.settings;

import org.restlet.resource.*;

import jhi.germinate.resource.ClientConfiguration;
import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * {@link ServerResource} handling {@link SettingsResource} requests.
 *
 * @author Sebastian Raubach
 */
public class SettingsResource extends ServerResource
{
	@Get("json")
	public ClientConfiguration getJson()
	{
		ClientConfiguration result = new ClientConfiguration();
		result.setColorsCharts(PropertyWatcher.getPropertyList(ServerProperty.COLORS_CHART, String.class));
		result.setColorsTemplate(PropertyWatcher.getPropertyList(ServerProperty.COLORS_TEMPLATE, String.class));

		return result;
	}
}
