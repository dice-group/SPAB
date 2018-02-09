package org.aksw.spab.exceptions;

import org.aksw.spab.Candidate;

/**
 * Thrown, if a candidate is found, which produces a perfect solution.
 * 
 * @author Adrian Wilke
 */
public class PerfectSolutionException extends Exception {

	protected static final long serialVersionUID = 1L;
	protected Candidate candidate;

	public PerfectSolutionException(Candidate candidate) {
		super();
		this.candidate = candidate;
	}

	/**
	 * Gets candidate, which produces perfect solution.
	 */
	public Candidate getCandidate() {
		return candidate;
	}
}