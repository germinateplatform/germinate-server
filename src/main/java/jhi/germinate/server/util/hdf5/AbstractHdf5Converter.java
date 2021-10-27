package jhi.germinate.server.util.hdf5;

import ch.systemsx.cisd.hdf5.*;
import jhi.flapjack.io.Hdf5Utils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Sebastian Raubach
 */
public abstract class AbstractHdf5Converter
{
	public static final String LINES       = "Lines";
	public static final String MARKERS     = "Markers";
	public static final String DATA        = "DataMatrix";
	public static final String STATE_TABLE = "StateTable";

	protected final Path        hdf5File;
	protected       Set<String> lines;
	protected       Set<String> markers;
	protected final Path        outputFilePath;

	protected Map<String, Integer>  lineInds;
	protected Map<String, Integer>  markerInds;
	protected LinkedHashSet<String> hdf5Lines;
	protected LinkedHashSet<String> hdf5Markers;

	public AbstractHdf5Converter(Path hdf5File, Set<String> lines, Set<String> markers, Path outputFilePath)
	{
		this.hdf5File = hdf5File;
		this.lines = lines;
		this.markers = markers;
		this.outputFilePath = outputFilePath;

		readInput();
	}

	private void readInput()
	{
		try (IHDF5Reader reader = HDF5Factory.openForReading(hdf5File.toFile()))
		{
			long s = System.currentTimeMillis();

			System.out.println();
			System.out.println("Hdf5 file opened for reading: " + (System.currentTimeMillis() - s) + " (ms)");

			s = System.currentTimeMillis();
			// Load lines from HDF5 and find the indices of our loaded lines
			String[] hdf5LinesArray = reader.readStringArray(LINES);
			hdf5Lines = new LinkedHashSet<>(Arrays.asList(hdf5LinesArray));

			if (lines == null)
				lines = hdf5Lines;
			else
				lines = lines.stream().filter(line -> hdf5Lines.contains(line)).collect(Collectors.toCollection(LinkedHashSet::new));

			lineInds = new HashMap<>();
			for (int i = 0; i < hdf5LinesArray.length; i++)
				lineInds.put(hdf5LinesArray[i], i);

			System.out.println();
			System.out.println("Read and filtered lines: " + (System.currentTimeMillis() - s) + " (ms)");

			s = System.currentTimeMillis();
			// Load markers from HDF5 and find the indices of our loaded markers
			String[] hdf5MarkersArray = reader.readStringArray(MARKERS);
			hdf5Markers = new LinkedHashSet<>(Arrays.asList(hdf5MarkersArray));

			if (markers == null)
				markers = hdf5Markers;
			else
				markers = markers.stream().filter(marker -> hdf5Markers.contains(marker)).collect(Collectors.toCollection(LinkedHashSet::new));

			markerInds = new HashMap<>();
			for (int i = 0; i < hdf5MarkersArray.length; i++)
				markerInds.put(hdf5MarkersArray[i], i);

			System.out.println();
			System.out.println("Read and filtered markers: " + (System.currentTimeMillis() - s) + " (ms)");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public abstract void extractData(String headerLines);

	public static long getMarkerCount(File hdf5File)
	{
		try (IHDF5Reader reader = HDF5Factory.openForReading(hdf5File))
		{
			return reader.readStringArray(MARKERS).length;
		}
	}

	/**
	 * Updates the given HDF5 file by replacing all occurrences of germplasm in otherNames with the preferredName.
	 *
	 * @param hdf5File      The HDF5 file to update
	 * @param preferredName The preferred name to replace the others with
	 * @param otherNames    The other names to replace
	 */
	public static synchronized void updateGermplasmNames(File hdf5File, String preferredName, List<String> otherNames)
	{
		// Update the names
		List<String> oldNames = Hdf5Utils.getLines(hdf5File);
		for (int i = 0; i < oldNames.size(); i++)
		{
			if (otherNames.contains(oldNames.get(i)))
			{
				oldNames.set(i, preferredName);
			}
		}

		// Write them back to the file
		try (IHDF5Writer writer = HDF5Factory.open(hdf5File))
		{
			writer.string().writeArray(LINES, oldNames.toArray(new String[0]), HDF5GenericStorageFeatures.GENERIC_DEFLATE);
		}
	}
}
