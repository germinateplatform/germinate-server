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
import jhi.germinate.server.util.ImageExifReader;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.concurrent.*;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.Images.*;

public class ImageExifReaderTask implements Runnable
{
	private ThreadPoolExecutor executor;

	@Override
	public void run()
	{
		int cores = Runtime.getRuntime().availableProcessors();

		// If there are more than 2, leave one for handling of REST requests, otherwise use them all.
		if (cores > 2)
			cores /= 2;

		Logger.getLogger("").info("RUNNING IMAGE EXIF READER WITH " + cores + " CORES.");

		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(cores);

		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			context.selectFrom(IMAGES)
				   .where(IMAGES.EXIF.isNull())
				   .forEach(i -> {
					   try
					   {
							executor.submit(new ImageExifReader(i));
					   }
					   catch (Exception e)
					   {
						   e.printStackTrace();
					   }
				   });
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			while (!executor.awaitTermination(10, TimeUnit.SECONDS))
			{
				// Wait here
				Logger.getLogger("").log(Level.INFO, "Image Exif scanner queue active/count: " + executor.getActiveCount() + "/" + executor.getQueue().size());

				if (executor.getQueue().size() < 1 && executor.getActiveCount() < 1)
				{
					executor.shutdownNow();
				}
			}
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}
}