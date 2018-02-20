package org.aksw.spab.exceptions;

import org.aksw.spab.candidates.Candidate;

/**
 * Exception thrown by {@link Candidate} implementations.
 * 
 * @author Adrian Wilke
 */
public class CandidateException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CandidateException(String message) {
		super(message);
	}

	public CandidateException(String message, Throwable cause) {
		super(message, cause);
	}

	protected CandidateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CandidateException(Throwable cause) {
		super(cause);
	}
}