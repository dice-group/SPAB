package org.dice_research.spab.feasible.files;

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

/**
 * Parses SPARQL queries FEASIBLE text files.
 * 
 * 
 * The method {@link QueriesTxt#main(String[])} tests the import.
 * 
 * The method {@link QueriesTxt#getQueries(int, int)} returns results of a
 * respective file. int queryType is one of the QUERYTYPE constants defined in
 * {@link FeasibleFileAccesor}. int dataset is one of the DATASET constants
 * defined in {@link FeasibleFileAccesor}.
 * 
 * The method {@link QueriesTxt#generateToLinesSplittedFiles()} generates files
 * for the SPAB webdemo.
 * 
 * 
 * @see https://github.com/dice-group/feasible#downloads
 *
 * @author Adrian Wilke
 */
public class QueriesTxt {

	protected File directory;

	/**
	 * Checks existing directory.
	 * 
	 * @param directory containing the sub-directories 'Benchmarks_Errors' and
	 *                  'Benchmarks_Evaluation'.
	 */
	public QueriesTxt(File directory) throws IOException {
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
	 *                  {@link FeasibleFileAccesor}.
	 * @param dataset   is one of the DATASET constants defined in
	 *                  {@link FeasibleFileAccesor}.
	 * @return A list of SPARQL queries.
	 */
	public List<String> getQueries(int queryType, int dataset) throws FileNotFoundException, IOException {
		List<String> queries = new LinkedList<String>();
		File file = getFile(queryType, dataset);
		LineIterator lineIterator = IOUtils.lineIterator(new FileReader(file));
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
		for (int querytype = 0; querytype <= 4; querytype++) {
			for (int dataset = 0; dataset <= 1; dataset++) {
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
			for (int dataset = 0; dataset <= 1; dataset++) {

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
				if (dataset == FeasibleFileAccesor.DATASET_DBPEDIA) {
					filenameBuilder.append("DBpedia-");
				} else {
					filenameBuilder.append("SWDF-");
				}

				if (querytype == FeasibleFileAccesor.QUERYTYPE_ASK) {
					filenameBuilder.append("ASK-");
				} else if (querytype == FeasibleFileAccesor.QUERYTYPE_CONSTRUCT) {
					filenameBuilder.append("CONSTRUCT-");
				} else if (querytype == FeasibleFileAccesor.QUERYTYPE_DESCRIBE) {
					filenameBuilder.append("DESCRIBE-");
				} else if (querytype == FeasibleFileAccesor.QUERYTYPE_SELECT) {
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
		pathBuilder
				.append(queryType == FeasibleFileAccesor.QUERYTYPE_MIX ? "mix-benchmarks/" : "individual-benchmarks/");

		if (dataset == FeasibleFileAccesor.DATASET_DBPEDIA) {
			pathBuilder.append("dbpedia351/");
		} else if (dataset == FeasibleFileAccesor.DATASET_SWDF) {
			pathBuilder.append("swdf/");
		} else {
			throw new RuntimeException("Unknown dataset: " + dataset);

		}

		if (queryType == FeasibleFileAccesor.QUERYTYPE_MIX) {
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

			if (dataset == FeasibleFileAccesor.DATASET_DBPEDIA) {
				if (queryType == FeasibleFileAccesor.QUERYTYPE_ASK) {
					pathBuilder.append("dbpedia-ask-100/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_CONSTRUCT) {
					pathBuilder.append("dbpedia-construct-100/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_DESCRIBE) {
					pathBuilder.append("dbpedia-describe-25/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_SELECT) {
					pathBuilder.append("dbpedia-select-100/");

				} else {
					throw new RuntimeException("Unknown queryType: " + queryType);

				}

			} else {
				if (queryType == FeasibleFileAccesor.QUERYTYPE_ASK) {
					pathBuilder.append("swdf-ask-50/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_CONSTRUCT) {
					pathBuilder.append("swdf-construct-23/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_DESCRIBE) {
					pathBuilder.append("swdf-describe-100/");

				} else if (queryType == FeasibleFileAccesor.QUERYTYPE_SELECT) {
					pathBuilder.append("swdf-select-100/");

				} else {
					throw new RuntimeException("Unknown queryType: " + queryType);

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

		QueriesTxt benchmarkTxt = new QueriesTxt(new File(args[0]));

		// Check files
		if (benchmarkTxt.getQueryTextFiles().size() != 5 * 2) {
			System.err.println("Expected " + 5 * 2 + " results, found " + benchmarkTxt.getQueryTextFiles().size());
		}

		// Check queries
		int queryCounter = 0;
		for (int querytype = 0; querytype <= 4; querytype++) {
			for (int dataset = 0; dataset <= 1; dataset++) {
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