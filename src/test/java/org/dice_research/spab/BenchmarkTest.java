package org.dice_research.spab;

import java.io.File;
import java.io.IOException;

import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.Runtime;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.junit.Test;

/**
 * Tests benchmarks.
 * 
 * @author Adrian Wilke
 */
public class BenchmarkTest extends AbstractTestCase {

	public static final String benchmarkId = "Benchmark 2000";
	public static final String benchmarkComment = "The best benchmark of the century.";
	public static final String tripleStoreAId = "Triple Store A";
	public static final String tripleStoreBId = "Triple Store B";
	public static final String queryStringA = "SELECT ?s WHERE { ?s <A> ?o }";
	public static final String queryStringB = "SELECT ?s WHERE { ?s <B> ?o }";
	public static final String queryIdA = "1";
	public static final String queryIdB = "2";

	@Test
	public void test() {
		Benchmark benchmark = new Benchmark(benchmarkId);
		benchmark.setComment(benchmarkComment);

		TripleStore tripleStoreA = benchmark.addTripleStore(tripleStoreAId);
		TripleStore tripleStoreB = benchmark.addTripleStore(tripleStoreBId);

		Query queryA = benchmark.addQuery(queryIdA, queryStringA);
		Query queryB = benchmark.addQuery(queryIdB, queryStringB);

		// For testing: No combination TS-B and Q-B
		Runtime runtimeAA = benchmark.addRuntime(tripleStoreA, queryA, 1111);
		Runtime runtimeAB = benchmark.addRuntime(tripleStoreA, queryB, 1122);
		Runtime runtimeBA = benchmark.addRuntime(tripleStoreB, queryA, 2211);

		if (PRINT) {
			System.out.println(benchmark);
			System.out.println(benchmark.getComment());

			System.out.println(tripleStoreA);
			System.out.println(tripleStoreB);

			System.out.println(queryIdA);
			System.out.println(queryIdB);

			System.out.println(queryA);
			System.out.println(queryB);

			System.out.println(runtimeAA);
			System.out.println(runtimeAB);
			System.out.println(runtimeBA);
		}

		assertNotNull(benchmark.getTripleStore(tripleStoreAId));
		assertNull(benchmark.getTripleStore("not existent triple store"));

		assertNotNull(benchmark.getQueryById(queryIdA));
		assertNull(benchmark.getQueryById("not existing query id"));

		assertNotNull(benchmark.getQuery(queryStringA));
		assertNull(benchmark.getQuery("not existing query string"));

		assertNotNull(benchmark.getRuntime(tripleStoreA, queryA));
		assertNull(benchmark.getRuntime(tripleStoreB, queryB));

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
		assertEquals(benchmark.getRuntimes().size(), restoredBenchmark.getRuntimes().size());
	}
}