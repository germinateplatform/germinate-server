package jhi.germinate.server.util;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Word frequency counting built on Apache Lucene.
 * <p>
 * Maven:
 * org.apache.lucene:lucene-core
 * org.apache.lucene:lucene-analysis-common
 * (same version for both; Lucene 9.x works on Java 11+, Lucene 10.x needs Java 21+)
 * <p>
 * Pipeline: HTML strip -> StandardTokenizer -> possessive removal -> lowercase -> stop words
 * -> Snowball stemming (to merge "sample"/"samples") -> counting.
 * Words that appear in a large share of all documents are dropped, which removes
 * catalogue-specific filler ("dataset", "data") without a hand-written list.
 */
public class LuceneWordFrequencyService implements AutoCloseable
{

	public record WordCount(String text, int count)
	{
	}

	public static List<WordCount> LIST = new ArrayList<>();

	private static final Pattern URLS                      = Pattern.compile("https?://\\S+|www\\.\\S+");
	private static final int     MIN_WORD_LENGTH           = 3;
	// The doc-frequency cutoff is only meaningful with enough documents
	private static final int     MIN_DOCS_FOR_RATIO_FILTER = 20;

	private static final String DEFAULT_STOP_WORDS_RESOURCE = "/jhi/germinate/server/util/database/util/english_stop.txt";

	private final Analyzer analyzer;

	/**
	 * Loads stop words from /english_stop.txt on the classpath (src/main/resources).
	 */
	public LuceneWordFrequencyService()
			throws IOException
	{
		this(DEFAULT_STOP_WORDS_RESOURCE);
	}

	/**
	 * @param stopWordsResource classpath location of a Snowball-format stop word file
	 */
	public LuceneWordFrequencyService(String stopWordsResource)
			throws IOException
	{
		final CharArraySet stopWords = loadStopWords(stopWordsResource);

		this.analyzer = new Analyzer()
		{
			@Override
			protected Reader initReader(String fieldName, Reader reader)
			{
				return new HTMLStripCharFilter(reader);
			}

			@Override
			protected TokenStreamComponents createComponents(String fieldName)
			{
				Tokenizer source = new StandardTokenizer();
				TokenStream stream = new EnglishPossessiveFilter(source);
				stream = new LowerCaseFilter(stream);
				stream = new StopFilter(stream, stopWords); // runs after lowercasing, so the file can be lowercase
				return new TokenStreamComponents(source, stream);
			}
		};
	}

	private static CharArraySet loadStopWords(String resourcePath)
			throws IOException
	{
		try (InputStream in = LuceneWordFrequencyService.class.getResourceAsStream(resourcePath))
		{
			if (in == null)
			{
				throw new IOException("Stop word file not found on classpath: " + resourcePath);
			}
			try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8))
			{
				return WordlistLoader.getSnowballWordSet(reader);
			}
		}
	}

	/**
	 * Same as the full version, with no extra ignore terms.
	 */
	public List<WordCount> countWords(List<String> texts, int maxWords, int minCount, double maxDocRatio)
			throws IOException
	{
		return countWords(texts, Set.of(), maxWords, minCount, maxDocRatio);
	}

	/**
	 * @param texts       raw names/descriptions (nulls and blanks are ignored)
	 * @param ignoreTerms extra terms to exclude. They go through the same analysis and stemming as
	 *                    the texts, so "sample" also removes "samples" and "Sampling". A multi-word
	 *                    entry such as "soil moisture" ignores each of its words individually.
	 * @param maxWords    maximum number of words to return
	 * @param minCount    words occurring fewer times than this are dropped
	 * @param maxDocRatio drop words found in more than this fraction of documents (e.g. 0.5);
	 *                    use 1.0 to disable
	 */
	public List<WordCount> countWords(List<String> texts, Collection<String> ignoreTerms,
									  int maxWords, int minCount, double maxDocRatio)
			throws IOException
	{

		Map<String, Integer> stemCounts = new HashMap<>();
		Map<String, Integer> docFreq = new HashMap<>();
		Map<String, Map<String, Integer>> surfaceForms = new HashMap<>(); // stem -> original word -> count
		EnglishStemmer stemmer = new EnglishStemmer(); // not thread-safe, so one per call
		Set<String> ignoredStems = toStems(ignoreTerms, stemmer);
		int docCount = 0;

		for (String text : texts)
		{
			if (text == null || text.isBlank())
			{
				continue;
			}
			docCount++;
			Set<String> stemsInDoc = new HashSet<>();

			for (String token : tokenize(text))
			{
				stemmer.setCurrent(token);
				stemmer.stem();
				String stem = stemmer.getCurrent();

				if (ignoredStems.contains(stem))
				{
					continue; // skipped before counting, so it doesn't affect doc frequencies either
				}

				stemCounts.merge(stem, 1, Integer::sum);
				surfaceForms.computeIfAbsent(stem, k -> new HashMap<>()).merge(token, 1, Integer::sum);
				stemsInDoc.add(stem);
			}
			stemsInDoc.forEach(s -> docFreq.merge(s, 1, Integer::sum));
		}

		final int docs = docCount;
		final boolean applyRatio = docs >= MIN_DOCS_FOR_RATIO_FILTER;

		return stemCounts.entrySet().stream()
						 .filter(e -> e.getValue() >= minCount)
						 .filter(e -> !applyRatio || docFreq.get(e.getKey()) <= maxDocRatio * docs)
						 .map(e -> new WordCount(displayForm(surfaceForms.get(e.getKey())), e.getValue()))
						 .sorted(Comparator.comparingInt(WordCount::count).reversed()
										   .thenComparing(WordCount::text))
						 .limit(maxWords)
						 .collect(Collectors.toList());
	}

	/**
	 * Runs ignore terms through the same pipeline as the texts and returns their stems.
	 */
	private Set<String> toStems(Collection<String> terms, EnglishStemmer stemmer)
			throws IOException
	{
		Set<String> stems = new HashSet<>();
		if (terms == null)
		{
			return stems;
		}
		for (String term : terms)
		{
			if (term == null || term.isBlank())
			{
				continue;
			}
			for (String token : tokenize(term))
			{
				stemmer.setCurrent(token);
				stemmer.stem();
				stems.add(stemmer.getCurrent());
			}
		}
		return stems;
	}

	private List<String> tokenize(String text)
			throws IOException
	{
		String cleaned = URLS.matcher(text).replaceAll(" ");
		List<String> tokens = new ArrayList<>();

		try (TokenStream stream = analyzer.tokenStream("text", cleaned))
		{
			CharTermAttribute term = stream.addAttribute(CharTermAttribute.class);
			stream.reset();
			while (stream.incrementToken())
			{
				String t = term.toString();
				// keep alphabetic tokens only (drops "2019", "19", etc.)
				if (t.length() >= MIN_WORD_LENGTH && t.chars().allMatch(Character::isLetter))
				{
					tokens.add(t);
				}
			}
			stream.end();
		}
		return tokens;
	}

	/**
	 * Stems like "moistur" aren't readable, so show the most common original word instead.
	 */
	private static String displayForm(Map<String, Integer> forms)
	{
		return forms.entrySet().stream()
					.max(Map.Entry.<String, Integer>comparingByValue()
								  .thenComparing(Map.Entry.<String, Integer>comparingByKey().reversed()))
					.get().getKey();
	}

	@Override
	public void close()
	{
		analyzer.close();
	}
}