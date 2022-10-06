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
import jhi.germinate.server.database.codegen.enums.DataExportJobsStatus;
import jhi.germinate.server.util.*;
import org.jooq.DSLContext;

import java.io.File;
import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.DataExportJobs.*;

public class DatasetExportJobCheckerTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			context.selectFrom(DATA_EXPORT_JOBS)
				   .where(DATA_EXPORT_JOBS.STATUS.notEqual(DataExportJobsStatus.completed))
				   .forEach(j -> {
					   try
					   {
						   boolean finished = ApplicationListener.SCHEDULER.isJobFinished(j.getJobId());

						   if (finished && j.getStatus() == DataExportJobsStatus.running)
						   {
							   String uuid = j.getUuid();
							   File jobFolder = ApplicationListener.getFromExternal(uuid, "async");

							   // Get zip result files (there'll only be one per folder)
							   File[] zipFiles = jobFolder.listFiles((dir, name) -> name.endsWith(".zip"));

							   if (!CollectionUtils.isEmpty(zipFiles))
								   j.setResultSize(zipFiles[0].length());

							   j.setStatus(DataExportJobsStatus.completed);
							   j.store();
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