package jhi.germinate.server.util.tasks;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.GatekeeperClient;
import jhi.germinate.server.util.*;

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
