package org.dice_research.spab.candidates;

import org.dice_research.spab.Matcher;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.six.CandidateSix;
import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.exceptions.SpabException;

/**
 * Returns root instance of respective {@link Candidate} implementation.
 * 
 * New implementations of candidates must be added to the enumeration
 * CandidateImplementation in {@link SpabApi} and to {@link CandidateFactory}.
 * 
 * @author Adrian Wilke
 */
public abstract class CandidateFactory {

	public static Candidate<?> createCandidate(CandidateImplementation candidateImplementation, Matcher matcher)
			throws SpabException {
		switch (candidateImplementation) {

		case SPAB_SIX:
			return new CandidateSix<Expression>();

		case UNIT_TEST:
			// Unit tests of candidates also are matchers
			if (matcher instanceof Candidate) {
				return (Candidate<?>) matcher;
			} else {
				throw new SpabException("Test not found.");
			}

		default:
			throw new SpabException("No candidate implementation found.");
		}
	}

}