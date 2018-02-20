package org.aksw.spab.exceptions;

/**
 * Thrown, if a user input is incorrect.
 * 
 * @author Adrian Wilke
 */
public class UserInputException extends RuntimeException {

	protected static final long serialVersionUID = 1L;

	public UserInputException(String message) {
		super(message);
	}

    public UserInputException(Throwable cause) {
        super(cause);
    }
}