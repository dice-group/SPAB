package org.dice_research.spab.benchmark;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.dice_research.spab.exceptions.IoRuntimeException;

/**
 * Uses {@link Benchmark} objects to create {@link InputSets} for SPAB.
 * 
 * @author Adrian Wilke
 */
public class InputSetsCreator {

	private Benchmark benchmark;

	public InputSetsCreator(Benchmark benchmark) {
		this.benchmark = benchmark;
	}

	public InputSets createStandardDeviationSets(double factor, boolean smallIsPositive) {

		// Check, if there is a result for every triple store and every query.
		try {
			checkBenchmark();
		} catch (Exception e) {
			throw new IoRuntimeException(e);
		}

		boolean positiveSet = true;
		boolean negativeSet = false;
		if (!smallIsPositive) {
			positiveSet = false;
			negativeSet = true;
		}

		InputSets inputSets = new InputSets(benchmark.getTripleStoreIds());

		Map<Query, List<Result>> queriesToResults = benchmark.getResultsOrderedbyQueries();
		Map<Query, Double> queriesToMeans = getArithmeticMeans(queriesToResults);
		Map<Query, Double> queriesToStandardDeviations = getStandardDeviations(queriesToResults);

		for (Entry<Query, List<Result>> queryToResults : benchmark.getResultsOrderedbyQueries().entrySet()) {
			Double arithmeticMean = queriesToMeans.get(queryToResults.getKey());
			Double standardDeviation = queriesToStandardDeviations.get(queryToResults.getKey());

			for (Result result : queryToResults.getValue()) {

				if (result.getResult() < (arithmeticMean - (standardDeviation * factor))) {
					inputSets.addQuery(result.getTripleStore().getTripleStoreId(), positiveSet,
							queryToResults.getKey());
				}
				if (result.getResult() > (arithmeticMean + (standardDeviation * factor))) {
					inputSets.addQuery(result.getTripleStore().getTripleStoreId(), negativeSet,
							queryToResults.getKey());
				}
			}
		}

		return inputSets;
	}

	/**
	 * Creates positive/negative set for results, which deviate from the arithmetic
	 * mean.
	 * 
	 * @param percentageDeviation
	 *            0: Every value less/greater than the arithmetic mean is used in
	 *            positive/negative sets. 0.2: values which have a divergence of at
	 *            least 20 percent of the arithmetic mean are used in sets.
	 * @param smallIsPositive
	 *            true for runtimes, false for number of executions per time period.
	 */
	public InputSets createPercentualSets(double percentageDeviation, boolean smallIsPositive) {

		// Check, if there is a result for every triple store and every query.
		try {
			checkBenchmark();
		} catch (Exception e) {
			throw new IoRuntimeException(e);
		}

		boolean positiveSet = true;
		boolean negativeSet = false;
		if (!smallIsPositive) {
			positiveSet = false;
			negativeSet = true;
		}

		InputSets inputSets = new InputSets(benchmark.getTripleStoreIds());

		Map<Query, List<Result>> queriesToResults = benchmark.getResultsOrderedbyQueries();
		Map<Query, Double> queriesToMeans = getArithmeticMeans(queriesToResults);

		for (Entry<Query, List<Result>> queryToResults : benchmark.getResultsOrderedbyQueries().entrySet()) {
			Double arithmeticMean = queriesToMeans.get(queryToResults.getKey());
			for (Result result : queryToResults.getValue()) {
				if (result.getResult() < arithmeticMean * (1d - percentageDeviation)) {
					inputSets.addQuery(result.getTripleStore().getTripleStoreId(), positiveSet,
							queryToResults.getKey());
				}
				if (result.getResult() > arithmeticMean * (1d + percentageDeviation)) {
					inputSets.addQuery(result.getTripleStore().getTripleStoreId(), negativeSet,
							queryToResults.getKey());
				}
			}
		}

		return inputSets;
	}

	private Map<Query, Double> getStandardDeviations(Map<Query, List<Result>> queriesToResults) {
		Map<Query, Double> standardDeviations = new LinkedHashMap<Query, Double>();
		for (Query query : queriesToResults.keySet()) {
			List<Result> results = queriesToResults.get(query);
			double[] resultValues = new double[results.size()];
			for (int i = 0; i < results.size(); i++) {
				resultValues[i] = results.get(i).getResult();
				System.out.println(resultValues[i]);
			}
			standardDeviations.put(query, new StandardDeviation().evaluate(resultValues));
		}
		return standardDeviations;
	}

	private Map<Query, Double> getArithmeticMeans(Map<Query, List<Result>> queriesToResults) {
		Map<Query, Double> arithmeticMeans = new LinkedHashMap<Query, Double>();
		for (Query query : queriesToResults.keySet()) {
			List<Result> results = queriesToResults.get(query);
			double sum = 0;
			for (Result result : results) {
				sum += result.getResult();
			}
			arithmeticMeans.put(query, sum / results.size());
		}
		return arithmeticMeans;
	}

	/**
	 * Checks, if there is a result for every triple store and every query.
	 * 
	 * @throws Exception
	 *             if entry is missing
	 */
	private void checkBenchmark() throws Exception {
		Map<String, Set<String>> queryidsToTriplestoreids = new HashMap<String, Set<String>>();

		// Add all queries
		for (Result result : benchmark.getResults()) {
			String queryId = result.getQuery().getQueryId();
			if (!queryidsToTriplestoreids.containsKey(queryId)) {
				queryidsToTriplestoreids.put(queryId, new HashSet<String>());
			}
			queryidsToTriplestoreids.get(queryId).add(result.getTripleStore().getTripleStoreId());
		}

		// Check, if there is a triple store result for all queries
		int numberOfTripleStores = benchmark.getTripleStores().size();
		for (String queryId : queryidsToTriplestoreids.keySet()) {
			if (numberOfTripleStores != queryidsToTriplestoreids.get(queryId).size()) {
				throw new Exception("For query ID " + queryId + " there are " + queryidsToTriplestoreids.size()
						+ " triple stores instead of " + numberOfTripleStores);
			}
		}
	}
}