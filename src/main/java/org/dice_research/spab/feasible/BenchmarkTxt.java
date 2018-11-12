package org.dice_research.spab.feasible;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class BenchmarkTxt {

	public final static int QUERYTYPE_ASK = 1;
	public final static int QUERYTYPE_CONSTRUCT = 2;
	public final static int QUERYTYPE_DESCRIBE = 3;
	public final static int QUERYTYPE_SELECT = 4;
	public final static int QUERYTYPE_MIX = 5;

	public final static int DATASET_DBPEDIA = 1;
	public final static int DATASET_SWDF = 2;

	protected File directory;

	/**
	 * Checks existing directory.
	 * 
	 * @param directory containing the sub-directories 'Benchmarks_Errors' and
	 *                  'Benchmarks_Evaluation'.
	 */
	public BenchmarkTxt(File directory) throws IOException {
		if (!directory.exists()) {
			throw new FileNotFoundException("Directory not found: " + directory.getAbsolutePath());
		} else if (!directory.canRead()) {
			throw new IOException("Can not read directory: " + directory.getAbsolutePath());
		}
		this.directory = directory;
	}

	/**
	 * Gets FEASIBLE SPARQL queries.
	 * 
	 * @param queryType is one of the QUERYTYPE constants defined in
	 *                  {@link BenchmarkTxt}.
	 * @param dataset   is one of the DATASET constants defined in
	 *                  {@link BenchmarkTxt}.
	 * @return A list of SPARQL queries.
	 */
	public List<String> getQueries(int queryType, int dataset) throws FileNotFoundException, IOException {
		List<String> queries = new LinkedList<String>();

		LineIterator lineIterator = IOUtils.lineIterator(new FileReader(getFile(queryType, dataset)));
		StringBuilder stringBuilder = new StringBuilder();
		while (lineIterator.hasNext()) {
			String line = lineIterator.next();
			if (line.startsWith("#")) {
				if (stringBuilder.length() > 0) {
					queries.add(stringBuilder.toString());
					stringBuilder = new StringBuilder();
				}
			} else {
				if (stringBuilder.length() > 0) {
					stringBuilder.append(System.lineSeparator());
				}
				stringBuilder.append(line);
			}
		}
		if (stringBuilder.length() > 0) {
			queries.add(stringBuilder.toString());
		}
		return queries;
	}

	/**
	 * Gets path and file of default extracted directory.
	 */
	protected File getFile(int queryType, int dataset) throws IOException {
		StringBuilder pathBuilder = new StringBuilder();

		pathBuilder.append("Benchmarks_Evaluation/");
		pathBuilder.append(queryType == QUERYTYPE_MIX ? "mix-benchmarks/" : "individual-benchmarks/");
		pathBuilder.append(dataset == DATASET_DBPEDIA ? "dbpedia351/" : "swdf/");

		if (queryType == QUERYTYPE_MIX) {
			pathBuilder.append("queries-175.txt");
			File file = new File(directory, pathBuilder.toString());
			if (!file.exists()) {
				throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
			} else if (!file.canRead()) {
				throw new IOException("Can not read file: " + file.getAbsolutePath());
			} else {
				return file;
			}

		} else {

			if (dataset == DATASET_DBPEDIA) {
				if (queryType == QUERYTYPE_ASK) {
					pathBuilder.append("dbpedia-ask-100/");

				} else if (queryType == QUERYTYPE_CONSTRUCT) {
					pathBuilder.append("dbpedia-construct-100/");

				} else if (queryType == QUERYTYPE_DESCRIBE) {
					pathBuilder.append("dbpedia-describe-25/");

				} else {
					pathBuilder.append("dbpedia-select-100/");

				}

			} else {
				if (queryType == QUERYTYPE_ASK) {
					pathBuilder.append("swdf-ask-50/");

				} else if (queryType == QUERYTYPE_CONSTRUCT) {
					pathBuilder.append("swdf-construct-23/");

				} else if (queryType == QUERYTYPE_DESCRIBE) {
					pathBuilder.append("swdf-describe-100/");

				} else {
					pathBuilder.append("swdf-select-100/");
				}
			}

			pathBuilder.append("queries/queries.txt");
			File file = new File(directory, pathBuilder.toString());
			if (!file.exists()) {
				throw new FileNotFoundException("File not found: " + file.getAbsolutePath());
			} else if (!file.canRead()) {
				throw new IOException("Can not read file: " + file.getAbsolutePath());
			} else {
				return file;
			}
		}
	}

	/**
	 * Tests outside of JUnit, as files have provided manually.
	 */
	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.err.println("Please provide a directory containing the FEASIBLE benchmark queries as text files.");
			System.exit(1);
		}

		BenchmarkTxt benchmarkTxt = new BenchmarkTxt(new File(args[0]));

		// Check if all files present
		for (int querytype = 1; querytype <= 5; querytype++) {
			for (int dataset = 1; dataset <= 2; dataset++) {
				benchmarkTxt.getFile(querytype, dataset);
			}
		}

		// Check queries
		int queryCounter = 0;
		for (int querytype = 1; querytype <= 5; querytype++) {
			for (int dataset = 1; dataset <= 2; dataset++) {
				List<String> queries = benchmarkTxt.getQueries(querytype, dataset);
				queryCounter += queries.size();
				for (String query : queries) {
					if (query.length() < 16) {
						System.err.println("Query seems too short: " + query);
					}
				}
			}
		}
		int assumedResults = 100 + 100 + 25 + 100 + 50 + 23 + 100 + 100 + 175 + 175;
		if (queryCounter != assumedResults) {
			System.err.println("Expected " + assumedResults + " results, found " + queryCounter);
		}

		System.out.println("Tests finished");
	}

}