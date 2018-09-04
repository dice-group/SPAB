package org.dice_research.spabrun.iguana;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.io.FileReader;

public class SpabBenchmark {

	public static List<String> getQueryStrings(String filePath) {
		return FileReader.readFileToList(new File(filePath).getPath(), true, StandardCharsets.UTF_8.name());
	}

	public static void main(String[] args) {

		// Load configuration from file {@link Configuration#PROPERTIES}
		// Typically resources file "iguana-2018-01-20/file-locations.properties"
		Configuration configuration = new Configuration();

		Benchmark benchmark = new Benchmark("Iguana-2018-01-20-NonParallel-3DB");
		benchmark.setComment("Included: Fuseki, TNT, Virtuoso. Runtime is queries per second.");

		benchmark.addTripleStore(Task.CONNECTION_FUSEKI);
		benchmark.addTripleStore(Task.CONNECTION_TNT);
		benchmark.addTripleStore(Task.CONNECTION_VIRTUOSO);
		Map<String, TripleStore> tripleStores = benchmark.getTripleStoresMap();

		// Import RDF statements into memory DB
		File dbpediaResults = new File(configuration.get(Configuration.DBPEDIA_RESULTS_2018_01_20));
		// File swdfResults = new
		// File(configuration.get(Configuration.SWDF_RESULTS_2018_01_20));
		IguanaModel iguanaModel = new IguanaModel(dbpediaResults);

		IguanaExtractor iguanaExtractor = new IguanaExtractor(iguanaModel);
		iguanaExtractor.addTdb(Task.CONNECTION_FUSEKI);
		iguanaExtractor.addTdb(Task.CONNECTION_TNT);
		iguanaExtractor.addTdb(Task.CONNECTION_VIRTUOSO);
		// iguanaExtractor.addTdb(Task.CONNECTION_N_GRAPHSTORE);
		// iguanaExtractor.addTdb(Task.CONNECTION_NGRAPHSTORE);

		// Query index to list of QPS objects
		Map<Integer, List<QueriesPerSecond>> queryMap = iguanaExtractor.getNonParallelQueries();

		// Query strings from file
		List<String> queryStrings = getQueryStrings(configuration.get(Configuration.DBPEDIA_QUERIES_2018_01_20));

		for (Entry<Integer, List<QueriesPerSecond>> query : queryMap.entrySet()) {
			Integer queryId = query.getKey();
			String queryString = queryStrings.get(queryId);

			Query queryObj = benchmark.addQuery(queryId.toString(), queryString);

			for (QueriesPerSecond qpsObj : query.getValue()) {
				String tripleStoreId = qpsObj.getConnection();
				Double queriesPerSecond = qpsObj.getQueriesPerSecondValue();

				benchmark.addResult(tripleStores.get(tripleStoreId), queryObj, queriesPerSecond);
			}
		}

		benchmark.writeJsonFile("/tmp/iguana.json");
	}
}
