package org.dice_research.spab.feasible.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.dice_research.spab.feasible.Triplestore;

/**
 * Parses results of FEASIBLE CSV files.
 * 
 * 
 * The method {@link ResultsCsv#main(String[])} tests the import.
 * 
 * The method {@link ResultsCsv#getResults(int, int)} returns results of a
 * respective file. int queryType is one of the QUERYTYPE constants defined in
 * {@link FeasibleFileAccesor}. int dataset is one of the DATASET constants
 * defined in {@link FeasibleFileAccesor}.
 * 
 * The method {@link ResultsCsv#generateRowBasedTsvFiles()} generates files for
 * the SPAB webdemo.
 * 
 * 
 * @see https://github.com/dice-group/feasible#evaluation-results-and-timeouts-queries
 *
 * @author Adrian Wilke
 */
public class ResultsCsv {

	public static final String DBPEDIA_ASK = "FEASIBLE-DBpedia-ASK-100.csv";
	public static final String DBPEDIA_CONSTRUCT = "FEASIBLE-DBpedia-CONSTRUCT-100.csv";
	public static final String DBPEDIA_DESCRIBE = "FEASIBLE-DBpedia-DESCRIBE-25.csv";
	public static final String DBPEDIA_SELECT = "FEASIBLE-DBpedia-SELECT-100.csv";
	public static final String DBPEDIA_MIX = "FEASIBLE-DBpedia-175.csv";
	public static final String SWDF_ASK = "FEASIBLE-SWDF-ASK-50.csv";
	public static final String SWDF_CONSTRUCT = "FEASIBLE-SWDF-CONSTRUCT-23.csv";
	public static final String SWDF_DESCRIBE = "FEASIBLE-SWDF-DESCRIBE-100.csv";
	public static final String SWDF_SELECT = "FEASIBLE-SWDF-SELECT-100.csv";
	public static final String SWDF_MIX = "FEASIBLE-SWDF-175.csv";

	public static final String TRIPLESTORE_FUSEKI = Triplestore.FUSEKI.getCsvHeader();
	public static final String TRIPLESTORE_OWLIM_SE = Triplestore.OWLIM_SE.getCsvHeader();
	public static final String TRIPLESTORE_SESAME = Triplestore.SESAME.getCsvHeader();
	public static final String TRIPLESTORE_VITUOSO = Triplestore.VITUOSO.getCsvHeader();

	public static final String QUERY_ID = "Query-ID";

	protected File directory;

	/**
	 * Checks existing directory.
	 * 
	 * @param directory containing the 10 CSV files
	 */
	public ResultsCsv(File directory) throws IOException {
		if (!directory.exists()) {
			throw new FileNotFoundException("Directory not found: " + directory.getAbsolutePath());
		} else if (!directory.canRead()) {
			throw new IOException("Can not read directory: " + directory.getAbsolutePath());
		}
		this.directory = directory;
	}

	/**
	 * Gets FEASIBLE results.
	 * 
	 * @param queryType is one of the QUERYTYPE constants defined in
	 *                  {@link FeasibleFileAccesor}.
	 * @param dataset   is one of the DATASET constants defined in
	 *                  {@link FeasibleFileAccesor}.
	 * @return A list, whose position represents the query ID. The list contains
	 *         Maps, which map a Triplestore ID to its result. Triplestore IDs are
	 *         defined as constants in {@link ResultsCsv}.
	 */
	public List<Map<String, Float>> getResults(int queryType, int dataset) throws FileNotFoundException, IOException {
		if (dataset == FeasibleFileAccesor.DATASET_DBPEDIA) {
			if (queryType == FeasibleFileAccesor.QUERYTYPE_ASK) {
				return getResults(DBPEDIA_ASK);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_CONSTRUCT) {
				return getResults(DBPEDIA_CONSTRUCT);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_DESCRIBE) {
				return getResults(DBPEDIA_DESCRIBE);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_SELECT) {
				return getResults(DBPEDIA_SELECT);
			} else {
				return getResults(DBPEDIA_MIX);
			}
		} else {
			if (queryType == FeasibleFileAccesor.QUERYTYPE_ASK) {
				return getResults(SWDF_ASK);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_CONSTRUCT) {
				return getResults(SWDF_CONSTRUCT);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_DESCRIBE) {
				return getResults(SWDF_DESCRIBE);
			} else if (queryType == FeasibleFileAccesor.QUERYTYPE_SELECT) {
				return getResults(SWDF_SELECT);
			} else {
				return getResults(SWDF_MIX);
			}
		}
	}

