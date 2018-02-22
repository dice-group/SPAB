package org.aksw.spab.candidates.one;

import java.util.LinkedList;
import java.util.List;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * First implementation of Candidate, type: SELECT.
 * 
 * @author Adrian Wilke
 */
public class SpabOneSelectCandidate extends SpabOneCandidate {
	
	public SpabOneSelectCandidate(String regex) {
		super(regex);
	}

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {

		// TODO
		return new LinkedList<Candidate>();
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRexEx() throws CandidateRuntimeException {
		if (regex != null && !regex.isEmpty()) {
			return regex;
		} else {
			throw new CandidateRuntimeException("No regular expression set.");
		}
	}

}