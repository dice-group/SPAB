package org.dice_research.spab.exceptions;

/**
 * Thrown, on errors in unit tests.
 * 
 * @author Adrian Wilke
 */
public class UnitTestRuntimeException extends RuntimeException {

	protected static final long serialVersionUID = 1L;

	public UnitTestRuntimeException() {
		super();
	}

	public UnitTestRuntimeException(String message) {
		super(message);
	}

	public UnitTestRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnitTestRuntimeException(Throwable cause) {
		super(cause);
	}
}