package org.dice_research.spab.structures;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;

/**
 * Matchers check, if a candidates regular expression and a query are matching.
 * 
 * @author Adrian Wilke
 */
public interface Matcher {

	/**
	 * Checks, if the candidates regular expression and the query are matching.
	 */
	public boolean matches(Candidate candidate, String query) throws CandidateRuntimeException;

}