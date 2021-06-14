package jhi.germinate.server.util.hdf5;

import ch.systemsx.cisd.hdf5.*;

import java.io.File;
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

	protected final File        hdf5File;
	protected       Set<String> lines;
	protected       Set<String> markers;
	protected final String      outputFilePath;

	protected Map<String, Integer> lineInds;
	protected Map<String, Integer> markerInds;
	protected List<String>         hdf5Lines;
	protected List<String>         hdf5Markers;

	public AbstractHdf5Converter(File hdf5File, Set<String> lines, Set<String> markers, String outputFilePath)
	{
		this.hdf5File = hdf5File;
		this.lines = lines;
		this.markers = markers;
		this.outputFilePath = outputFilePath;

		readInput();
	}

	private void readInput()
	{
		try (IHDF5Reader reader = HDF5Factory.openForReading(hdf5File))
		{
			long s = System.currentTimeMillis();

			System.out.println();
			System.out.println("Hdf5 file opened for reading: " + (System.currentTimeMillis() - s) + " (ms)");

			s = System.currentTimeMillis();
			// Load lines from HDF5 and find the indices of our loaded lines
			String[] hdf5LinesArray = reader.readStringArray(LINES);
			hdf5Lines = new ArrayList<>(Arrays.asList(hdf5LinesArray));

			if (lines == null)
				lines = new LinkedHashSet<>(hdf5Lines);
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
			hdf5Markers = new ArrayList<>(Arrays.asList(hdf5MarkersArray));

			if (markers == null)
				markers = new LinkedHashSet<>(hdf5Markers);
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
}
