package org.dice_research.spab.feasible.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * FEASIBLE file accessor.
 * 
 * @author Adrian Wilke
 */
public class FileAccesor {

	public final static int QUERYTYPE_ASK = 1;
	public final static int QUERYTYPE_CONSTRUCT = 2;
	public final static int QUERYTYPE_DESCRIBE = 3;
	public final static int QUERYTYPE_SELECT = 4;
	public final static int QUERYTYPE_MIX = 5;

	public final static int DATASET_DBPEDIA = 1;
	public final static int DATASET_SWDF = 2;

	/**
	 * Manual data access.
	 * 
	 * @param args
	 *            [0] Query directory containing the sub-directories
	 *            'Benchmarks_Errors' and 'Benchmarks_Evaluation'.
	 * 
	 *            [1] Results directory containing the 10 CSV files.
	 * 
	 *            [2] QUERYTYPE constant defined in {@link FileAccesor}.
	 * 
	 *            [3] DATASET constant defined in {@link FileAccesor}.
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {

		// To manually disable check of number of parameters in args
		boolean manuallyDefined = false;

		File directoryQueries = null;
		File directoryResults = null;
		int queryType = 0;
		int dataset = 0;

		if (args.length != 4 && !manuallyDefined) {
			System.err.println("Please provide 4 parameters.");
			System.exit(1);
		} else {
			if (directoryQueries == null)
				directoryQueries = new File(args[0]);
			if (directoryResults == null)
				directoryResults = new File(args[1]);
			if (queryType == 0)
				queryType = Integer.parseInt(args[2]);
			if (dataset == 0)
				dataset = Integer.parseInt(args[3]);
		}

		FileAccesor fileAccesor = new FileAccesor(directoryQueries, directoryResults);
		List<String> queries = fileAccesor.getQueries(queryType, dataset);
		List<Map<String, Float>> results = fileAccesor.getResults(queryType, dataset);

		System.out.println("Queries: ");
		System.out.println();
		for (int q = 0; q < queries.size(); q++) {
			System.out.println("No. " + (q + 1) + ", Index " + q);
			System.out.println(queries.get(q));
			System.out.println();
		}

		System.out.println("Results: ");
		System.out.println();
		for (int r = 0; r < results.size(); r++) {
			System.out.println("No. " + (r + 1) + ", Index " + r);
			for (Entry<String, Float> result : results.get(r).entrySet()) {
				System.out.println(result.getValue() + "  " + result.getKey());
			}
			System.out.println();
		}

		System.out.println("Queries: " + queries.size());
		System.out.println("Results: " + results.size());
	}

	protected QueriesTxt queriesTxt;
	protected ResultsCsv resultsCsv;

	/**
	 * Reads FEASIBLE files.
	 * 
	 * @param directoryQueries
	 *            containing the sub-directories 'Benchmarks_Errors' and
	 *            'Benchmarks_Evaluation'.
	 * @param directoryResults
	 *            containing 10 CSV result files.
	 * 
	 * @throws IOException
	 *             If one of the directories can not be accessed.
	 */
	public FileAccesor(File directoryQueries, File directoryResults) throws IOException {
		this.queriesTxt = new QueriesTxt(directoryQueries);
		this.resultsCsv = new ResultsCsv(directoryResults);
	}

	/**
	 * Gets SPARQL queries.
	 * 
	 * @param queryType
	 *            one of the QUERYTYPE constants defined in {@link FileAccesor}
	 * @param dataset
	 *            one of the DATASET constants defined in {@link FileAccesor}
	 */
	public List<String> getQueries(int queryType, int dataset) throws FileNotFoundException, IOException {
		return queriesTxt.getQueries(queryType, dataset);
	}

	/**
	 * Gets FEASIBLE benchmark results.
	 * 
	 * @param queryType
	 *            one of the QUERYTYPE constants defined in {@link FileAccesor}
	 * @param dataset
	 *            one of the DATASET constants defined in {@link FileAccesor}
	 */
	public List<Map<String, Float>> getResults(int queryType, int dataset) throws FileNotFoundException, IOException {
		return resultsCsv.getResults(queryType, dataset);
	}

}