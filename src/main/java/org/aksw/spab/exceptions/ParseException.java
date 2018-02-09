package org.aksw.spab.exceptions;

/**
 * Thrown, if data could not be parsed.
 * 
 * @author Adrian Wilke
 */
public class ParseException extends Exception {

	protected static final long serialVersionUID = 1L;

	public ParseException(String message) {
		super(message);
	}

	public ParseException(Throwable cause) {
		super(cause);
	}
}