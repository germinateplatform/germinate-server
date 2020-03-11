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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jooq.DSLContext;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.util.List;

import jhi.germinate.resource.ImportResult;
import jhi.germinate.server.*;
import jhi.germinate.server.database.enums.DataImportJobsStatus;
import jhi.germinate.server.resource.BaseServerResource;

import static jhi.germinate.server.database.tables.DataImportJobs.*;

public class DatasetImportJobCheckerTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			context.selectFrom(DATA_IMPORT_JOBS)
				   .where(DATA_IMPORT_JOBS.STATUS.notEqual(DataImportJobsStatus.completed))
				   .forEach(j -> {
					   try
					   {
						   boolean finished = ApplicationListener.SCHEDULER.isJobFinished(j.getJobId());

						   if (finished)
						   {
							   j.setStatus(DataImportJobsStatus.completed);


							   String uuid = j.getUuid();
							   File jobFolder = BaseServerResource.getFromExternal(uuid, "async");

							   // Check if the json status file exists
							   File json = new File(jobFolder, uuid + ".json");
							   if (json.exists() && json.isFile())
							   {
								   try (BufferedReader br = Files.newBufferedReader(json.toPath()))
								   {
									   // Parse it
									   List<ImportResult> feedback = new Gson().fromJson(br, new TypeToken<List<ImportResult>>()
									   {
									   }.getType());

									   // Save it
									   j.setFeedback(feedback.toArray(new ImportResult[0]));
								   }
								   catch (IOException e)
								   {
									   e.printStackTrace();
								   }
							   }
							   else
							   {
								   // Set it to an empty array if no file exists
								   j.setFeedback(new ImportResult[0]);
							   }

							   j.store(DATA_IMPORT_JOBS.STATUS, DATA_IMPORT_JOBS.FEEDBACK);
						   }
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
	}
}