package org.dice_research.spab.candidates;

import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.one.SpabOneRootCandidate;
import org.dice_research.spab.exceptions.SpabException;

/**
 * Returns root instance of respective {@link Candidate} implementation.
 * 
 * @author Adrian Wilke
 */
public abstract class CandidateFactory {

	private static Candidate UNIT_TEST_CANDIDATE = null;

	public static void setUnitTestCandidate(Candidate candidate) throws SpabException {

		// The test candidate should only set once to prevent using wrong implementation
		if (UNIT_TEST_CANDIDATE == null) {
			UNIT_TEST_CANDIDATE = candidate;
		} else {
			throw new SpabException("Test candidate already set.");
		}
	}

	public static Candidate createCandidate(CandidateImplementation candidateImplementation) throws SpabException {
		switch (candidateImplementation) {
		case SPAB_ONE:
			return new SpabOneRootCandidate();
		case UNIT_TEST:
			return UNIT_TEST_CANDIDATE;
		default:
			throw new SpabException("No candidate implementation set.");
		}
	}
}