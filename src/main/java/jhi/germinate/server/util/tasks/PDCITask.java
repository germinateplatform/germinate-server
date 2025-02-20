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
import jhi.germinate.server.database.codegen.tables.pojos.*;
import jhi.germinate.server.database.codegen.tables.records.*;
import jhi.germinate.server.util.StringUtils;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.util.*;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;
import static jhi.germinate.server.database.codegen.tables.Links.*;
import static jhi.germinate.server.database.codegen.tables.Linktypes.*;
import static jhi.germinate.server.database.codegen.tables.Locations.*;
import static jhi.germinate.server.database.codegen.tables.Mcpd.*;
import static jhi.germinate.server.database.codegen.tables.Pedigreedefinitions.*;
import static jhi.germinate.server.database.codegen.tables.Pedigrees.*;
import static jhi.germinate.server.database.codegen.tables.Taxonomies.*;

/**
 * Calculates Passport Data Completeness Index (PDCI) for all accessions.
 *
 * <p>
 * Theo van Hintum, Frank Menting and Elisabeth van Strien (2011). <b>Quality indicators for passport data in ex situ genebanks.</b> Plant Genetic Resources, 9, pp 478-485.
 * <p>
 * doi:10.1017/S1479262111000682
 */
public class PDCITask implements Runnable
{
	private Map<Integer, Taxonomies> taxonomies;
	private Map<Integer, Locations>  locations;

	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			long start = System.currentTimeMillis();
			final List<Integer> hasLink;

			// First, check if there's a generic link for all accessions
			boolean hasGenericLink = context.selectCount()
											.from(LINKS.leftJoin(LINKTYPES).on(LINKS.LINKTYPE_ID.eq(LINKTYPES.ID)))
											.where(LINKTYPES.TARGET_TABLE.eq("germinatebase"))
											.and(LINKTYPES.PLACEHOLDER.isNotNull())
											.fetchAnyInto(Integer.class) != 0;

			// If there isn't then check individuals
			if (!hasGenericLink)
			{
				hasLink = context.selectDistinct(GERMINATEBASE.ID)
								 .from(GERMINATEBASE)
								 .leftJoin(LINKS).on(LINKS.FOREIGN_ID.eq(GERMINATEBASE.ID))
								 .leftJoin(LINKTYPES).on(LINKTYPES.ID.eq(LINKS.LINKTYPE_ID))
								 .where(LINKTYPES.TARGET_TABLE.eq("germinatebase"))
								 .fetchInto(Integer.class);
			}
			else
			{
				hasLink = new ArrayList<>();
			}

			List<Integer> hasPedigree = context.selectDistinct(DSL.field("id"))
											   .from(DSL.selectDistinct(PEDIGREEDEFINITIONS.GERMINATEBASE_ID.as("id")).from(PEDIGREEDEFINITIONS)
														.unionAll(DSL.selectDistinct(PEDIGREES.GERMINATEBASE_ID.as("id")).from(PEDIGREES)))
											   .fetchInto(Integer.class);
			List<Integer> hasStorage = context.selectDistinct(MCPD.GERMINATEBASE_ID)
											  .from(MCPD)
											  .where(MCPD.STORAGE.isNotNull())
											  .fetchInto(Integer.class);

			Map<Integer, McpdRecord> mcpds = context.selectFrom(MCPD)
												   .fetchMap(MCPD.GERMINATEBASE_ID);

			taxonomies = context.selectFrom(TAXONOMIES).fetchMap(TAXONOMIES.ID, Taxonomies.class);
			locations = context.selectFrom(LOCATIONS).fetchMap(LOCATIONS.ID, Locations.class);

			Logger.getLogger("").log(Level.INFO, "PDCI calculation caches created in " + (System.currentTimeMillis() - start) + "ms");
			Logger.getLogger("").log(Level.INFO, "PDCI calculation generic link: " + hasGenericLink);
			Logger.getLogger("").log(Level.INFO, "PDCI calculation link cache: " + hasLink.size());
			Logger.getLogger("").log(Level.INFO, "PDCI calculation pedigree cache: " + hasPedigree.size());
			Logger.getLogger("").log(Level.INFO, "PDCI calculation storage cache: " + hasStorage.size());

