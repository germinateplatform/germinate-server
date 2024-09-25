/*
 *  Copyright 2018 Information and Computational Sciences,
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

package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.codegen.tables.pojos.Datasets;
import jhi.germinate.server.resource.settings.SettingsResource;
import org.jooq.DSLContext;

import java.sql.Connection;
import java.util.*;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.Climates.CLIMATES;
import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;
import static jhi.germinate.server.database.codegen.tables.Experiments.EXPERIMENTS;
import static jhi.germinate.server.database.codegen.tables.Fileresources.FILERESOURCES;
import static jhi.germinate.server.database.codegen.tables.Germinatebase.GERMINATEBASE;
import static jhi.germinate.server.database.codegen.tables.Images.IMAGES;
import static jhi.germinate.server.database.codegen.tables.Locations.LOCATIONS;
import static jhi.germinate.server.database.codegen.tables.Maps.MAPS;
import static jhi.germinate.server.database.codegen.tables.Markers.MARKERS;
import static jhi.germinate.server.database.codegen.tables.Phenotypes.PHENOTYPES;
import static jhi.germinate.server.database.codegen.tables.Projects.PROJECTS;
import static jhi.germinate.server.database.codegen.tables.Publications.PUBLICATIONS;
import static jhi.germinate.server.database.codegen.tables.Stories.STORIES;

public class HiddenPagesAutodiscoverTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			int germplasm = context.selectCount().from(GERMINATEBASE).fetchOne(0, int.class);
			int traits = context.selectCount().from(PHENOTYPES).fetchOne(0, int.class);
			int climates = context.selectCount().from(CLIMATES).fetchOne(0, int.class);
			int markers = context.selectCount().from(MARKERS).fetchOne(0, int.class);
			int maps = context.selectCount().from(MAPS).fetchOne(0, int.class);
			int locations = context.selectCount().from(LOCATIONS).fetchOne(0, int.class);
			int experiments = context.selectCount().from(EXPERIMENTS).fetchOne(0, int.class);
			int dataResources = context.selectCount().from(FILERESOURCES).fetchOne(0, int.class);
			int images = context.selectCount().from(IMAGES).fetchOne(0, int.class);
			int publications = context.selectCount().from(PUBLICATIONS).fetchOne(0, int.class);
			int stories = context.selectCount().from(STORIES).fetchOne(0, int.class);
			int projects = context.selectCount().from(PROJECTS).fetchOne(0, int.class);

			Set<String> hiddenPages = new HashSet<>();
			if (germplasm < 1)
			{
				hiddenPages.add("germplasm");
				hiddenPages.add("germplasm-unifier");
				hiddenPages.add("passport");
			}
			if (traits < 1)
			{
				hiddenPages.add("traits");
				hiddenPages.add("trait-details");
				hiddenPages.add("export-trials");
			}
			if (climates < 1)
			{
				hiddenPages.add("climates");
				hiddenPages.add("climate-details");
				hiddenPages.add("export-climates");
			}
			if (markers < 1)
			{
				hiddenPages.add("markers");
				hiddenPages.add("marker-details");
				hiddenPages.add("maps");
				hiddenPages.add("map-details");
				hiddenPages.add("export-genotypes");
				hiddenPages.add("export-allelefrequency");
			}
			if (maps < 1)
			{
				hiddenPages.add("maps");
				hiddenPages.add("map-details");
			}
			if (locations < 1)
			{
				hiddenPages.add("locations");
				hiddenPages.add("geographic-search");
			}
			if (experiments < 1)
			{
				hiddenPages.add("experiments");
				hiddenPages.add("experiment-details");
			}
			if (dataResources < 1)
			{
				hiddenPages.add("data-resources");
			}
			if (images < 1)
			{
				hiddenPages.add("images");
			}
			if (publications < 1)
			{
				hiddenPages.add("publications");
				hiddenPages.add("publication-details");
			}
			if (stories < 1)
			{
				hiddenPages.add("stories");
			}
			if (projects < 1)
			{
				hiddenPages.add("projects");
				hiddenPages.add("projectDetails");
			}

			List<Datasets> datasets = context.selectFrom(DATASETS).fetchInto(Datasets.class);

			if (datasets.stream().noneMatch(d -> d.getDatasettypeId() == 1))
				hiddenPages.add("export-genotypes");
			if (datasets.stream().noneMatch(d -> d.getDatasettypeId() == 3))
				hiddenPages.add("export-trials");
			if (datasets.stream().noneMatch(d -> d.getDatasettypeId() == 4))
				hiddenPages.add("export-allelefrequency");
			if (datasets.stream().noneMatch(d -> d.getDatasettypeId() == 5))
				hiddenPages.add("export-climates");

			Logger.getLogger("").log(Level.INFO, "Hidden page autodiscovery: " + hiddenPages);

			SettingsResource.AUTO_DISCOVERY_HIDDEN_PAGES = hiddenPages;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}