package jhi.germinate.server.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.*;

/**
 * Used to subset tab-delimited files by given column and row identifiers
 *
 * @author Sebastian Raubach
 */
public class TabFileSubsetter
{
	/**
	 * @param input          The input file to subset
	 * @param output         The output file to create
	 * @param rowsToKeep     The row headers of the rows to keep
	 * @param columnsToKeep  The column headers of the columns to keep
	 * @param optionalHeader An optional header that will be added to the beginning of the output
	 * @return An array of length two. Index 0 indicates how many columns were kept, index 1 how many rows were kept.
	 * @throws IOException thrown if the file interaction fails
	 */
	public int[] run(File input, File output, Collection<String> rowsToKeep, Collection<String> columnsToKeep, String optionalHeader)
		throws IOException
	{
		if (CollectionUtils.isEmpty(rowsToKeep))
			rowsToKeep = null;
		if (CollectionUtils.isEmpty(columnsToKeep))
			columnsToKeep = null;

		int[] counts = new int[2];

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(input), StandardCharsets.UTF_8));
			 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), StandardCharsets.UTF_8)))
		{
			if (!StringUtils.isEmpty(optionalHeader))
				bw.write(optionalHeader);

			String line = br.readLine();
			String[] headers = line.split("\t", -1);

			// Remember which columns to keep
			boolean[] keep = new boolean[headers.length];
			keep[0] = true;

			// For each column, check if it should be kept and if so, write it to the header row
			bw.write(headers[0]);
			for (int i = 1; i < keep.length; i++)
			{
				keep[i] = columnsToKeep == null || columnsToKeep.contains(headers[i]);

				if (keep[i])
				{
					bw.write("\t" + headers[i]);
					counts[0]++;
				}
			}

			bw.newLine();

			while ((line = br.readLine()) != null)
			{
				String[] parts = line.split("\t", -1);

				if (rowsToKeep == null || rowsToKeep.contains(parts[0]))
				{
					// Stream over all indices, filter only the ones to keep, map to their String, then join
					bw.write(IntStream.range(0, parts.length)
									  .filter(i -> keep[i])
									  .mapToObj(i -> parts[i])
									  .collect(Collectors.joining("\t")));
					bw.newLine();
					counts[1]++;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw e;
		}

		return counts;
	}
}
