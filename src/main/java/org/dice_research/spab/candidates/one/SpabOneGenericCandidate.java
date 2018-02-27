package org.dice_research.spab.candidates.one;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;

/**
 * Generic implementation for SPARQL-queries CONSTRUCT, DESCRIBE, and ASK
 * without generating children.
 * 
 * @author Adrian Wilke
 */
public class SpabOneGenericCandidate extends SpabOneCandidate {

	public SpabOneGenericCandidate(String regex) {
		super(regex);
	}

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {

		// No children for generic types
		return new LinkedList<Candidate>();
	}

	/**
	 * Returns regular expression got from constructor.
	 */
	public String getRegEx() throws CandidateRuntimeException {
		if (regex != null && !regex.isEmpty()) {
			return regex;
		} else {
			throw new CandidateRuntimeException("No regular expression set.");
		}
	}
}