package org.dice_research.spab.candidates;

import org.dice_research.spab.Matcher;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.one.SpabOneRootCandidate;
import org.dice_research.spab.exceptions.SpabException;

/**
 * Returns root instance of respective {@link Candidate} implementation.
 * 
 * @author Adrian Wilke
 */
public abstract class CandidateFactory {

	public static Candidate createCandidate(CandidateImplementation candidateImplementation, Matcher matcher)
			throws SpabException {
		switch (candidateImplementation) {

		case SPAB_ONE:
			// The default implementation
			return new SpabOneRootCandidate();

		case UNIT_TEST:
			// Unit tests of candidates also are matching
			if (matcher instanceof Candidate) {
				return (Candidate) matcher;
			} else {
				throw new SpabException("Test not found.");
			}

		default:
			throw new SpabException("No candidate implementation found.");
		}
	}

}