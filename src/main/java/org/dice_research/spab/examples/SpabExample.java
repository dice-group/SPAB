package org.dice_research.spab.examples;

import java.util.List;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.SparqlUnit;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.Resources;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabExample {

	/**
	 * Configuration: Number of iterations. Test run with 1000 iterations took about
	 * 9 seconds
	 */
	public static final float LAMBDA = .1f;

	/**
	 * Configuration: Number of iterations. Test run with 1000 iterations took about
	 * 9 seconds
	 */
	public static final int MAX_ITERATIONS = 100;

	/**
	 * Configuration: Use Fuseki or Virtuoso file.
	 */
	public static final boolean USE_FUSEKI = true;

	/**
	 * Configuration: Number of SPARQL queries added to set of negative examples.
	 */
	public static final int NUMBER_OF_NEGATIVES = 10;

	/**
	 * Configuration: Number of SPARQL queries added to set of positives examples.
	 */
	public static final int NUMBER_OF_POSITIVES = 10;

	public static final String RESOURCE_IGUANA_FUSEKI_NEGATIVE = "iguana-2018-01-20/Fuseki-negative.txt";
	public static final String RESOURCE_IGUANA_FUSEKI_POSITIVE = "iguana-2018-01-20/Fuseki-positive.txt";

	public static final String RESOURCE_IGUANA_VIRTUOSO_NEGATIVE = "iguana-2018-01-20/Virtuoso-negative.txt";
	public static final String RESOURCE_IGUANA_VIRTUOSO_POSITIVE = "iguana-2018-01-20/Virtuoso-positive.txt";

	public static void main(String[] args) throws SpabException {

		String negFile = RESOURCE_IGUANA_VIRTUOSO_NEGATIVE;
		String posFile = RESOURCE_IGUANA_VIRTUOSO_POSITIVE;
		if (USE_FUSEKI) {
			negFile = RESOURCE_IGUANA_FUSEKI_NEGATIVE;
			posFile = RESOURCE_IGUANA_FUSEKI_POSITIVE;
		}

		List<String> negatives = FileReader.readFileToList(Resources.getResource(negFile).getPath(), true,
				FileReader.UTF8);
		List<String> positives = FileReader.readFileToList(Resources.getResource(posFile).getPath(), true,
				FileReader.UTF8);

		SpabApi spab = new SpabApi();

		int n = NUMBER_OF_NEGATIVES;
		for (String query : negatives) {
			spab.addNegative(query);
			if (--n == 0) {
				break;
			}
		}

		int p = NUMBER_OF_POSITIVES;
		for (String query : positives) {
			spab.addPositive(query);
			if (--p == 0) {
				break;
			}
		}

		System.out.println("Positives:");
		for (SparqlUnit unit : spab.getInput().getPositives()) {
			System.out.println(" " + unit.getLineRepresentation());
		}

		System.out.println();
		System.out.println("Negatives:");
		for (SparqlUnit unit : spab.getInput().getNegatives()) {
			System.out.println(" " + unit.getLineRepresentation());
		}

		System.out.println();
		System.out.println("Lambda: " + LAMBDA);
		System.out.println("Iterations: " + MAX_ITERATIONS);

		spab.setLambda(LAMBDA);
		spab.setMaxIterations(MAX_ITERATIONS);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_SIX);

		CandidateVertex bestCandidate = spab.run();
		System.out.println("Best candidate:");
		System.out.println(bestCandidate.getInfoLine());

		System.out.println();
		int noOfPrintBest = 30;
		List<CandidateVertex> bestCandidates = spab.getBestCandidates();
		noOfPrintBest = Math.min(noOfPrintBest, bestCandidates.size());
		System.out.println("First " + noOfPrintBest + " best candidates: ");
		for (int i = 0; i < noOfPrintBest; i++) {
			System.out.println(bestCandidates.get(i).getInfoLine());
		}

		System.out.println();
		System.out.println("Generated generations:                  " + spab.getGraph().getDepth());
		System.out.println("Number generated candidates (graph):    " + spab.getGraph().getAllCandidates().size());
		System.out.println("Number of remaining candidates (queue): " + spab.getQueue().getQueue().size());
		System.out.println("Number visited candidates (stack):      " + spab.getStack().size());
	}
}