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
	 * Test run with 1000 iterations took 85 seconds
	 */
	public static final int MAX_ITERATIONS = 1000;

	public static final String RESOURCE_IGUANA_NEGATIVE = "iguana-2018-01-20/Fuseki-negative.txt";
	public static final String RESOURCE_IGUANA_POSITIVE = "iguana-2018-01-20/Fuseki-positive.txt";

	public static final String RESOURCE_IGUANA_VIRTUOSO_NEGATIVE = "iguana-2018-01-20/Virtuoso-negative.txt";
	public static final String RESOURCE_IGUANA_VIRTUOSO_POSITIVE = "iguana-2018-01-20/Virtuoso-positive.txt";

	public static void main(String[] args) throws SpabException {

		List<String> negatives = FileReader.readFileToList(Resources.getResource(RESOURCE_IGUANA_NEGATIVE).getPath(),
				true, FileReader.UTF8);
		List<String> positives = FileReader.readFileToList(Resources.getResource(RESOURCE_IGUANA_POSITIVE).getPath(),
				true, FileReader.UTF8);

		SpabApi spab = new SpabApi();

		int n = 3;
		for (String query : negatives) {
			spab.addNegative(query);
			if (--n == 0) {
				break;
			}
		}

		int p = 3;
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

		System.out.println("Negatives:");
		for (SparqlUnit unit : spab.getInput().getNegatives()) {
			System.out.println(" " + unit.getLineRepresentation());
		}

		spab.setLambda(.2f);
		spab.setMaxIterations(MAX_ITERATIONS);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_TWO);

		CandidateVertex bestCandidate = spab.run();

		System.out.println("Final score of best candidate: " + bestCandidate.getScore());
		System.out.println("F-measure of best candidate:   " + bestCandidate.getfMeasure());
		System.out.println("RegEx of best candidate:       " + bestCandidate.getCandidate().getRegEx());
		System.out.println("Generation of best candidate:  " + bestCandidate.getGeneration());
		System.out.print("TP " + bestCandidate.getNumberOfTruePositives());
		System.out.print(", TN " + bestCandidate.getNumberOfTrueNegatives());
		System.out.print(", FP " + bestCandidate.getNumberOfFalsePositives());
		System.out.println(", FN " + bestCandidate.getNumberOfFalseNegatives());
		System.out.println("Next best candidates: ");
		int i = 0;
		while (i <= 4) {
			CandidateVertex candidate = spab.getQueue().peekBestCandidate(i);
			System.out.print("S " + candidate.getScore());
			System.out.print(", fM " + candidate.getfMeasure());
			System.out.print(", G " + candidate.getGeneration());
			System.out.print(", TP " + candidate.getNumberOfTruePositives());
			System.out.print(", TN " + candidate.getNumberOfTrueNegatives());
			System.out.print(", FP " + candidate.getNumberOfFalsePositives());
			System.out.print(", FN " + candidate.getNumberOfFalseNegatives());
			System.out.print(", " + candidate.getCandidate().getRegEx());
			System.out.println();
			i++;
		}
		System.out.println("Generated generations:                   " + spab.getGraph().getDepth());
		System.out.println("Number generated candidates:             " + spab.getGraph().getAllCandidates().size());
		System.out.println("Number of remaining candidates in queue: " + spab.getQueue().getQueue().size());
	}
}