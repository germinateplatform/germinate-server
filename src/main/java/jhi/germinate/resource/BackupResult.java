package jhi.germinate.resource;

import jhi.germinate.server.Database;

import java.sql.Timestamp;

public class BackupResult
{
	private Timestamp           timestamp;
	private String              germinateVersion;
	private String              filename;
	private Database.BackupType type;
	private long                filesize;

	public BackupResult()
	{
	}

	public Timestamp getTimestamp()
	{
		return timestamp;
	}

	public BackupResult setTimestamp(Timestamp timestamp)
	{
		this.timestamp = timestamp;
		return this;
	}

	public String getGerminateVersion()
	{
		return germinateVersion;
	}

	public BackupResult setGerminateVersion(String germinateVersion)
	{
		this.germinateVersion = germinateVersion;
		return this;
	}

	public String getFilename()
	{
		return filename;
	}

	public BackupResult setFilename(String filename)
	{
		this.filename = filename;
		return this;
	}

	public long getFilesize()
	{
		return filesize;
	}

	public BackupResult setFilesize(long filesize)
	{
		this.filesize = filesize;
		return this;
	}

	public Database.BackupType getType()
	{
		return type;
	}

	public BackupResult setType(Database.BackupType type)
	{
		this.type = type;
		return this;
	}
}
