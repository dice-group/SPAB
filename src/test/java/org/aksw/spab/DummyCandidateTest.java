package org.aksw.spab;

import org.aksw.spab.SpabApi.CandidateImplementation;
import org.aksw.spab.candidates.DummyCandidate;
import org.aksw.spab.exceptions.SpabException;
import org.aksw.spab.structures.CandidateVertex;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Tests for {@link DummyCandidate}.
 * 
 * @author Adrian Wilke
 */
public class DummyCandidateTest extends TestCase {

	public static String query = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";

	@Test
	public void test() throws SpabException {

		SpabApi spab = new SpabApi();

		spab.addNamespacePrefix("foaf", "<http://xmlns.com/foaf/0.1/>");

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
		spab.setCandidateImplementation(CandidateImplementation.DUMMY);

		CandidateVertex bestCandidate = spab.run();
		assertTrue(bestCandidate.getScore() >= 0);

		// At least the root should be in graph
		assertTrue(spab.getGraph().getAllCandidates().size() > 0);
	}
}