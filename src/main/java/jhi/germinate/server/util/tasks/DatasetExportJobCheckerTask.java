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

import org.jooq.DSLContext;

import java.sql.*;

import jhi.germinate.server.*;
import jhi.germinate.server.database.enums.DatasetExportJobsStatus;

import static jhi.germinate.server.database.tables.DatasetExportJobs.*;

public class DatasetExportJobCheckerTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection();
			 DSLContext context = Database.getContext(conn))
		{
			context.selectFrom(DATASET_EXPORT_JOBS)
				   .forEach(j -> {
					   try
					   {
						   boolean finished = ApplicationListener.SCHEDULER.isJobFinished(j.getJobId());

						   if (finished)
						   {
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
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}