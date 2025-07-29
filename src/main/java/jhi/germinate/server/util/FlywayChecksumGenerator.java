package jhi.germinate.server.util;

import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.internal.util.StringUtils;
import org.flywaydb.core.internal.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;

/**
 * @author Sebastian Raubach
 */
public class FlywayChecksumGenerator
{
	public static void main(String[] args)
			throws IOException
	{
		System.out.println(getChecksum(new File("src/main/resources/jhi/germinate/server/util/database/migration/V4.25.01.08__update.sql")));
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
		final CRC32 crc32 = new CRC32();

		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file), 4096);
			String line = bufferedReader.readLine();

			if (line != null) {
				line = BomFilter.FilterBomFromString(line);
				do {
					//noinspection Since15
					crc32.update(line.getBytes(StandardCharsets.UTF_8));
				} while ((line = bufferedReader.readLine()) != null);
			}
		} catch (IOException e) {
			throw new FlywayException("Unable to calculate checksum of " + file.getName() + "\r\n" + e.getMessage(), e);
		} finally {
			IOUtils.close(bufferedReader);
		}

		return (int) crc32.getValue();
	}
}