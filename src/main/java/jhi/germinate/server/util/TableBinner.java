package jhi.germinate.server.util;

import java.io.*;
import java.util.*;

/**
 * Efficiently bins decimal values in a tab-separated file into x bins.
 * <p>
 * Designed for very large files (millions of columns, thousands of rows):
 * - Streams row-by-row; only one row is held in memory at a time
 * - Avoids String[] allocation per row by parsing tokens inline
 * - EQUAL_SIZE mode collects primitive doubles (not strings) and supports
 * reservoir sampling to avoid materialising billions of values
 * <p>
 * Two modes:
 * EQUAL_WIDTH - every bin covers the same value range [0..1] / numBins
 * EQUAL_SIZE  - every bin contains roughly the same number of non-empty
 * entries (quantile binning, optionally via reservoir sampling)
 */
public class TableBinner
{

	public enum BinMode
	{
		EQUAL_WIDTH,
		EQUAL_SIZE
	}

	/**
	 * Reservoir sample limit for EQUAL_SIZE threshold calculation.
	 * ~500k doubles = ~4 MB; gives statistically excellent quantile estimates.
	 * Set to Integer.MAX_VALUE to always use all values (exact, but costly).
	 */
	private static final int DEFAULT_SAMPLE_LIMIT = 500_000;

	// -------------------------------------------------------------------------
	// Public API
	// -------------------------------------------------------------------------

	/**
	 * Convenience overload using the default sample limit.
	 *
	 * @return The binning thresholds
	 */
	public static double[] bin(File inputPath, File outputPath,
	                       int numBins, BinMode mode)
			throws IOException
	{
		return bin(inputPath, outputPath, numBins, mode, DEFAULT_SAMPLE_LIMIT);
	}

	/**
	 * Reads {@code inputPath}, bins every decimal cell value into {@code numBins}
	 * bins using {@code mode}, and writes the result to {@code outputPath}.
	 * <p>
	 * Row headers (col 0) and column headers (row 0) are preserved verbatim.
	 * Empty cells remain empty in the output.
	 *
	 * @param inputPath   path to the input TSV file
	 * @param outputPath  path where the output TSV file will be written
	 * @param numBins     number of bins (>= 1)
	 * @param mode        EQUAL_WIDTH or EQUAL_SIZE
	 * @param sampleLimit max values to collect for quantile estimation
	 *                    (EQUAL_SIZE only); use Integer.MAX_VALUE for exact
	 * @return The binning thresholds
	 * @throws IOException              on read/write failure
	 * @throws IllegalArgumentException on bad numBins or out-of-range cell value
	 */
	public static double[] bin(File inputPath, File outputPath,
	                       int numBins, BinMode mode, int sampleLimit)
			throws IOException
	{

		if (numBins < 1) throw new IllegalArgumentException("numBins must be >= 1");

		// 1. Compute thresholds (requires a full first pass only for EQUAL_SIZE)
		double[] thresholds = (mode == BinMode.EQUAL_WIDTH)
				? equalWidthThresholds(numBins)
				: equalSizeThresholds(inputPath, numBins, sampleLimit);

		// 2. Stream rows: read → bin → write, one row at a time
		streamAndBin(inputPath, outputPath, numBins, thresholds);

		return thresholds;
	}

	// -------------------------------------------------------------------------
	// Threshold computation
	// -------------------------------------------------------------------------

	/**
	 * Evenly spaced thresholds: 1/n, 2/n, ..., (n-1)/n
	 */
	private static double[] equalWidthThresholds(int numBins)
	{
		double[] t = new double[numBins - 1];
		for (int i = 0; i < t.length; i++) t[i] = (double) (i + 1) / numBins;
		return t;
	}

