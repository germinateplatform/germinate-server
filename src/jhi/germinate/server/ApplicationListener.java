package jhi.germinate.server;

import javax.servlet.*;

import jhi.germinate.server.util.watcher.PropertyWatcher;

public class ApplicationListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		PropertyWatcher.initialize();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		PropertyWatcher.stopFileWatcher();
	}
}
