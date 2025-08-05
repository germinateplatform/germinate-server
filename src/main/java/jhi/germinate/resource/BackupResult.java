package jhi.germinate.resource;

import jhi.germinate.server.Database;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class BackupResult
{
	private Timestamp           timestamp;
	private String              germinateVersion;
	private String              filename;
	private Database.BackupType type;
	private long                filesize;
}
