package org.aksw.spab.exceptions;

/**
 * Thrown, if data could not be parsed.
 * 
 * @author Adrian Wilke
 */
public class ParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParseException(String message) {
		super(message);
	}
}