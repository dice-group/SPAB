package org.dice_research.spab;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.two.Features;
import org.dice_research.spab.candidates.two.SpabTwoCandidate;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.SparqlUnit;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link SpabTwoCandidate}.
 * 
 * @author Adrian Wilke
 */
public class CandidateSpabTwoTest extends AbstractTestCase {

	public static final boolean EXECUTE_LONG_RUN_TESTS = true;

	static ImportFilesTest importFilesTest = new ImportFilesTest();
	SpabApi spab;

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

	public void printMatches(CandidateVertex bestCandidate, SpabApi spabApi, int maxEntries) {

		List<String> truePositives = new LinkedList<String>();
		List<String> falseNegatives = new LinkedList<String>();
		List<String> trueNegatives = new LinkedList<String>();
		List<String> falsePositives = new LinkedList<String>();

		for (SparqlUnit sparqlUnit : spabApi.getInput().getPositives()) {
			String queryLine = sparqlUnit.getLineRepresentation();
			if (bestCandidate.matches(bestCandidate.getCandidate(), queryLine)) {
				truePositives.add(queryLine);
			} else {
				falseNegatives.add(queryLine);
			}
		}

		for (SparqlUnit sparqlUnit : spabApi.getInput().getNegatives()) {
			String queryLine = sparqlUnit.getLineRepresentation();
			if (bestCandidate.matches(bestCandidate.getCandidate(), queryLine)) {
				falsePositives.add(queryLine);
			} else {
				trueNegatives.add(queryLine);
			}
		}

		int counter;

		counter = 0;
		System.out.println("truePositives " + truePositives.size());
		for (String queryLine : truePositives) {
			if (++counter > maxEntries) {
				break;
			}
			System.out.println(" " + queryLine);
		}
		System.out.println();

		counter = 0;
		System.out.println("falseNegatives " + falseNegatives.size());
		for (String queryLine : falseNegatives) {
			if (++counter > maxEntries) {
				break;
			}
			System.out.println(" " + queryLine);
		}
		System.out.println();

		counter = 0;
		System.out.println("trueNegatives " + trueNegatives.size());
		for (String queryLine : trueNegatives) {
			if (++counter > maxEntries) {
				break;
			}
			System.out.println(" " + queryLine);
		}
		System.out.println();

		counter = 0;
		System.out.println("falsePositives " + falsePositives.size());
		for (String queryLine : falsePositives) {
			if (++counter > maxEntries) {
				break;
			}
			System.out.println(" " + queryLine);
		}
		System.out.println();
	}

	@Override
	@Before
	public void setUp() {
		spab = new SpabApi();
	}

	@Test
	public void test() throws SpabException {

		if (!EXECUTE_LONG_RUN_TESTS) {
			return;
		}

		List<String> selectQueries = ImportFilesTest.getDbpediaSelectQueries();
		for (int i = 0; i < 30; i++) {
			spab.addPositive(selectQueries.get(i));
		}
		for (int i = 30; i < 40; i++) {
			spab.addNegative(selectQueries.get(i));
		}

		List<String> describeQueries = ImportFilesTest.getDbpediaDescribeQueries();
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
		if (PRINT) {
			printMatches(bestCandidate, spab, 5);
		}
	}

	@Test
	public void testGroupBy() throws SpabException {

		if (!EXECUTE_LONG_RUN_TESTS) {
			return;
		}

		for (String query : ImportFilesTest.getDbpediaSelectQueries()) {
			if (query.contains(Features._019_GROUP_CLAUSE) && Math.random() < 0.99
					|| !query.contains(Features._019_GROUP_CLAUSE) && Math.random() < 0.01) {
				spab.addPositive(query);
			} else {
				spab.addNegative(query);
			}
		}
		for (String query : ImportFilesTest.getDbpediaAskQueries()) {
			if (query.contains(Features._019_GROUP_CLAUSE) && Math.random() < 0.99
					|| !query.contains(Features._019_GROUP_CLAUSE) && Math.random() < 0.01) {
				spab.addPositive(query);
			} else {
				spab.addNegative(query);
			}
		}

		CandidateVertex bestCandidate = spab.run();

		if (PRINT) {
			print(bestCandidate, spab);
		}
		if (PRINT) {
			printMatches(bestCandidate, spab, 5);
		}
	}

	@Test
	public void testHaving() throws SpabException {
		// No appropriate test data in dbpedia samples
	}

	@Test
	public void testOrderBy() throws SpabException {

		if (!EXECUTE_LONG_RUN_TESTS) {
			return;
		}

		for (String query : ImportFilesTest.getDbpediaSelectQueries()) {
			if (query.contains(Features._023_ORDER_CLAUSE) && Math.random() < 0.7
					|| !query.contains(Features._023_ORDER_CLAUSE) && Math.random() < 0.2) {
				spab.addPositive(query);
			} else {
				spab.addNegative(query);
			}
		}
		for (String query : ImportFilesTest.getDbpediaAskQueries()) {
			if (query.contains(Features._023_ORDER_CLAUSE) && Math.random() < 0.7
					|| !query.contains(Features._023_ORDER_CLAUSE) && Math.random() < 0.2) {
				spab.addPositive(query);
			} else {
				spab.addNegative(query);
			}
		}

		CandidateVertex bestCandidate = spab.run();

		if (PRINT) {
			print(bestCandidate, spab);
		}
		if (PRINT) {
			printMatches(bestCandidate, spab, 5);
		}
	}
}