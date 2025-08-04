package jhi.germinate.server;

import jhi.germinate.resource.enums.ServerProperty;
import jhi.germinate.server.database.codegen.GerminateDb;
import jhi.germinate.server.resource.ResourceUtils;
import jhi.germinate.server.util.*;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.*;
import org.jooq.*;
import org.jooq.conf.*;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.io.*;
import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.logging.*;

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

	private static boolean mysqlDumpExists = false;
	private static boolean backupsRanOnce  = false;
	private static boolean backupIsRunning = false;
	private static Object  lock            = new Object();

//	private static HikariDataSource datasource;

	private static final String utc = TimeZone.getDefault().getID();

	public static void close()
	{
//		Logger.getLogger("").info("CLOSE DATABASE " + (datasource != null && !datasource.isClosed()));
//		if (datasource != null && !datasource.isClosed())
//		{
//			datasource.close();
//			datasource = null;
//		}
	}

	public static boolean check(String databaseServer, String databaseName, String databasePort, String username, String password)
	{
		String oldDbServer = Database.databaseServer;
		String oldDbName = Database.databaseName;
		String oldDbPort = Database.databasePort;
		String oldUsername = Database.username;
		String oldPassword = Database.password;

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

		try (Connection conn = getConnection())
		{
			Database.getContext(conn);
			return true;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			Logger.getLogger("").severe(e.getLocalizedMessage());
			return false;
		}
		finally
		{
			Database.databaseServer = oldDbServer;
			Database.databaseName = oldDbName;
			Database.databasePort = oldDbPort;
			Database.username = oldUsername;
			Database.password = oldPassword;
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

//		Database.datasource = new HikariDataSource();
//		Database.datasource.setJdbcUrl(getDatabaseUrl());
//		Database.datasource.setUsername(username);
//		Database.datasource.setPassword(password);
//		Database.datasource.setMaxLifetime(10 * 60 * 1000);
//		Database.datasource.setMaximumPoolSize(7);
//		Database.datasource.addDataSourceProperty("cachePrepStmts", "true");
//		Database.datasource.addDataSourceProperty("prepStmtCacheSize", "250");
//		Database.datasource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
//		Database.datasource.addDataSourceProperty("useServerPrepStmts", "true");
//		Database.datasource.addDataSourceProperty("useLocalSessionState", "true");
//		Database.datasource.addDataSourceProperty("rewriteBatchedStatements", "true");
//		// Setting this to `true` would cause stored procedures that return varying number of columns
//		// to be seen as always returning the number of columns as the first call to the stored procedure.
//		Database.datasource.addDataSourceProperty("cacheResultSetMetadata", "false");
//		Database.datasource.addDataSourceProperty("cacheServerConfiguration", "true");
//		Database.datasource.addDataSourceProperty("elideSetAutoCommits", "true");
//		Database.datasource.addDataSourceProperty("maintainTimeStats", "false");

		// Get an initial connection to try if it works. Attempt a connection 10 times before failing
		boolean isDev = PropertyWatcher.getBoolean(ServerProperty.DEBUG_IS_DEVELOPMENT, false);
		int attemptCount = isDev ? 1 : 6;
		boolean connectionSuccessful = false;
		for (int attempt = 0; attempt < attemptCount; attempt++)
		{
			try (Connection conn = getConnection())
			{
				Database.getContext(conn);
				connectionSuccessful = true;
				break;
			}
			catch (SQLException e)
			{
				e.printStackTrace();
				Logger.getLogger("").severe(e.getLocalizedMessage());

				if (!isDev)
				{
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
		}

		if (!connectionSuccessful)
		{
			Logger.getLogger("").severe("Unable to connect to database after 10 attempts. Exiting.");
			return;
		}

		if (initAndUpdate)
		{
			boolean databaseExists = true;
			// Check if the germinatebase table exists
			try (Connection conn = getConnection();
				 PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(1) AS count FROM information_schema.tables WHERE table_schema = ? AND table_name = ?"))
			{
				stmt.setString(1, databaseName);
				stmt.setString(2, "germinatebase");
				ResultSet rs = stmt.executeQuery();

				while (rs.next())
					databaseExists = rs.getInt("count") > 0;

				rs.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
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

			// Convert the database to UTF-8
			try (Connection conn = getConnection())
			{
				DSLContext context = Database.getContext(conn);
				context.execute("ALTER DATABASE `" + databaseName + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;");
			}
			catch (SQLException | DataAccessException e)
			{
				e.printStackTrace();
			}

			try
			{
				List<String> params = new ArrayList<>();
				params.add(PropertyWatcher.get(ServerProperty.MYSQLDUMP_PATH, "mysqldump"));
				Process process = Runtime.getRuntime().exec(params.toArray(new String[0]));
				process.waitFor();
				process.destroy();

				mysqlDumpExists = true;
			}
			catch (Exception e)
			{
				mysqlDumpExists = false;
				Logger.getLogger("").severe(e.getLocalizedMessage());
				e.printStackTrace();
			}

			// Run database updates
			try
			{
				Logger.getLogger("").log(Level.INFO, "RUNNING FLYWAY on: " + databaseName);
				Flyway flyway = Flyway.configure()
									  .table("schema_version")
									  .validateOnMigrate(false)
									  .dataSource(getDatabaseUrl(false), username, password)
									  .locations("classpath:jhi/germinate/server/util/database/migration")
									  .baselineOnMigrate(true)
									  .load();

				MigrationInfo[] pending = flyway.info().pending();
				if (!CollectionUtils.isEmpty(pending))
				{
					Logger.getLogger("").info("MIGRATIONS PENDING: " + pending.length);
					// There are pending DB updates/migrations. Run a backup first.
					attemptDatabaseDump(BackupType.UPDATE);
				}
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

			try
			{
				DatabaseUpdateFixer.migrateClimateOverlayImages();
			}
			catch (IOException | SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static File attemptDatabaseDump(BackupType type)
	{
		synchronized (lock)
		{
			if (backupIsRunning)
				return null;

			if (mysqlDumpExists)
			{
				if (backupsRanOnce && type == BackupType.UPDATE)
					return null;

				backupIsRunning = true;

				try
				{
					// Remember we already ran this for an update
					backupsRanOnce |= type == BackupType.UPDATE;

					String dt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
					dt += "_" + type.name().toLowerCase() + "_" + Database.class.getPackage().getImplementationVersion();
					File dbBackupFile = ResourceUtils.getFromExternal(null, dt + ".sql", "backups");
					dbBackupFile.getParentFile().mkdirs();

					List<String> params = new ArrayList<>();
					params.add(PropertyWatcher.get(ServerProperty.MYSQLDUMP_PATH, "mysqldump"));
					// Attempt a database backup
					params.add("-h");
					params.add(databaseServer);

					params.add("-u");
					params.add(username);

					if (!StringUtils.isEmpty(password))
						params.add("-p" + password);

					if (!StringUtils.isEmpty(databasePort))
					{
						params.add("-P");
						params.add(databasePort);
					}

					params.add("-r");
					params.add(dbBackupFile.getAbsolutePath());

					params.add(databaseName);

					ProcessBuilder pb = new ProcessBuilder(params);
					pb.redirectErrorStream(true);
					Process process = pb.start();
					int exitCode = process.waitFor();

					if (exitCode == 0)
						Logger.getLogger("").info("DATABASE BACKUP SUCCESSFUL: " + dbBackupFile.getAbsolutePath());
					else
						Logger.getLogger("").severe("DATABASE BACKUP UNSUCCESSFUL!");

					process.destroy();

					File zip = ResourceUtils.getFromExternal(null, dt + ".zip", "backups");
					FileUtils.zipUp(zip, List.of(new File(dbBackupFile.getAbsolutePath())), true);

					backupIsRunning = false;
					return zip;
				}
				catch (Exception e)
				{
					Logger.getLogger("").severe(e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

			backupIsRunning = false;

			return null;
		}
	}

	/**
	 * Executes an .sql file against the database
	 *
	 * @param sqlFile The file to execute
	 */
	private static void executeFile(File sqlFile)
	{
		try (Connection conn = getConnection();
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

	/**
	 * Creates and returns the database connection string
	 *
	 * @param allowStreaming Should streaming of results be allowed?
	 * @return
	 */
	private static String getDatabaseUrl(boolean allowStreaming)
	{
		return "jdbc:mysql://"
				+ databaseServer
				+ ":"
				+ (StringUtils.isEmptyOrQuotes(databasePort) ? "3306" : databasePort)
				+ "/"
				+ databaseName
				+ "?allowMultiQueries=true&useUnicode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true&autoReconnect=true&serverTimezone="
				+ utc
				+ (allowStreaming ? "&useCursorFetch=true" : "");
	}

	/**
	 * Get a database connection
	 *
	 * @return The {@link Connection} to the database
	 * @throws SQLException thrown if the database interaction fails
	 */
	public static Connection getConnection()
			throws SQLException
	{
		return getConnection(false);
	}

	/**
	 * Get a database connection
	 *
	 * @param allowStreaming Should streaming of results be allowed?
	 * @return The {@link Connection} to the database
	 * @throws SQLException thrown if the database interaction fails
	 */
	public static Connection getConnection(boolean allowStreaming)
			throws SQLException
	{
		return DriverManager.getConnection(getDatabaseUrl(allowStreaming), username, password);
	}

//	public static DSLContext getContext()
//	{
//		Settings settings = new Settings()
//			.withRenderMapping(new RenderMapping()
//				.withSchemata(
//					new MappedSchema().withInput(GerminateDb.GERMINATE_DB.getQualifiedName().first())
//									  .withOutput(databaseName)));
//
//		return DSL.using(datasource, SQLDialect.MYSQL, settings);
//	}

	/**
	 * Gets the {@link DSLContext} based on the given connection. This allows jOOQ queries.
	 *
	 * @param connection The active {@link Connection}
	 * @return The {@link DSLContext} based on the given connection.
	 */
	public static DSLContext getContext(Connection connection)
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

	public static enum BackupType
	{
		UPDATE,
		PERIODICAL,
		MANUAL
	}
}