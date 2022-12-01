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
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.*;

import static jhi.germinate.server.database.codegen.tables.Userfeedback.*;

/**
 * Runnable that checks submitted user feedback and removes their email address if the feedback has been marked as read and 30 days have elapsed.
 */
public class UserFeedbackEmailRemovalTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);
			context.selectFrom(USERFEEDBACK)
				   .where(USERFEEDBACK.IS_NEW.eq(false))
				   .and(DSL.dateDiff(new Date(System.currentTimeMillis()), DSL.date(USERFEEDBACK.UPDATED_ON)).ge(30))
				   .forEach(uf -> {
					   uf.setContactEmail("");
					   uf.store(USERFEEDBACK.CONTACT_EMAIL);
				   });
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}