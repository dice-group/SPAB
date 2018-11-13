package org.dice_research.spab.benchmark;

public class BenchmarkNullException extends Exception {

	private static final long serialVersionUID = 1L;

	public BenchmarkNullException() {
		super();
	}

	public BenchmarkNullException(String message) {
		super(message);
	}

	public BenchmarkNullException(String message, Throwable cause) {
		super(message, cause);
	}

	public BenchmarkNullException(Throwable cause) {
		super(cause);
	}
}