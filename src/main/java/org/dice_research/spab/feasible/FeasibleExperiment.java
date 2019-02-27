package org.dice_research.spab.feasible;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.io.FileUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.BenchmarkNullException;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.benchmark.InputSetsCreator;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.feasible.ExperimentResult.InputSetsCreationType;
import org.dice_research.spab.feasible.enumerations.Dataset;
import org.dice_research.spab.feasible.enumerations.QueryType;
import org.dice_research.spab.feasible.enumerations.Triplestore;
import org.dice_research.spab.feasible.errors.DefectiveQueries;
import org.dice_research.spab.feasible.files.FeasibleFileAccesor;

public class FeasibleExperiment {

	private static int MODE = 2;

	public static final int MODE_BEST_CREATOR = 1;
	public static final int MODE_ALL_SETS = 2;

	private static final float LAMBDA = 0.1f;
	private static final int MAX_ITERATIONS = 10;
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

		if (MODE == MODE_BEST_CREATOR) {
			experiment.experientOne();

		} else if (MODE == MODE_ALL_SETS) {

			File file = new File("/tmp/experiment.txt");
			for (QueriesContainer container : experiment.createQueryContainers()) {
				System.out.println(container);

				// TODO: Virtuoso and Fuseki have so many negative benchmark results? -> There
				// is a bug in the house. Should be inverted.

				FileUtils.write(file, container.toString() + "\n", StandardCharsets.UTF_8, true);
				Thread.sleep(1000);
				int iterations = Math.min(container.queriesPositive.size(), container.queriesNegative.size());
				for (int i = 1; i <= iterations; i++) {
					SpabApi spabApi = experiment.runSpab(container.queriesPositive, container.queriesNegative);
					FileUtils.write(file, "" + i + "\n", StandardCharsets.UTF_8, true);
					Thread.sleep(1000);
					FileUtils.write(file, spabApi.getBestCandidates().get(0).getInfoLine() + "\n",
							StandardCharsets.UTF_8, true);
					Thread.sleep(1000);
				}

			}
		}

	}

	public FeasibleExperiment(File directoryQueries, File directoryResults) throws IOException {
		feasibleFileAccesor = new FeasibleFileAccesor(directoryQueries, directoryResults);
	}

	/**
	 * Creates query sets and query properties
	 */
	public Set<QueriesContainer> createQueryContainers() throws Exception {
		Set<QueriesContainer> containers = new TreeSet<>();
		for (QueryType queryType : QueryType.values()) {
			for (Dataset dataset : Dataset.values()) {
				Benchmark benchmark = createBenchmark(queryType, dataset);
				InputSets inputSets = new InputSetsCreator(benchmark).createMaxSizeSets(benchmark.getQueries().size(),
						false);
				for (Triplestore triplestore : Triplestore.values()) {
					containers.add(new QueriesContainer().setQueryType(queryType).setDataset(dataset)
							.setTriplestore(triplestore)
							.setQueriesPositive(inputSets.getPositives(triplestore.getCsvHeader()))
							.setQueriesNegative(inputSets.getNegatives(triplestore.getCsvHeader())));
				}
			}
		}
		return containers;
	}

	/**
	 * First experiment
	 */
	public void experientOne() throws Exception {

		// Create input sets

		List<ExperimentResult> results = new LinkedList<>();
		for (QueryType queryType : QueryType.values()) {
			for (Dataset dataset : Dataset.values()) {
				Benchmark benchmark = createBenchmark(queryType, dataset);
				ExperimentResult result = searchInputSetUsingAllCreators(benchmark);
				if (result != null) {
					result.queryType = queryType;
					result.dataset = dataset;
					results.add(result);
				}
			}
		}

		// Run SPAB

		for (ExperimentResult result : results) {
			for (Triplestore triplestore : result.triplestores) {
				SpabApi spabApi = runSpab(result.inputSets.getPositives(triplestore.getCsvHeader()),
						result.inputSets.getNegatives(triplestore.getCsvHeader()));
				result.spabApis.add(spabApi);
			}
		}

		// Put results

		for (ExperimentResult result : results) {
			System.out.println(result);
		}
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
	 * Returns an acceptable result or null.
	 */
	private ExperimentResult searchInputSetUsingAllCreators(Benchmark benchmark) throws BenchmarkNullException {
		InputSetsCreator inputSetsCreator = new InputSetsCreator(benchmark);
		float argument;
		InputSets inputSets;
		List<Triplestore> triplesstores;

		argument = 1;
		inputSets = inputSetsCreator.createStandardDeviationSets(argument, false);
		triplesstores = getAcceptable(inputSets, MINIMUM_SET_SIZE);
		if (!triplesstores.isEmpty()) {
			ExperimentResult result = new ExperimentResult();
			result.inputSetsCreationType = InputSetsCreationType.STDDEV;
			result.inputSetsCreationTypeArgument = argument;
			result.inputSets = inputSets;
			result.triplestores = triplesstores;
			return result;
		}

		argument = 10;
		inputSets = inputSetsCreator.createPercentualSets(0.2, false);
		triplesstores = getAcceptable(inputSets, MINIMUM_SET_SIZE);
		if (!triplesstores.isEmpty()) {
			ExperimentResult result = new ExperimentResult();
			result.inputSetsCreationType = InputSetsCreationType.PERCENTUAL;
			result.inputSetsCreationTypeArgument = argument;
			result.inputSets = inputSets;
			result.triplestores = triplesstores;
			return result;
		}

		argument = MINIMUM_SET_SIZE;
		inputSets = inputSetsCreator.createMaxSizeSets(MINIMUM_SET_SIZE, false);
		triplesstores = getAcceptable(inputSets, MINIMUM_SET_SIZE);
		if (!triplesstores.isEmpty()) {
			ExperimentResult result = new ExperimentResult();
			result.inputSetsCreationType = InputSetsCreationType.SIZE;
			result.inputSetsCreationTypeArgument = argument;
			result.inputSets = inputSets;
			result.triplestores = triplesstores;
			return result;
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