	/**
	 * Gets FEASIBLE results.
	 * 
	 * @param filename is one of the DBPEDIA or SWDF constants defined in
	 *                 {@link ResultsCsv}.
	 * @return A list, whose position represents the query ID. The list contains
	 *         Maps, which map a Triplestore ID to its result. Triplestore IDs are
	 *         defined as constants in {@link ResultsCsv}.
	 */
	public List<Map<String, Float>> getResults(String filename) throws FileNotFoundException, IOException {
		CSVParser csvParser = CSVParser.parse(new FileReader(getFile(filename)),
				CSVFormat.DEFAULT.withFirstRecordAsHeader());

		Set<String> headers = csvParser.getHeaderMap().keySet();
		if (!headers.contains(TRIPLESTORE_FUSEKI)) {
			throw new IOException("Column not found: " + TRIPLESTORE_FUSEKI);
		}
		if (!headers.contains(TRIPLESTORE_OWLIM_SE)) {
			throw new IOException("Column not found: " + TRIPLESTORE_OWLIM_SE);
		}
		if (!headers.contains(TRIPLESTORE_SESAME)) {
			throw new IOException("Column not found: " + TRIPLESTORE_SESAME);
		}
		if (!headers.contains(TRIPLESTORE_VITUOSO)) {
			throw new IOException("Column not found: " + TRIPLESTORE_VITUOSO);
		}
		if (!headers.contains("")) {
			throw new IOException("Column not found: " + "Query ID");
		}

		List<Map<String, Float>> results = new LinkedList<Map<String, Float>>();

		int counter = 0;
		for (CSVRecord csvRecord : csvParser) {
			if (Integer.parseInt(csvRecord.get(0)) != ++counter) {
				throw new IOException("Could not map query ID");
			}
			Map<String, Float> result = new LinkedHashMap<String, Float>();
			putResult(result, TRIPLESTORE_FUSEKI, csvRecord);
			putResult(result, TRIPLESTORE_OWLIM_SE, csvRecord);
			putResult(result, TRIPLESTORE_SESAME, csvRecord);
			putResult(result, TRIPLESTORE_VITUOSO, csvRecord);
			results.add(result);
		}

		return results;
	}

	/**
	 * Gets CSV files in directory.
	 */
	public List<String> getCsvFiles() {
		return Arrays.asList(directory.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".csv")) {
					return true;
				} else {
					return false;
				}
			}
		}));
	}

	/**
	 * Writes row-based TSV files in result directory.
	 * 
	 * Query number, Triplestore ID, result
	 */
	public void generateRowBasedTsvFiles() throws FileNotFoundException, IOException {
		for (String csvFile : getCsvFiles()) {

			List<Map<String, Float>> results = getResults(csvFile);

			File tsvFile = new File(directory, csvFile.replaceAll("\\.csv", ".tsv"));
			CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(tsvFile), CSVFormat.DEFAULT.withDelimiter('\t'));

			int resultsCounter = 0;
			for (Map<String, Float> map : results) {
				resultsCounter++;

				for (Entry<String, Float> result : map.entrySet()) {
					List<String> resultElements = new LinkedList<String>();
					resultElements.add("" + resultsCounter);
					resultElements.add(result.getKey());
					resultElements.add("" + result.getValue());
					csvPrinter.printRecord(resultElements);
				}

			}
			csvPrinter.close();
		}
	}

	/**
	 * Gets file, if in results directory.
	 */
	protected File getFile(String filename) throws FileNotFoundException {
		if (!getCsvFiles().contains(filename)) {
			throw new FileNotFoundException("File not found: " + filename);
		}
		return new File(directory, filename);
	}

	/**
	 * Helper for getResults method. Replaces separators of floating point numbers.
	 */
	private void putResult(Map<String, Float> map, String key, CSVRecord csvRecord) {
		try {
			map.put(key, Float.parseFloat(csvRecord.get(key)));
		} catch (NumberFormatException e) {
			map.put(key, Float.parseFloat(csvRecord.get(key).replaceAll(",", ".")));
		}
	}

	/**
	 * Tests outside of JUnit, as files have provided manually.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Please provide a directory containing the FEASIBLE results as CSV files.");
			System.exit(1);
		}

		ResultsCsv resultsCsv = new ResultsCsv(new File(args[0]));

		List<String> csvFiles = resultsCsv.getCsvFiles();
		if (csvFiles.size() != 10) {
			System.err.println("Did not found exactly 10 CSV files");
		}

		int resultsCounter = 0;
		for (String csvFile : csvFiles) {
			resultsCounter += resultsCsv.getResults(csvFile).size();
		}
		int assumedResults = 50 + 23 + 100 + 100 + 100 + 100 + 25 + 100 + 175 + 175;
		if (resultsCounter != assumedResults) {
			System.err.println("Expected " + assumedResults + " results, found " + resultsCounter);
		}

		System.out.println("Tests finished");
	}

}