package org.dice_research.spabrun.iguana;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.io.FileReader;

/**
 * Creates benchmark sets for Iguana benchmark Spring 2018.
 * 
 * Creates in-memory triple store, imports RDF statements, extracts chosen
 * queries, writes JSON file.
 * 
 * DBpedia results: https://figshare.com/s/01a0dad8427c463f2b25
 * 
 * SWDF results: https://figshare.com/s/790f9441a36f15015252
 * 
 * @author Adrian Wilke
 */
public class SpabBenchmark {

	/**
	 * Configuration and execution of extraction.
	 */
	public static void main(String[] args) {

		// Load configuration from file {@link Configuration#PROPERTIES}
		// Typically resources file "iguana-2018-01-20/file-locations.properties"
		Configuration configuration = new Configuration();

		// Input: DBpedia or SWDF
		boolean useDbpedia = true;
		String queriesFilePath;
		String resultsFilePath;
		if (useDbpedia) {
			queriesFilePath = configuration.get(Configuration.DBPEDIA_QUERIES_2018_01_20);
			resultsFilePath = configuration.get(Configuration.DBPEDIA_RESULTS_2018_01_20);
		} else {
			queriesFilePath = configuration.get(Configuration.SWDF_QUERIES_2018_01_20);
			resultsFilePath = configuration.get(Configuration.SWDF_RESULTS_2018_01_20);
		}

		// Benchmark set metadata
		String benchmarkId = "Iguana-2018-Spring-NonParallel-DBpedia-3TDBs";
		String benchmarkComment = "" +

				"Benchmark: Iguana, spring 2018 (around 2018-01-20). "

				+ "DBpedia dataset. Only non-parallel queries. "

				+ "Included: Fuseki, TNT, Virtuoso. "

				+ "Results are queries per second.";

		// Output file
		String outputFilePath = "/tmp/" + benchmarkId + ".json";

		// Iguana triple stores
		List<String> tripleStores = new LinkedList<String>();
		if (Boolean.valueOf(true))
			tripleStores.add(Task.CONNECTION_FUSEKI);
		if (Boolean.valueOf(true))
			tripleStores.add(Task.CONNECTION_TNT);
		if (Boolean.valueOf(true))
			tripleStores.add(Task.CONNECTION_VIRTUOSO);
		if (Boolean.valueOf(false))
			tripleStores.add(Task.CONNECTION_N_GRAPHSTORE);
		if (Boolean.valueOf(false))
			tripleStores.add(Task.CONNECTION_NGRAPHSTORE);

		// Generate
		SpabBenchmark.generateBenchmarkSet(queriesFilePath, resultsFilePath, outputFilePath, benchmarkId,
				benchmarkComment, tripleStores);
	}

	/**
	 * Extracts data and writes JSON file.
	 */
	public static final void generateBenchmarkSet(String queriesFilePath, String resultsFilePath,
			String benchmarkSetFilePath, String benchmarkId, String benchmarkComment, List<String> tripleStores) {

		// Import RDF statements into memory DB
		IguanaModel iguanaModel = new IguanaModel(new File(resultsFilePath));

		// Set TDBs to extract
		IguanaExtractor iguanaExtractor = new IguanaExtractor(iguanaModel);
		for (String tripleStore : tripleStores) {
			iguanaExtractor.addTdb(tripleStore);
		}

		// Extract. Query index to list of QPS objects
		Map<Integer, List<QueriesPerSecond>> queryMap = iguanaExtractor.getNonParallelQueries();

		// Get query strings from file
		List<String> queryStrings = FileReader.readFileToList(new File(queriesFilePath).getPath(), true,
				StandardCharsets.UTF_8.name());

		// Create benchmark set
		Benchmark benchmark = new Benchmark(benchmarkId);
		benchmark.setComment(benchmarkComment);

		// Add TDBs to benchmark set
		for (String tripleStore : tripleStores) {
			benchmark.addTripleStore(tripleStore);
		}
		Map<String, TripleStore> tripleStoresMap = benchmark.getTripleStoresMap();

		// Go through extracted items and add them to benchmark set.
		for (Entry<Integer, List<QueriesPerSecond>> query : queryMap.entrySet()) {

			// Add queries (ID and string) to benchmark set
			Integer queryId = query.getKey();
			String queryString = queryStrings.get(queryId);
			Query queryObj = benchmark.addQuery(queryId.toString(), queryString);

			// Add results to benchmark sets
			for (QueriesPerSecond qpsObj : query.getValue()) {
				String tripleStoreId = qpsObj.getConnection();
				Double queriesPerSecond = qpsObj.getQueriesPerSecondValue();
				benchmark.addResult(tripleStoresMap.get(tripleStoreId), queryObj, queriesPerSecond);
			}
		}

		// Write results to file
		benchmark.writeJsonFile(benchmarkSetFilePath);
	}
}