			List<Germinatebase> accessions = context.selectFrom(GERMINATEBASE)
					.where(GERMINATEBASE.ENTITYTYPE_ID.eq(1)) // Just the accessions
					.fetchInto(Germinatebase.class);

			for (Germinatebase g : accessions) {
				boolean gLink = false;
				boolean gPedigree = false;
				boolean gStorage = false;
				if (hasGenericLink || hasLink.contains(g.getId())) {
					hasLink.remove((g.getId()));
					gLink = true;
				}
				if (hasPedigree.contains(g.getId())) {
					hasPedigree.remove(g.getId());
					gPedigree = true;
				}
				if (hasStorage.contains(g.getId())) {
					hasStorage.remove(g.getId());
					gStorage = true;
				}

				McpdRecord mcpd = mcpds.get(g.getId());

				double value = calculateGenericPart(g, mcpd, gLink, gStorage);

				switch ((mcpd != null && mcpd.getSampstat() != null) ? (mcpd.getSampstat() / 100) : -1) {
					case 1:
					case 2:
						value += calculateWildWeedy(g, mcpd);
						break;
					case 3:
						value += calculateLandrace(g, mcpd, gPedigree);
						break;
					case 4:
						value += calculateBreedingMaterial(g, mcpd, gPedigree);
						break;
					case 5:
						value += calculateCultivar(g, mcpd, gPedigree);
						break;
					default:
						value += calculateOther(g, mcpd, gPedigree);
						break;
				}

				// Divide by 100 to get a value between 0 and 10.
				value /= 100d;

				context.update(GERMINATEBASE).set(GERMINATEBASE.PDCI, value).where(GERMINATEBASE.ID.eq(g.getId())).execute();
			}

			Logger.getLogger("").log(Level.INFO, "PDCI calculation complete in " + (System.currentTimeMillis() - start) + "ms");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private int calculateGenericPart(Germinatebase acc, McpdRecord mcpd, boolean hasLink, boolean hasStorage)
	{
		int value = 0;

		Taxonomies taxonomy = acc.getTaxonomyId() != null ? taxonomies.get(acc.getTaxonomyId()) : null;

		if (taxonomy != null && !StringUtils.isEmpty(taxonomy.getGenus()))
		{
			value += 120;

			if (!StringUtils.isEmpty(taxonomy.getSpecies()))
			{
				value += 80;


				if (!StringUtils.isEmpty(taxonomy.getSpeciesAuthor()))
					value += 5;
				if (!StringUtils.isEmpty(taxonomy.getSubtaxa()))
					value += 40;
				if (!StringUtils.isEmpty(taxonomy.getSubtaxaAuthor()))
					value += 5;
				if (!StringUtils.isEmpty(taxonomy.getCropname()))
					value += 45;
			}
		}

		if (!StringUtils.isEmpty(mcpd.getAcqdate()))
			value += 10;

		if (mcpd.getSampstat() != null)
			value += 80;
		if (!StringUtils.isEmpty(mcpd.getDonorcode()))
			value += 40;
		else if (!StringUtils.isEmpty(mcpd.getDonorname()))
			value += 20;
		if (!StringUtils.isEmpty(mcpd.getDonornumb()))
		{
			if (StringUtils.isEmpty(mcpd.getDonorcode()) && StringUtils.isEmpty(mcpd.getDonorname()))
				value += 20;
			else
				value += 40;
		}
		if (!StringUtils.isEmpty(mcpd.getOthernumb()))
			value += 35;
		if (!StringUtils.isEmpty(mcpd.getDuplsite()))
			value += 30;
		else if (!StringUtils.isEmpty(mcpd.getDuplinstname()))
			value += 15;

		if (hasStorage)
			value += 15;

		if (hasLink)
			value += 40;

		if (mcpd.getMlsstat() != null)
			value += 15;

		return value;
	}

