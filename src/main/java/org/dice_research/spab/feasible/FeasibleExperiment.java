package org.dice_research.spab.feasible;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.benchmark.InputSetsCreator;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.feasible.files.FeasibleFileAccesor;

public class FeasibleExperiment {

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

		FeasibleExperiment feasibleExperiment = new FeasibleExperiment(new File(args[0]), new File(args[1]));

		Benchmark benchmark = feasibleExperiment.createBenchmark(QueryType.ASK, Dataset.DBPEDIA);

		InputSetsCreator inputSetsCreator = new InputSetsCreator(benchmark);
		InputSets inputSets = inputSetsCreator.createMaxSizeSets(10, true);
		for (Triplestore triplestore : Triplestore.values()) {
			System.out.println(triplestore.name() + " " + inputSets.getPositives(triplestore.getCsvHeader()).size());
			System.out.println(triplestore.name() + " " + inputSets.getNegatives(triplestore.getCsvHeader()).size());
		}

	}

	public FeasibleExperiment(File directoryQueries, File directoryResults) throws IOException {
		feasibleFileAccesor = new FeasibleFileAccesor(directoryQueries, directoryResults);
	}

	private Benchmark createBenchmark(QueryType queryType, Dataset dataset) throws Exception {

		// Read data from files

		List<String> queries = feasibleFileAccesor.getQueries(queryType.ordinal(), queryType.ordinal());

		List<Map<String, Float>> results = feasibleFileAccesor.getResults(queryType.ordinal(), queryType.ordinal());
		if (queries.size() != results.size()) {
			throw new IoRuntimeException("Queries " + queries.size() + " != Results " + results.size());
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

}
