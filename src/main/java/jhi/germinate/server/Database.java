package jhi.germinate.server;

import com.zaxxer.hikari.HikariDataSource;
import jhi.germinate.server.database.codegen.GerminateDb;
import jhi.germinate.server.util.StringUtils;
import jhi.germinate.server.util.database.ScriptRunner;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.*;
import org.jooq.conf.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.TimeZone;
import java.util.logging.*;

import static jhi.germinate.server.database.codegen.tables.Germinatebase.*;

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

	private static HikariDataSource datasource;

	private static final String utc = TimeZone.getDefault().getID();

	public static void close()
	{
		if (datasource != null && !datasource.isClosed())
		{
			datasource.close();
			datasource = null;
		}
	}

	public static void init(String databaseServer, String databaseName, String databasePort, String username, String password, boolean initAndUpdate)
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
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
		}
		catch (Exception ex)
		{
			// handle the error
		}

		Database.datasource = new HikariDataSource();
		Database.datasource.setJdbcUrl(getDatabaseUrl());
		Database.datasource.setUsername(username);
		Database.datasource.setPassword(password);
		Database.datasource.addDataSourceProperty("maximumPoolSize", "5");
		Database.datasource.addDataSourceProperty("cachePrepStmts", "true");
		Database.datasource.addDataSourceProperty("prepStmtCacheSize", "250");
		Database.datasource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		Database.datasource.addDataSourceProperty("useServerPrepStmts", "true");
		Database.datasource.addDataSourceProperty("useLocalSessionState", "true");
		Database.datasource.addDataSourceProperty("rewriteBatchedStatements", "true");
		// Setting this to `true` would cause stored procedures that return varying number of columns
		// to be seen as always returning the number of columns as the first call to the stored procedure.
		Database.datasource.addDataSourceProperty("cacheResultSetMetadata", "false");
		Database.datasource.addDataSourceProperty("cacheServerConfiguration", "true");
		Database.datasource.addDataSourceProperty("elideSetAutoCommits", "true");
		Database.datasource.addDataSourceProperty("maintainTimeStats", "false");

		// Get an initial connection to try if it works. Attempt a connection 10 times before failing
		boolean connectionSuccessful = false;
		for (int attempt = 0; attempt < 10; attempt++)
		{
			try (Connection conn = getDirectConnection();
				 DSLContext context = Database.getContext(conn))
			{
				context.close();
				connectionSuccessful = true;
				break;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				Logger.getLogger("").severe(e.getLocalizedMessage());

				// If the attempt fails, wait 5 seconds before the next one
				try
				{
					Thread.sleep(5000);
				}
				catch (InterruptedException ex)
				{
					ex.printStackTrace();
				}
			}
		}

		if (!connectionSuccessful)
			throw new RuntimeException("Unable to connect to database after 10 attempts. Exiting.");

		if (initAndUpdate)
		{
			boolean databaseExists = true;
			try (DSLContext context = Database.getContext())
			{
				// Try and see if the `germinatebase` table exists
				context.selectFrom(GERMINATEBASE)
					   .fetchAny();
			}
			catch (DataAccessException e)
			{
				databaseExists = false;
			}

			if (!databaseExists)
			{
				// Set up the database initially
				try
				{
					URL url = Database.class.getClassLoader().getResource("jhi/germinate/server/util/database/init/db_setup.sql");

					if (url != null)
					{
						Logger.getLogger("").log(Level.INFO, "RUNNING DATABASE CREATION SCRIPT!");
						executeFile(new File(url.toURI()));
					}
					else
					{
						throw new IOException("Setup SQL file not found!");
					}
				}
				catch (IOException | URISyntaxException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Logger.getLogger("").log(Level.INFO, "DATABASE EXISTS, NO NEED TO CREATE IT!");
			}

			try (DSLContext context = Database.getContext())
			{
				context.execute("ALTER DATABASE `" + databaseName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
			}
			catch (DataAccessException e)
			{
				e.printStackTrace();
			}

			// Run database update
			try
			{
				Logger.getLogger("").log(Level.INFO, "RUNNING FLYWAY on: " + databaseName);
				Flyway flyway = Flyway.configure()
									  .table("schema_version")
									  .validateOnMigrate(false)
									  .dataSource(getDatabaseUrl(), username, password)
									  .locations("classpath:jhi/germinate/server/util/database/migration")
									  .baselineOnMigrate(true)
									  .load();
				flyway.migrate();
				flyway.repair();
			}
			catch (FlywayException e)
			{
				e.printStackTrace();
			}

			// Then create all views and stored procedures
			try
			{
				URL url = Database.class.getClassLoader().getResource("jhi/germinate/server/util/database/init/views_procedures.sql");

				if (url != null)
				{
					Logger.getLogger("").log(Level.INFO, "RUNNING VIEW/PROCEDURE CREATION SCRIPT!");
					executeFile(new File(url.toURI()));
				}
				else
				{
					throw new IOException("View/procedure SQL file not found!");
				}
			}
			catch (IOException | URISyntaxException e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void executeFile(File sqlFile)
	{
		try (Connection conn = Database.getDirectConnection();
			 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile), StandardCharsets.UTF_8)))
		{
			ScriptRunner runner = new ScriptRunner(conn, true, true);
			runner.runScript(br);
		}
		catch (SQLException | IOException e)
		{
			e.printStackTrace();
		}
	}

	private static String getDatabaseUrl()
	{
		return "jdbc:mysql://" + databaseServer + ":" + (StringUtils.isEmptyOrQuotes(databasePort) ? "3306" : databasePort) + "/" + databaseName + "?useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=" + utc;
	}

	private static Connection getDirectConnection()
		throws SQLException
	{
		return DriverManager.getConnection(getDatabaseUrl(), username, password);
	}

	public static DSLContext getContext()
	{
		Settings settings = new Settings()
			.withRenderMapping(new RenderMapping()
				.withSchemata(
					new MappedSchema().withInput(GerminateDb.GERMINATE_DB.getQualifiedName().first())
									  .withOutput(databaseName)));

		return DSL.using(datasource, SQLDialect.MYSQL, settings);
	}

	private static DSLContext getContext(Connection connection)
	{
		Settings settings = new Settings()
			.withRenderMapping(new RenderMapping()
				.withSchemata(
					new MappedSchema().withInput(GerminateDb.GERMINATE_DB.getQualifiedName().first())
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