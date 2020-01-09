package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.gatekeeper.GatekeeperClient;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.watcher.PropertyWatcher;

/**
 * @author Sebastian Raubach
 */
public class GatekeeperUserUpdaterTask implements Runnable
{
	@Override
	public void run()
	{
		if (!StringUtils.isEmpty(PropertyWatcher.get(ServerProperty.GATEKEEPER_URL)))
			GatekeeperClient.getUsersFromGatekeeper();
	}
}
