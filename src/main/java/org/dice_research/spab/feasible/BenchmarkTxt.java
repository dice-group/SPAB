package org.dice_research.spab.feasible;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
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
	 * Gets TXT files containing SPARQL queries in directory.
	 */
	public List<File> getQueryTextFiles() throws IOException {
		List<File> files = new LinkedList<File>();
		for (int querytype = 1; querytype <= 5; querytype++) {
			for (int dataset = 1; dataset <= 2; dataset++) {
				files.add(getFile(querytype, dataset));
			}
		}
		return files;
	}

	/**
	 * Writes text files with queries into root of given directory. Queries are
	 * separated by two empty lines.
	 * 
	 * Query number, Triplestore ID, result
	 */
	public void generateToLinesSplittedFiles() throws FileNotFoundException, IOException {
		for (int querytype = 1; querytype <= 5; querytype++) {
			for (int dataset = 1; dataset <= 2; dataset++) {

				List<String> queries = getQueries(querytype, dataset);
				StringBuilder stringBuilder = new StringBuilder();
				for (String query : queries) {
					LineIterator lineIterator = IOUtils.lineIterator(new StringReader(query));
					boolean empty = false;
					boolean firstLine = true;
					while (lineIterator.hasNext()) {
						String line = lineIterator.next();
						if (line.trim().isEmpty()) {
							if (!empty) {
								if (!firstLine) {
									stringBuilder.append(System.lineSeparator());
								}
							}
							empty = true;
						} else {
							if (!firstLine) {
								stringBuilder.append(System.lineSeparator());
							}
							stringBuilder.append(line);
							empty = false;
						}
						firstLine = false;
					}
					stringBuilder.append(System.lineSeparator());
					stringBuilder.append(System.lineSeparator());
					stringBuilder.append(System.lineSeparator());
				}

				StringBuilder filenameBuilder = new StringBuilder();
				filenameBuilder.append("FEASIBLE-");
				if (dataset == DATASET_DBPEDIA) {
					filenameBuilder.append("DBpedia-");
				} else {
					filenameBuilder.append("SWDF-");
				}

				if (querytype == QUERYTYPE_ASK) {
					filenameBuilder.append("ASK-");
				} else if (querytype == QUERYTYPE_CONSTRUCT) {
					filenameBuilder.append("CONSTRUCT-");
				} else if (querytype == QUERYTYPE_DESCRIBE) {
					filenameBuilder.append("DESCRIBE-");
				} else if (querytype == QUERYTYPE_SELECT) {
					filenameBuilder.append("SELECT-");
				} else {
					filenameBuilder.append("MIX-");
				}

				filenameBuilder.append(queries.size());
				filenameBuilder.append(".queries.txt");
				FileUtils.writeStringToFile(new File(directory, filenameBuilder.toString()), stringBuilder.toString(),
						StandardCharsets.UTF_8);
			}
		}

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

		// Check files
		if (benchmarkTxt.getQueryTextFiles().size() != 5 * 2) {
			System.err.println("Expected " + 5 * 2 + " results, found " + benchmarkTxt.getQueryTextFiles().size());
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