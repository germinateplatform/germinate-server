package jhi.germinate.server;

import org.flywaydb.core.*;
import org.flywaydb.core.api.*;
import org.jooq.*;
import org.jooq.conf.*;
import org.jooq.impl.*;

import java.sql.*;
import java.util.logging.*;

import jhi.germinate.server.database.*;

/**
 * @author Sebastian Raubach
 */
public class Database
{
	private static String databaseServer;
	private static String databaseName;
	private static String databasePort;
	private static String username;
	private static String password;

	public static void init(String databaseServer, String databaseName, String databasePort, String username, String password)
	{
		Database.databaseServer = databaseServer;
		Database.databaseName = databaseName;
		Database.databasePort = databasePort;
		Database.username = username;
		Database.password = password;

		try
		{
			// The newInstance() call is a work around for some
			// broken Java implementations
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		}
		catch (Exception ex)
		{
			// handle the error
		}

		// Get an initial connection to try if it works
		try (Connection conn = getConnection())
		{
			DSL.using(conn, SQLDialect.MYSQL).close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// Run database update/init
		try
		{
			Logger.getLogger("").log(Level.INFO, "RUNNING FLYWAY on: " + databaseName);
			Flyway flyway = new Flyway();
			flyway.setTable("schema_version");
			flyway.setValidateOnMigrate(false);
			flyway.setDataSource(getDatabaseUrl(), username, password);
			flyway.setLocations("classpath:jhi.germinate.server.util.databasemigration");
			flyway.setBaselineOnMigrate(true);
			flyway.migrate();
			flyway.repair();
		}
		catch (FlywayException e)
		{
			e.printStackTrace();
		}
	}

	private static String getDatabaseUrl()
	{
		return "jdbc:mysql://" + databaseServer + ":" + (databasePort != null ? databasePort : "3306") + "/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
	}

	public static Connection getConnection()
		throws SQLException
	{
		return DriverManager.getConnection(getDatabaseUrl(), username, password);
	}

	public static DSLContext getContext(Connection connection)
	{
		Settings settings = new Settings()
			.withRenderMapping(new RenderMapping()
				.withSchemata(
					new MappedSchema().withInput(GerminateTemplate_3_7_0.GERMINATE_TEMPLATE_3_7_0.getQualifiedName().first())
									  .withOutput(databaseName)));

		return DSL.using(connection, SQLDialect.MYSQL, settings);
	}

	public static String getDatabaseServer()
	{
		return databaseServer;
	}

	public static void setDatabaseServer(String databaseServer)
	{
		Database.databaseServer = databaseServer;
	}

	public static String getDatabaseName()
	{
		return databaseName;
	}

	public static void setDatabaseName(String databaseName)
	{
		Database.databaseName = databaseName;
	}
}