package jhi.germinate.server;

import javax.servlet.*;

import jhi.germinate.server.gatekeeper.*;
import jhi.germinate.server.resource.*;

public class ApplicationListener implements ServletContextListener
{
	@Override
	public void contextInitialized(ServletContextEvent sce)
	{
		ServletContext ctx = sce.getServletContext();
		String databaseServer = ctx.getInitParameter("database-server");
		String databaseName = ctx.getInitParameter("database-name");
		String databasePort = ctx.getInitParameter("database-port");
		String username = ctx.getInitParameter("username");
		String password = ctx.getInitParameter("password");
		Database.init(databaseServer, databaseName, databasePort, username, password);

		String gatekeeperUrl = ctx.getInitParameter("gatekeeper-url");
		String gatekeeperUsername = ctx.getInitParameter("gatekeeper-username");
		String gatekeeperPassword = ctx.getInitParameter("gatekeeper-password");
		GatekeeperClient.init(gatekeeperUrl, gatekeeperUsername, gatekeeperPassword);

		Integer salt;
		try
		{
			salt = Integer.parseInt(ctx.getInitParameter("salt"));
		}
		catch (Exception e)
		{
			salt = 10;
		}

		TokenResource.SALT = salt;
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
	}
}
