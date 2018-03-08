package org.dice_research.spab;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Test;

public class DummyTest extends SpabTestCase implements Candidate, Matcher {

	final public static int CHILDREN_MAX = 3;
	final public static int CHILDREN_MIN = 1;
	final public static double MATCHING_PROBABILITY = .5;

	public static String query = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";

	public boolean matcherUsed = false;

	/**
	 * Generates dummy children between {@link CHILDREN_MIN} and
	 * {@link CHILDREN_MAX}
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {
		try {

			List<Candidate> list = new LinkedList<Candidate>();
			int numberOfCandidates = ThreadLocalRandom.current().nextInt(CHILDREN_MIN, CHILDREN_MAX + 1);
			for (int i = 0; i < numberOfCandidates; i++) {
				Candidate candidate = new DummyTest();
				list.add(candidate);
			}
			return list;

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}

	/**
	 * Returns dummy regex.
	 */
	public String getRegEx() throws CandidateRuntimeException {
		try {

			return DummyTest.class.getName();

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}

	/**
	 * Checks, if the candidates regular expression and the query are matching.
	 */
	public boolean matches(Candidate candidate, String query) throws CandidateRuntimeException {

		matcherUsed = true;

		if (Math.random() < MATCHING_PROBABILITY) {
			return true;
		} else {
			return false;
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

	public void setVertex(CandidateVertex candidateVertex) {
		new DummyTest();
	}

	@Test
	public void test() throws SpabException {
		SpabApi spab = new SpabApi();

		spab.addNamespacePrefix("foaf", "http://xmlns.com/foaf/0.1/");

		spab.addPositive(query);
		spab.addPositive(query);
		spab.addPositive(query);
		spab.addPositive(query);
		spab.addPositive(query);

		spab.addNegative(query);
		spab.addNegative(query);
		spab.addNegative(query);
		spab.addNegative(query);
		spab.addNegative(query);

		spab.setLambda(.5f);
		spab.setMaxIterations(30);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.UNIT_TEST);

		CandidateVertex bestCandidate;
		bestCandidate = spab.run(this);

		// The matcher of this class has to be used
		assertTrue(matcherUsed);

		// The regular expression of this class has to be used
		assertTrue(bestCandidate.getCandidate().getRegEx().equals(this.getRegEx()));

		// At least the root should be in graph
		assertTrue(spab.getGraph().getAllCandidates().size() > 0);

		// A negative score should not be possible
		assertTrue(bestCandidate.getScore() >= 0);

		// If a human is interested in the results
		if (PRINT) {
			print(bestCandidate, spab);
		}
	}
}