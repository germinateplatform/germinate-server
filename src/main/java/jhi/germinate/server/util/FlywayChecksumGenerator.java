package jhi.germinate.server.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.CRC32;

/**
 * @author Sebastian Raubach
 */
public class FlywayChecksumGenerator
{
	public static void main(String[] args)
		throws IOException
	{
		System.out.println(getChecksum(new File("src/main/resources/jhi/germinate/server/util/database/migration/V4.22.08.10__update.sql")));
	}

	/**
	 * Returns the checksum int for a given Flyway migration SQL file
	 *
	 * @param file The SQL file
	 * @return The checksum int
	 * @throws IOException Thrown if the interaction with the file fails
	 */
	public static int getChecksum(File file)
		throws IOException
	{
		// Read the file as UTF8
		try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8))
		{
			// Create a new checksum
			CRC32 crc32 = new CRC32();

			// Add each line trimmed
			for (String line; ((line = br.readLine()) != null); )
				crc32.update(line.trim().getBytes(StandardCharsets.UTF_8));

			// Return value
			return (int) crc32.getValue();
		}
	}
}