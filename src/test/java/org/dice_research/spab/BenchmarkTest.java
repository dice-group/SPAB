package org.dice_research.spab;

import java.io.File;
import java.io.IOException;

import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.benchmark.InputSetsCreator;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.junit.Test;

/**
 * Tests benchmark serialization and creation of input sets.
 * 
 * @author Adrian Wilke
 */
public class BenchmarkTest extends AbstractTestCase {

	public static final String BENCHMARK_ID = "Benchmark 2000";
	public static final String BENCHMARK_COMMENT = "The best benchmark of the century.";
	public static final String TRIPLESTORE_A_ID = "Triple Store A";
	public static final String TRIPLESTORE_B_ID = "Triple Store B";
	public static final String TRIPLESTORE_C_ID = "Triple Store C";
	public static final String QUERYSTRING_A = "SELECT ?s WHERE { ?s <A> ?o }";
	public static final String QUERYSTRING_B = "SELECT ?s WHERE { ?s <B> ?o }";
	public static final String QUERY_ID_A = "1";
	public static final String QUERY_ID_B = "2";

	public static final Double RUNTIME_A_ON_A = 11d;
	public static final Double RUNTIME_B_ON_A = 21d;
	public static final Double RUNTIME_A_ON_B = 12d;

	@Test
	public void testCreatePercentual() {

		Benchmark benchmark = createBenchmark(false);
		TripleStore tripleStoreA = benchmark.getTripleStore(TRIPLESTORE_A_ID);
		TripleStore tripleStoreB = benchmark.getTripleStore(TRIPLESTORE_B_ID);
		TripleStore tripleStoreC = benchmark.getTripleStore(TRIPLESTORE_C_ID);
		Query queryA = benchmark.getQuery(QUERYSTRING_A);

		// TS-A should have Q-A in positive set.
		// Mean runtime is 110.
		// Percentual deviation: 110 * (1 - 0.1) = 99 -> no
		// Percentual deviation: 110 * (1 - 0.09) = 100.01 -> ok, 100 < 100.1
		benchmark.addResult(tripleStoreA, queryA, 100);
		benchmark.addResult(tripleStoreB, queryA, 110);
		benchmark.addResult(tripleStoreC, queryA, 120);
		InputSetsCreator inputSetsCreator = new InputSetsCreator(benchmark);

		InputSets inputSets = inputSetsCreator.createPercentual(0.09, true);
		assertEquals(inputSets.getPositives(TRIPLESTORE_A_ID).size(), 1);
		assertEquals(inputSets.getPositives(TRIPLESTORE_B_ID).size(), 0);
		assertEquals(inputSets.getNegatives(TRIPLESTORE_C_ID).size(), 1);

		// Negate test
		inputSets = inputSetsCreator.createPercentual(0.09, false);
		assertEquals(inputSets.getNegatives(TRIPLESTORE_A_ID).size(), 1);
		assertEquals(inputSets.getPositives(TRIPLESTORE_B_ID).size(), 0);
		assertEquals(inputSets.getPositives(TRIPLESTORE_C_ID).size(), 1);

		// Results have only to be not arithmetic mean
		inputSets = inputSetsCreator.createPercentual(0, true);
		assertEquals(inputSets.getPositives(TRIPLESTORE_A_ID).size(), 1);
		assertEquals(inputSets.getPositives(TRIPLESTORE_B_ID).size(), 0);
		assertEquals(inputSets.getPositives(TRIPLESTORE_C_ID).size(), 0);

		// No runtime is good/bad enough
		inputSets = inputSetsCreator.createPercentual(0.1, true);
		assertEquals(inputSets.getPositives(TRIPLESTORE_A_ID).size(), 0);
		assertEquals(inputSets.getPositives(TRIPLESTORE_B_ID).size(), 0);
		assertEquals(inputSets.getPositives(TRIPLESTORE_C_ID).size(), 0);
		assertEquals(inputSets.getNegatives(TRIPLESTORE_A_ID).size(), 0);
		assertEquals(inputSets.getNegatives(TRIPLESTORE_B_ID).size(), 0);
		assertEquals(inputSets.getNegatives(TRIPLESTORE_C_ID).size(), 0);
	}

	@Test
	public void testBenchmarkSerialization() {

		Benchmark benchmark = createBenchmark(true);
		TripleStore tripleStoreA = benchmark.getTripleStore(TRIPLESTORE_A_ID);
		TripleStore tripleStoreB = benchmark.getTripleStore(TRIPLESTORE_B_ID);
		Query queryA = benchmark.getQuery(QUERYSTRING_A);
		Query queryB = benchmark.getQuery(QUERYSTRING_B);

		assertNotNull(benchmark.getTripleStore(TRIPLESTORE_A_ID));
		assertNull(benchmark.getTripleStore("not existent triple store"));

		assertNotNull(benchmark.getQueryById(QUERY_ID_A));
		assertNull(benchmark.getQueryById("not existing query id"));

		assertNotNull(benchmark.getQuery(QUERYSTRING_A));
		assertNull(benchmark.getQuery("not existing query string"));

		assertNotNull(benchmark.getResult(tripleStoreA, queryA));
		assertNull(benchmark.getResult(tripleStoreB, queryB));

		// Export

		File tempFile;
		try {
			tempFile = File.createTempFile("SPAB", ".json");
			tempFile.deleteOnExit();
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		}
		benchmark.writeJsonFile(tempFile.getPath());

		// Import

		Benchmark restoredBenchmark = Benchmark.readJsonFile(tempFile.getPath());
		assertEquals(benchmark.getBenchmarkId(), restoredBenchmark.getBenchmarkId());
		assertEquals(benchmark.getComment(), restoredBenchmark.getComment());
		assertEquals(benchmark.getTripleStores().size(), restoredBenchmark.getTripleStores().size());
		assertEquals(benchmark.getQueries().size(), restoredBenchmark.getQueries().size());
		assertEquals(benchmark.getResults().size(), restoredBenchmark.getResults().size());
	}

	private Benchmark createBenchmark(boolean addResults) {
		Benchmark benchmark;

		benchmark = new Benchmark(BENCHMARK_ID);
		benchmark.setComment(BENCHMARK_COMMENT);

		TripleStore tripleStoreA = benchmark.addTripleStore(TRIPLESTORE_A_ID);
		TripleStore tripleStoreB = benchmark.addTripleStore(TRIPLESTORE_B_ID);
		benchmark.addTripleStore(TRIPLESTORE_C_ID);

		Query queryA = benchmark.addQuery(QUERY_ID_A, QUERYSTRING_A);
		Query queryB = benchmark.addQuery(QUERY_ID_B, QUERYSTRING_B);

		if (addResults) {
			// For testing: No combination TS-B and Q-B
			benchmark.addResult(tripleStoreA, queryA, RUNTIME_A_ON_A);
			benchmark.addResult(tripleStoreA, queryB, RUNTIME_B_ON_A);
			benchmark.addResult(tripleStoreB, queryA, RUNTIME_A_ON_B);
		}

		return benchmark;
	}
}