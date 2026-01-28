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
import jhi.germinate.server.util.StringUtils;
import lombok.*;
import lombok.experimental.Accessors;
import okhttp3.OkHttpClient;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Taxonomies.TAXONOMIES;
import static jhi.germinate.server.database.codegen.tables.Taxonomyproviders.TAXONOMYPROVIDERS;
import static jhi.germinate.server.database.codegen.tables.Taxonomyproviderslinks.TAXONOMYPROVIDERSLINKS;

public class NCBITaxonomyLookupTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			List<Taxonomies> taxonomies = context.selectFrom(TAXONOMIES)
												 .whereNotExists(DSL.selectOne().from(TAXONOMYPROVIDERSLINKS)
																	.where(TAXONOMYPROVIDERSLINKS.TAXONOMY_ID.eq(TAXONOMIES.ID)))
												 .fetchInto(Taxonomies.class);

			Taxonomyproviders ncbi = context.selectFrom(TAXONOMYPROVIDERS)
											.where(TAXONOMYPROVIDERS.NAME.eq("NCBI"))
											.fetchAnyInto(Taxonomyproviders.class);

			if (ncbi == null)
			{
				Logger.getLogger("").severe("NCBI Taxonomy provider not found!");
				return;
			}

			Map<String, Integer> map = new HashMap<>();
			taxonomies.stream()
					  .map(t -> {
						  String s = t.getGenus();
						  if (!StringUtils.isEmpty(t.getSpecies()))
							  s += " " + t.getSpecies();

						  if (!StringUtils.isEmpty(t.getSubtaxa()))
							  s += " " + t.getSubtaxa();

						  map.put(s, t.getId());

						  return s;
					  })
					  .toList();

			// Create the HTTP client with the pool and timeouts
			OkHttpClient.Builder builder = new OkHttpClient.Builder()
					.readTimeout(20, TimeUnit.SECONDS)
					.callTimeout(20, TimeUnit.SECONDS)
					.connectTimeout(20, TimeUnit.SECONDS)
					.writeTimeout(20, TimeUnit.SECONDS)
					.retryOnConnectionFailure(true);

			OkHttpClient client = builder.build();

			// Create the retrofit instance
			Retrofit retrofit = (new Retrofit.Builder()).baseUrl("https://api.ncbi.nlm.nih.gov/datasets/v2/")
														.addConverterFactory(GsonConverterFactory.create())
														.client(client)
														.build();

			// Create an instance of the service interface
			NCBITaxonomyService service = retrofit.create(NCBITaxonomyService.class);

			try
			{
				Response<NCBIResponse> response = service.postTaxonomies(new NCBIRequest(map.keySet().toArray(new String[0]))).execute();

				if (response.isSuccessful() && response.body() != null)
				{
					for (Node item : response.body().taxonomy_nodes)
					{
						if (item.taxonomy != null && item.query != null && item.query.length > 0)
						{
							Integer id = map.get(item.query[0]);

							if (id != null)
							{
								context.insertInto(TAXONOMYPROVIDERSLINKS)
									   .set(TAXONOMYPROVIDERSLINKS.TAXONOMY_ID, id)
									   .set(TAXONOMYPROVIDERSLINKS.TAXONOMYPROVIDER_ID, ncbi.getId())
									   .set(TAXONOMYPROVIDERSLINKS.EXTERNAL_ID, Integer.toString(item.taxonomy.tax_id))
									   .execute();
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class NCBIRequest
	{
		private String[] taxons;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class NCBIResponse
	{
		private Node[] taxonomy_nodes;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class Node
	{
		private String[]     query;
		private TaxonomyItem taxonomy;
	}

	@NoArgsConstructor
	@Getter
	@Setter
	@Accessors(chain = true)
	@ToString
	private static class TaxonomyItem
	{
		private Integer tax_id;
	}

	private interface NCBITaxonomyService
	{
		@POST("taxonomy")
		Call<NCBIResponse> postTaxonomies(@Body NCBIRequest request);
	}
}