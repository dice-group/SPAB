package org.dice_research.spab.exceptions;

/**
 * Thrown, on input/output operations, e.g. on reading files.
 * 
 * @author Adrian Wilke
 */
public class IoRuntimeException extends RuntimeException {

	protected static final long serialVersionUID = 1L;

	public IoRuntimeException() {
		super();
	}

	public IoRuntimeException(String message) {
		super(message);
	}

	public IoRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public IoRuntimeException(Throwable cause) {
		super(cause);
	}
}