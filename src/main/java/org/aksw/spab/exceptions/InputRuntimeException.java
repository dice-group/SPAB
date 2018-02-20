package org.aksw.spab.exceptions;

/**
 * Thrown, if a user input is incorrect.
 * 
 * @author Adrian Wilke
 */
public class InputRuntimeException extends RuntimeException {

	protected static final long serialVersionUID = 1L;

	public InputRuntimeException(String message) {
		super(message);
	}

    public InputRuntimeException(Throwable cause) {
        super(cause);
    }
}