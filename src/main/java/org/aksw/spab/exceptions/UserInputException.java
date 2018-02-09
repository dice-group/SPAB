package org.aksw.spab.exceptions;

/**
 * Thrown, if a user input is incorrect.
 * 
 * @author Adrian Wilke
 */
public class UserInputException extends Exception {

	protected static final long serialVersionUID = 1L;

	public UserInputException(String message) {
		super(message);
	}
}