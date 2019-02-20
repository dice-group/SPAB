package org.dice_research.spab.feasible;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.BenchmarkNullException;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.benchmark.InputSetsCreator;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.feasible.errors.DefectiveQueries;
import org.dice_research.spab.feasible.files.FeasibleFileAccesor;
import org.dice_research.spab.structures.CandidateVertex;

public class FeasibleExperiment {

	private static final float LAMBDA = 0.1f;
	private static final int MAX_ITERATIONS = 100;
	private static final int MINIMUM_SET_SIZE = 5;

	private FeasibleFileAccesor feasibleFileAccesor;

	/**
	 * Main entry point
	 * 
	 * @param args [0] containing the sub-directories 'Benchmarks_Errors' and
	 *             'Benchmarks_Evaluation'.
	 * 
	 *             [1] containing 10 CSV result files.
	 */
	public static void main(String[] args) throws Exception {

		FeasibleExperiment experiment = new FeasibleExperiment(new File(args[0]), new File(args[1]));

		List<InputSets> inputSetsList = new LinkedList<>();
		for (QueryType queryType : QueryType.values()) {
			for (Dataset dataset : Dataset.values()) {
				Benchmark benchmark = experiment.createBenchmark(queryType, dataset);
				InputSets inputSets = experiment.searchInputSet(benchmark);
				if (inputSets != null) {
					System.out.println(queryType.name() + " " + dataset.name());
					inputSetsList.add(inputSets);
				}
			}
		}

// TODO: Run SPAB
//		for (InputSets inputSets : inputSetsList) {
//			SpabApi spabApi = experiment.runSpab(inputSets.getPositives(triplestore.getCsvHeader()),
//					inputSets.getNegatives(triplestore.getCsvHeader()));
//
//			System.out.println(spabApi.getBestCandidates().get(0).getScore());
//			System.out.println(spabApi.getBestCandidates().get(0).getInfoLine());
//		}

	}

	public FeasibleExperiment(File directoryQueries, File directoryResults) throws IOException {
		feasibleFileAccesor = new FeasibleFileAccesor(directoryQueries, directoryResults);
	}

	/**
	 * Creates benchmark using all available triple-stores.
	 */
	private Benchmark createBenchmark(QueryType queryType, Dataset dataset) throws Exception {

		// Read data from files

		List<String> queries = feasibleFileAccesor.getQueries(queryType.ordinal(), dataset.ordinal());

		List<Map<String, Float>> results = feasibleFileAccesor.getResults(queryType.ordinal(), dataset.ordinal());
		if (queries.size() != results.size()) {
			throw new IoRuntimeException("Queries " + queries.size() + " != Results " + results.size());
		}

		// Remove defective queries

		List<Integer> defective = DefectiveQueries.getAll(queryType.ordinal(), dataset.ordinal());
		Collections.sort(defective, new Comparator<Integer>() {
			@Override
			public int compare(Integer x, Integer y) {
				return Integer.compare(y, x);
			}
		});
		for (Integer d : defective) {
			queries.remove(d.intValue());
			results.remove(d.intValue());
		}

		// Create benchmark

		Benchmark benchmark = new Benchmark("FEASIBLE");

		List<TripleStore> triplestores = new LinkedList<>();
		for (Triplestore triplestore : Triplestore.values()) {
			triplestores.add(benchmark.addTripleStore(triplestore.getCsvHeader()));
		}

		for (int q = 0; q < queries.size(); q++) {
			benchmark.addQuery("" + q, queries.get(q));
		}

		for (int r = 0; r < results.size(); r++) {
			for (TripleStore tripleStore : triplestores) {
				benchmark.addResult(tripleStore, benchmark.getQueryById("" + r),
						results.get(r).get(tripleStore.getTripleStoreId()));
			}
		}

		return benchmark;
	}

	/**
	 * Returns an acceptable InputSets-object or null.
	 */
	private InputSets searchInputSet(Benchmark benchmark) throws BenchmarkNullException {
		InputSetsCreator inputSetsCreator = new InputSetsCreator(benchmark);
		InputSets inputSets;

		inputSets = inputSetsCreator.createStandardDeviationSets(1, false);
		if (!getAcceptable(inputSets, MINIMUM_SET_SIZE).isEmpty()) {
			return inputSets;
		}

		inputSets = inputSetsCreator.createPercentualSets(10, false);
		if (!getAcceptable(inputSets, MINIMUM_SET_SIZE).isEmpty()) {
			return inputSets;
		}

		inputSets = inputSetsCreator.createMaxSizeSets(MINIMUM_SET_SIZE, false);
		if (!getAcceptable(inputSets, MINIMUM_SET_SIZE).isEmpty()) {
			return inputSets;
		}

		return null;
	}

	/**
	 * Returns triplestores for wich the given input-sets-object contains a minimum
	 * set size of positice and negative queries.
	 */
	private List<Triplestore> getAcceptable(InputSets inputSets, int minimumSetSize) {
		List<Triplestore> tripleStores = new LinkedList<>();
		for (Triplestore triplestore : Triplestore.values()) {
			if (inputSets.getPositives(triplestore.getCsvHeader()).size() >= minimumSetSize
					&& inputSets.getNegatives(triplestore.getCsvHeader()).size() >= minimumSetSize) {
				tripleStores.add(triplestore);
			}
		}
		return tripleStores;
	}

	/**
	 * Runs SPAB.
	 */
	private SpabApi runSpab(List<Query> positives, List<Query> negatives) throws SpabException {
		SpabApi spabApi = new SpabApi();
		spabApi.setLambda(LAMBDA);
		spabApi.setMaxIterations(MAX_ITERATIONS);
		for (Query query : positives) {
			spabApi.addPositive(query.getQueryString());
		}
		for (Query query : negatives) {
			spabApi.addNegative(query.getQueryString());
		}
		spabApi.run();
		return spabApi;
	}
}