	/**
	 * Single-pass quantile thresholds using reservoir sampling.
	 * <p>
	 * Reservoir sampling (Vitter's Algorithm R) gives a uniform random sample
	 * of size {@code sampleLimit} from the stream of all data-cell values
	 * without knowing the total count in advance. From that sample we derive
	 * quantile cut-points. When the total number of values is <= sampleLimit,
	 * every value is retained (exact result).
	 */
	private static double[] equalSizeThresholds(File inputPath,
	                                            int numBins,
	                                            int sampleLimit)
			throws IOException
	{
		try
		{
			double[] reservoir = new double[sampleLimit];
			int filled = 0;          // how many slots have been written
			long seen = 0;          // total values encountered (for sampling probability)
			Random rng = new Random(0); // fixed seed for reproducibility

			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(inputPath)), 1 << 20))
			{

				String line;
				boolean firstRow = true;

				while ((line = reader.readLine()) != null)
				{
					if (line.startsWith("#")) continue;

					if (firstRow)
					{
						firstRow = false;
						continue;
					} // skip header row

					// Parse tokens inline without splitting the whole line
					int len = line.length();
					int colIndex = 0;
					int tokenStart = 0;

					for (int i = 0; i <= len; i++)
					{
						if (i == len || line.charAt(i) == '\t')
						{
							if (colIndex > 0 && i > tokenStart)
							{ // skip row-header col (0)
								// Trim whitespace manually (faster than String.trim())
								int s = tokenStart, e = i;
								while (s < e && line.charAt(s) <= ' ') s++;
								while (e > s && line.charAt(e - 1) <= ' ') e--;

								if (e > s)
								{ // non-empty cell
									double v = parseDouble(line, s, e);

									// Reservoir sampling (Algorithm R)
									if (filled < sampleLimit)
									{
										reservoir[filled++] = v;
									}
									else
									{
										long j = (long) (rng.nextDouble() * (seen + 1));
										if (j < sampleLimit)
										{
											reservoir[(int) j] = v;
										}
									}
									seen++;
								}
							}
							tokenStart = i + 1;
							colIndex++;
						}
					}
				}
			}

			if (filled == 0) return equalWidthThresholds(numBins); // no data

			// Sort the (possibly partial) reservoir
			double[] sample = (filled < sampleLimit) ? Arrays.copyOf(reservoir, filled) : reservoir;
			Arrays.sort(sample);

			// Derive (numBins-1) quantile thresholds
			int n = sample.length;
			double[] t = new double[numBins - 1];
			for (int i = 0; i < t.length; i++)
			{
				int idx = (int) Math.ceil((double) (i + 1) * n / numBins) - 1;
				t[i] = sample[Math.min(idx, n - 1)];
			}
			return t;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	// -------------------------------------------------------------------------
	// Streaming bin + write pass
	// -------------------------------------------------------------------------

	/**
	 * Reads the input file line-by-line, bins data cells inline, and writes
	 * each processed line immediately — only one line is in memory at a time.
	 * Avoids String.split() and String.join() for large rows by building the
	 * output token-by-token directly into the writer.
	 */
	private static void streamAndBin(File inputPath, File outputPath,
	                                 int numBins, double[] thresholds)
			throws IOException
	{

		// Large buffers reduce system-call overhead on wide files
		final int BUF = 1 << 20; // 1 MB

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(inputPath)), BUF);
		     BufferedWriter writer = new BufferedWriter(
					 new OutputStreamWriter(new FileOutputStream(outputPath)), BUF))
		{

			String line;
			boolean firstRow = true;

			while ((line = reader.readLine()) != null)
			{
				if (line.startsWith("#"))
					continue;
				if (firstRow)
				{
					writer.write(line); // header row verbatim
					writer.newLine();
					firstRow = false;
					continue;
				}

				writeBinnedRow(line, numBins, thresholds, writer);
				writer.newLine();
			}
		}
	}

	/**
	 * Parses one data row inline and writes the binned version token-by-token
	 * to {@code writer}, avoiding any intermediate String array or joined string.
	 */
	private static void writeBinnedRow(String line, int numBins,
	                                   double[] thresholds,
	                                   BufferedWriter writer)
			throws IOException
	{
		int len = line.length();
		int colIndex = 0;
		int tokenStart = 0;

		for (int i = 0; i <= len; i++)
		{
			if (i == len || line.charAt(i) == '\t')
			{
				if (colIndex > 0) writer.write('\t');

				if (colIndex == 0)
				{
					// Row header — write verbatim
					writer.write(line, tokenStart, i);
				}
				else
				{
					// Trim manually
					int s = tokenStart, e = i;
					while (s < e && line.charAt(s) <= ' ') s++;
					while (e > s && line.charAt(e - 1) <= ' ') e--;

					if (e <= s)
					{
						// Empty cell — write nothing (preserves the tab delimiter above)
					}
					else
					{
						double v = parseDouble(line, s, e);
						writer.write(Integer.toString(assignBin(v, numBins, thresholds)));
					}
				}

				tokenStart = i + 1;
				colIndex++;
			}
		}
	}

	// -------------------------------------------------------------------------
	// Bin assignment
	// -------------------------------------------------------------------------

	/**
	 * Returns the 0-based bin index.
	 * Bin i covers (thresholds[i-1], thresholds[i]]; bin 0 covers [0, thresholds[0]].
	 * Values of exactly 1.0 always fall into the last bin.
	 */
	private static int assignBin(double value, int numBins, double[] thresholds)
	{
		// Binary search is O(log b) vs O(b) linear scan — pays off for large numBins
		int lo = 0, hi = thresholds.length;
		while (lo < hi)
		{
			int mid = (lo + hi) >>> 1;
			if (value <= thresholds[mid]) hi = mid;
			else lo = mid + 1;
		}
		return lo; // lo == numBins-1 when value > all thresholds
	}

	// -------------------------------------------------------------------------
	// Fast in-place double parser (avoids substring allocation)
	// -------------------------------------------------------------------------

	/**
	 * Parses a decimal number from {@code line[start..end)} without allocating
	 * a substring. Handles optional leading '-', integer part, and '.' fraction.
	 * Sufficient for values in [0, 1]; does not handle 'e'/'E' exponents.
	 */
	private static double parseDouble(String line, int start, int end)
	{
		// Fast path: fall back to JDK parser via a substring only if we detect
		// an exponent character or other unusual content.
		for (int i = start; i < end; i++)
		{
			char c = line.charAt(i);
			if (c == 'e' || c == 'E' || c == 'N' || c == 'I')
			{
				// Rare: delegate to JDK (allocates one substring)
				double v = Double.parseDouble(line.substring(start, end));
				validateRange(v, line, start);
				return v;
			}
		}

		// Manual parse for the common case: optional '-', digits, optional '.' digits
		boolean neg = false;
		int i = start;
		if (i < end && line.charAt(i) == '-')
		{
			neg = true;
			i++;
		}

		long intPart = 0;
		while (i < end && line.charAt(i) != '.')
		{
			char c = line.charAt(i++);
			if (c < '0' || c > '9') throwParseError(line, start, end);
			intPart = intPart * 10 + (c - '0');
		}

		double fracPart = 0.0;
		if (i < end && line.charAt(i) == '.')
		{
			i++;
			double factor = 0.1;
			while (i < end)
			{
				char c = line.charAt(i++);
				if (c < '0' || c > '9') throwParseError(line, start, end);
				fracPart += (c - '0') * factor;
				factor *= 0.1;
			}
		}

		double v = (intPart + fracPart) * (neg ? -1.0 : 1.0);
		validateRange(v, line, start);
		return v;
	}

	private static void validateRange(double v, String line, int start)
	{
		if (v < 0.0 || v > 1.0) throw new IllegalArgumentException(
				"Value " + v + " is outside [0, 1] near: \"" +
						line.substring(start, Math.min(start + 20, line.length())) + "\"");
	}

	private static void throwParseError(String line, int start, int end)
	{
		throw new IllegalArgumentException("Cannot parse \"" +
				line.substring(start, end) + "\" as a decimal.");
	}
}