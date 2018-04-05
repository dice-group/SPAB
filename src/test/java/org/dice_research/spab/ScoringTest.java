package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.exceptions.PerfectSolutionException;
import org.dice_research.spab.input.Configuration;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;
import org.junit.Test;

/**
 * Test calculation of fMeasure
 * 
 * @author Adrian Wilke
 */
public class ScoringTest extends AbstractTestCase implements Candidate, Matcher {

	public final static String POSITIVE_MATCHING_SUBSTRING = "positive";

	@Test
	public void test() throws PerfectSolutionException {

		// Tested values
		// https://www.dcode.fr/precision-recall
		// a, b, c, d
		// a, b, c, x, y, z
		// P (3/6) = 0.5 = 50%
		// R (3/4) = 0.75 = 75%
		// F1 2*P*R/(P+R) = 0.6

		// Input and manual interpretation

		Input input = new Input();
		int tp = 3;
		input.addPositive("SELECT * WHERE { ?s ?p '111" + POSITIVE_MATCHING_SUBSTRING + "' }");
		input.addPositive("SELECT * WHERE { ?s ?p '222" + POSITIVE_MATCHING_SUBSTRING + "' }");
		input.addPositive("SELECT * WHERE { ?s ?p '333" + POSITIVE_MATCHING_SUBSTRING + "' }");
		int fn = 1;
		input.addPositive("SELECT * WHERE { ?s ?p '444' }");
		int fp = 3;
		input.addNegative("SELECT * WHERE { ?s ?p '555" + POSITIVE_MATCHING_SUBSTRING + "' }");
		input.addNegative("SELECT * WHERE { ?s ?p '666" + POSITIVE_MATCHING_SUBSTRING + "' }");
		input.addNegative("SELECT * WHERE { ?s ?p '777" + POSITIVE_MATCHING_SUBSTRING + "' }");
		int tn = 0;

		/// Manual computation

		float p = 1f * tp / (tp + fp);
		float r = 1f * tp / (tp + fn);
		float fm = 2 * p * r / (p + r);

		assertTrue(p == 0.5f);
		assertTrue(r == 0.75f);
		assertTrue(fm == 0.6f);

		// SPAB

		CandidateVertex candidateVertex = new CandidateVertex(this, input);
		candidateVertex.calculateScore(new Configuration(), 0, this);
		if (PRINT) {
			System.out.println("TP " + candidateVertex.getNumberOfTruePositives());
			System.out.println("TN " + candidateVertex.getNumberOfTrueNegatives());
			System.out.println("FP " + candidateVertex.getNumberOfFalsePositives());
			System.out.println("FN " + candidateVertex.getNumberOfFalseNegatives());
			System.out.println("fM " + candidateVertex.getfMeasure());
			System.out.println("S  " + candidateVertex.getScore());
			System.out.println("P  " + p);
			System.out.println("R  " + r);
			System.out.println("FM " + fm);
		}

		assertTrue(tp == candidateVertex.getNumberOfTruePositives());
		assertTrue(tn == candidateVertex.getNumberOfTrueNegatives());
		assertTrue(fp == candidateVertex.getNumberOfFalsePositives());
		assertTrue(fn == candidateVertex.getNumberOfFalseNegatives());
		assertEquals(fm, candidateVertex.getfMeasure());
	}

	@Override
	public boolean matches(Candidate candidate, String query) throws CandidateRuntimeException {
		if (query.contains(POSITIVE_MATCHING_SUBSTRING)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Candidate> getChildren(Input input) throws CandidateRuntimeException {
		return null;
	}

	@Override
	public String getRegEx() throws CandidateRuntimeException {
		return null;
	}

	@Override
	public void setVertex(CandidateVertex candidateVertex) {
	}
}