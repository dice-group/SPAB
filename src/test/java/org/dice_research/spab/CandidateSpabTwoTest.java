package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.two.SpabTwoCandidate;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Test;

/**
 * Tests for {@link SpabTwoCandidate}.
 * 
 * @author Adrian Wilke
 */
public class CandidateSpabTwoTest extends AbstractTestCase {

	@Test
	public void test() throws SpabException {

		SpabApi spab = new SpabApi();
		ImportFilesTest importFilesTest = new ImportFilesTest();

		List<String> selectQueries = importFilesTest.getDbpediaSelectQueries();
		for (int i = 0; i < 30; i++) {
			spab.addPositive(selectQueries.get(i));
		}
		for (int i = 30; i < 40; i++) {
			spab.addNegative(selectQueries.get(i));
		}

		List<String> describeQueries = importFilesTest.getDbpediaDescribeQueries();
		for (int i = 0; i < 20; i++) {
			spab.addNegative(describeQueries.get(i));
		}
		for (int i = 20; i < 25; i++) {
			spab.addPositive(describeQueries.get(i));
		}

		spab.setLambda(.5f);
		spab.setMaxIterations(40);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_TWO);

		CandidateVertex bestCandidate = spab.run();
		assertTrue(bestCandidate.getScore() >= 0);

		// At least the root should be in graph
		assertTrue(spab.getGraph().getAllCandidates().size() > 0);

		// If a human is interested in the results
		if (PRINT) {
			print(bestCandidate, spab);
		}
	}

	public void print(CandidateVertex bestCandidate, SpabApi spabApi) {
		System.out.println("Final score of best candidate: " + bestCandidate.getScore());
		System.out.println("F-measure of best candidate:   " + bestCandidate.getfMeasure());
		System.out.println("Generation of best candidate: " + bestCandidate.getGeneration());
		System.out.println("Generated generations:        " + spabApi.getGraph().getDepth());
		System.out.println("Number of remaining candidates in queue: " + spabApi.getQueue().getQueue().size());
		System.out.print("Next best scores: ");
		while (!spabApi.getQueue().getQueue().isEmpty()) {
			System.out.print(spabApi.getQueue().getBestCandidate().getScore() + " ");
		}
		System.out.println();
		System.out.println("Number generated candidates: " + spabApi.getGraph().getAllCandidates().size());
		System.out.println("RegEx of best candidate: " + bestCandidate.getCandidate().getRegEx());
	}
}