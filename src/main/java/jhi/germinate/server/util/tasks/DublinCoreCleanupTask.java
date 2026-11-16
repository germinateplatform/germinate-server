package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;
import jhi.germinate.server.database.pojo.DublinCore;
import jhi.germinate.server.util.CollectionUtils;
import org.jooq.DSLContext;

import java.sql.*;
import java.util.logging.Logger;

import static jhi.germinate.server.database.codegen.tables.Datasets.DATASETS;

public class DublinCoreCleanupTask implements Runnable
{
	@Override
	public void run()
	{
		try (Connection conn = Database.getConnection())
		{
			DSLContext context = Database.getContext(conn);

			context.selectFrom(DATASETS)
				   .where(DATASETS.DUBLIN_CORE.isNotNull())
				   .forEach(d -> {
					   DublinCore dublinCore = d.getDublinCore();
					   boolean changed = false;
					   // Now remove all [null] entries
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getTitle()))
					   {
						   dublinCore.setTitle(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getRights()))
					   {
						   dublinCore.setRights(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getDate()))
					   {
						   dublinCore.setDate(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getPublisher()))
					   {
						   dublinCore.setPublisher(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getFormat()))
					   {
						   dublinCore.setFormat(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getLanguage()))
					   {
						   dublinCore.setLanguage(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getSource()))
					   {
						   dublinCore.setSource(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getType()))
					   {
						   dublinCore.setType(null);
						   changed = true;
					   }
					   if (CollectionUtils.isEmptyOrNull(dublinCore.getSubject()))
					   {
						   dublinCore.setSubject(null);
						   changed = true;
					   }

					   if (changed)
					   {
						   d.setDublinCore(dublinCore);
						   d.store(DATASETS.DUBLIN_CORE);
					   }
				   });
		}
		catch (SQLException e)
		{
			Logger.getLogger("").severe(e.getMessage());
		}
	}
}
