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

import jhi.germinate.server.*;
import jhi.germinate.server.database.codegen.enums.DatasetExportJobsStatus;
import jhi.germinate.server.resource.BaseServerResource;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.DSLContext;

import java.io.File;
import java.sql.SQLException;

import static jhi.germinate.server.database.codegen.tables.DatasetExportJobs.*;

public class DatasetExportJobCheckerTask implements Runnable
{
	@Override
	public void run()
	{
		try (DSLContext context = Database.getContext())
		{
			context.selectFrom(DATASET_EXPORT_JOBS)
				   .where(DATASET_EXPORT_JOBS.STATUS.notEqual(DatasetExportJobsStatus.completed))
				   .forEach(j -> {
					   try
					   {
						   boolean finished = ApplicationListener.SCHEDULER.isJobFinished(j.getJobId());

						   if (finished)
						   {
							   String uuid = j.getUuid();
							   File jobFolder = BaseServerResource.getFromExternal(uuid, "async");

							   // Get zip result files (there'll only be one per folder)
							   File[] zipFiles = jobFolder.listFiles((dir, name) -> name.endsWith(".zip"));

							   if (!CollectionUtils.isEmpty(zipFiles))
								   j.setResultSize(zipFiles[0].length());

							   j.setStatus(DatasetExportJobsStatus.completed);
							   j.store();
						   }
					   }
					   catch (Exception e)
					   {
						   e.printStackTrace();
					   }
				   });
		}
	}
}