package org.dice_research.spab;

import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.one.SpabOneCandidate;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Test;

/**
 * Tests for {@link SpabOneCandidate}.
 * 
 * @author Adrian Wilke
 */
public class CandidateSpabOneTest extends AbstractTestCase {

	public static String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT ?x ?name\n"
			+ "WHERE  { ?x foaf:name ?name }";

	@Test
	public void test() throws SpabException {

		SpabApi spab = new SpabApi();

		spab.addPositive(query);
		spab.addPositive(query);
		spab.addNegative(query);

		spab.setLambda(.5f);
		spab.setMaxIterations(30);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_ONE);

		CandidateVertex bestCandidate = spab.run();
		assertTrue(bestCandidate.getScore() >= 0);

		// At least the root should be in graph
		assertTrue(spab.getGraph().getAllCandidates().size() > 0);
	}
}