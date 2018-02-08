package org.aksw.spab.exceptions;

/**
 * Generic exception. Thrown on errors in SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabException extends Exception {

	private static final long serialVersionUID = 1L;

	public SpabException(String message) {
		super(message);
	}
}