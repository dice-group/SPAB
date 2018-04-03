package org.dice_research.spab.examples;

import java.util.List;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.Resources;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabExample {

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

		for (String query : negatives) {
			spab.addNegative(query);
		}
		for (String query : positives) {
			spab.addPositive(query);
		}

		spab.setLambda(.2f);
		spab.setMaxIterations(1000);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_TWO);

		CandidateVertex bestCandidate = spab.run();

		System.out.println("Final score of best candidate: " + bestCandidate.getScore());
		System.out.println("F-measure of best candidate:   " + bestCandidate.getfMeasure());
		System.out.println("Generation of best candidate: " + bestCandidate.getGeneration());
		System.out.println("Generated generations:        " + spab.getGraph().getDepth());
		System.out.println("Number of remaining candidates in queue: " + spab.getQueue().getQueue().size());
		System.out.print("Next best scores: ");
		int numberOfBestScores = 100;
		while (!spab.getQueue().getQueue().isEmpty() && numberOfBestScores-- > 0) {
			System.out.print(spab.getQueue().getBestCandidate().getScore() + " ");
		}
		System.out.println();
		System.out.println("Number generated candidates: " + spab.getGraph().getAllCandidates().size());
		System.out.println("RegEx of best candidate: " + bestCandidate.getCandidate().getRegEx());
	}
}