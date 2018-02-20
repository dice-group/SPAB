package org.aksw.spab.exceptions;

import org.aksw.spab.structures.CandidateVertex;

/**
 * Thrown, if a candidate is found, which produces a perfect solution.
 * 
 * @author Adrian Wilke
 */
public class PerfectSolutionException extends Exception {

	protected static final long serialVersionUID = 1L;
	protected CandidateVertex candidate;

	public PerfectSolutionException(CandidateVertex candidate) {
		super();
		this.candidate = candidate;
	}

	/**
	 * Gets candidate, which produces perfect solution.
	 */
	public CandidateVertex getCandidate() {
		return candidate;
	}
}