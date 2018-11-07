package org.dice_research.spab.active;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.dice_research.spab.AbstractTestCase;
import org.dice_research.spab.Matcher;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Test;

/**
 * General candidate test, using random solutions for {@link #getChildren()} and
 * {@link #matches(Candidate, String)}
 * 
 * @author Adrian Wilke
 */
public class CandidateGeneralTest extends AbstractTestCase implements Candidate<Object>, Matcher {

	final public static int CHILDREN_MAX = 3;
	final public static int CHILDREN_MIN = 1;
	final public static double MATCHING_PROBABILITY = .5;

	public boolean matcherUsed = false;

	/**
	 * Generates dummy children between {@link CHILDREN_MIN} and
	 * {@link CHILDREN_MAX}
	 */
	public List<Candidate<Object>> getChildren(Input input) throws CandidateRuntimeException {
		try {

			List<Candidate<Object>> list = new LinkedList<Candidate<Object>>();
			int numberOfCandidates = ThreadLocalRandom.current().nextInt(CHILDREN_MIN, CHILDREN_MAX + 1);
			for (int i = 0; i < numberOfCandidates; i++) {
				Candidate<Object> candidate = new CandidateGeneralTest();
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

			return CandidateGeneralTest.class.getName();

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}

	/**
	 * Checks, if the candidates regular expression and the query are matching.
	 */
	public boolean matches(Candidate<?> candidate, String query) throws CandidateRuntimeException {

		matcherUsed = true;

		if (Math.random() < MATCHING_PROBABILITY) {
			return true;
		} else {
			return false;
		}
	}

	public void setVertex(CandidateVertex candidateVertex) {
		new CandidateGeneralTest();
	}

	@Test
	public void test() throws SpabException {
		SpabApi spab = new SpabApi();

		List<String> selectQueries = ImportFilesTest.getDbpediaSelectQueries();
		int numberOfPositives = 60;
		int numberOfNegatives = 40;

		for (int i = 0; i < numberOfPositives; i++) {
			try {
				spab.addPositive(selectQueries.get(i));
			} catch (RuntimeException e) {
				System.out.println(i);
				throw e;
			}
		}
		for (int i = numberOfPositives; i < numberOfPositives + numberOfNegatives; i++) {
			spab.addPositive(selectQueries.get(i));
		}

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
			printResult(bestCandidate, spab, "", PRINT);
		}
	}

	@Override
	public Object getInternalRepresentation(Class<Object> internalRepresentationClass)
			throws CandidateRuntimeException {
		return new Object();
	}
}