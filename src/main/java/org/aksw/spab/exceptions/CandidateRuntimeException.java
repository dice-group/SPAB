package org.aksw.spab.exceptions;

import org.aksw.spab.candidates.Candidate;

/**
 * Exception thrown by {@link Candidate} implementations.
 * 
 * @author Adrian Wilke
 */
public class CandidateRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CandidateRuntimeException(String message) {
		super(message);
	}

	public CandidateRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	protected CandidateRuntimeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CandidateRuntimeException(Throwable cause) {
		super(cause);
	}
}