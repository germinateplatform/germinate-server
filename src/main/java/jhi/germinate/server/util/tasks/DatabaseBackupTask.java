package jhi.germinate.server.util.tasks;

import jhi.germinate.server.Database;

/**
 * @author Sebastian Raubach
 */
public class DatabaseBackupTask implements Runnable
{
	@Override
	public void run()
	{
		Database.attemptDatabaseDump(Database.BackupType.PERIODICAL);
	}
}