	private int calculateOther(Germinatebase acc, McpdRecord mcpd, boolean hasPedigree)
	{
		int value = 0;

		if (acc.getLocationId() != null)
		{
			Locations location = locations.get(acc.getLocationId());
			if (location.getCountryId() != null)
				value += 40;

			if (!StringUtils.isEmpty(location.getSiteName()))
			{
				if (location.getLatitude() == null || location.getLongitude() == null)
					value += 20;
				else
					value += 10;
			}

			if (location.getLatitude() != null && location.getLongitude() != null)
				value += 30;

			if (location.getElevation() != null)
				value += 5;
		}

		if (mcpd.getColldate() != null)
			value += 10;

		if (!StringUtils.isEmpty(mcpd.getBredcode()))
			value += 10;
		else if (!StringUtils.isEmpty(mcpd.getBredname()))
			value += 10;

		if (hasPedigree)
			value += 40;

		if (mcpd.getCollsrc() != null)
			value += 25;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 40;

		if (!StringUtils.isEmpty(mcpd.getCollnumb()))
			value += 20;

		if (!StringUtils.isEmpty(mcpd.getCollcode()))
			value += 20;
		else if (!StringUtils.isEmpty(mcpd.getCollname()))
			value += 10;

		return value;
	}

	private int calculateCultivar(Germinatebase acc, McpdRecord mcpd, boolean hasPedigree)
	{
		int value = 0;

		if (acc.getLocationId() != null)
		{
			Locations location = locations.get(acc.getLocationId());

			if (location.getCountryId() != null)
				value += 40;
		}

		if (!StringUtils.isEmpty(mcpd.getBredcode()))
			value += 80;
		else if (!StringUtils.isEmpty(mcpd.getBredname()))
			value += 40;

		if (hasPedigree)
			value += 100;

		if (mcpd.getCollsrc() != null)
			value += 20;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 160;

		return value;
	}

	private int calculateBreedingMaterial(Germinatebase acc, McpdRecord mcpd, boolean hasPedigree)
	{
		int value = 0;

		if (acc.getLocationId() != null)
		{
			Locations location = locations.get(acc.getLocationId());

			if (location.getCountryId() != null)
				value += 40;
		}

		if (!StringUtils.isEmpty(mcpd.getBredcode()))
			value += 110;
		else if (!StringUtils.isEmpty(mcpd.getBredname()))
			value += 55;

		if (hasPedigree)
			value += 150;

		if (mcpd.getCollsrc() != null)
			value += 20;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 80;

		return value;
	}

	private int calculateLandrace(Germinatebase acc, McpdRecord mcpd, boolean hasPedigree)
	{
		int value = 0;

		if (acc.getLocationId() != null)
		{
			Locations location = locations.get(acc.getLocationId());
			if (location.getCountryId() != null)
				value += 80;

			if (!StringUtils.isEmpty(location.getSiteName()))
			{
				if (location.getLatitude() == null || location.getLongitude() == null)
					value += 45;
				else
					value += 15;
			}

			if (location.getLatitude() != null && location.getLongitude() != null)
				value += 80;

			if (location.getElevation() != null)
				value += 15;
		}

		if (mcpd.getColldate() != null)
			value += 30;

		if (hasPedigree)
			value += 10;

		if (mcpd.getCollsrc() != null)
			value += 50;

		if (!StringUtils.isEmpty(acc.getNumber()))
			value += 50;

		if (!StringUtils.isEmpty(mcpd.getCollnumb()))
			value += 40;

		if (!StringUtils.isEmpty(mcpd.getCollcode()))
			value += 30;
		else if (!StringUtils.isEmpty(mcpd.getCollname()))
			value += 15;

		return value;
	}

	private int calculateWildWeedy(Germinatebase acc, McpdRecord mcpd)
	{
		int value = 0;

		if (acc.getLocationId() != null)
		{
			Locations location = locations.get(acc.getLocationId());
			if (location.getCountryId() != null)
				value += 80;

			if (!StringUtils.isEmpty(location.getSiteName()))
			{
				if (location.getLatitude() == null || location.getLongitude() == null)
					value += 70;
				else
					value += 20;
			}

			if (location.getLatitude() != null && location.getLongitude() != null)
				value += 120;

			if (location.getElevation() != null)
				value += 20;
		}

		if (mcpd.getColldate() != null)
			value += 30;

		if (mcpd.getCollsrc() != null)
			value += 30;

		if (!StringUtils.isEmpty(mcpd.getCollnumb()))
			value += 60;

		if (!StringUtils.isEmpty(mcpd.getCollcode()))
			value += 40;
		else if (!StringUtils.isEmpty(mcpd.getCollname()))
			value += 20;

		return value;
	}
}