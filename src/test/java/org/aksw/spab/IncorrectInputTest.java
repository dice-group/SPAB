package org.aksw.spab;

import org.aksw.spab.exceptions.InputRuntimeException;
import org.junit.Test;

import junit.framework.TestCase;

public class IncorrectInputTest extends TestCase {

	public static String incorrectQuery = "SELECT ?nonsense WHERE {?nonsense a}";
	public static String correctQuery = "SELECT ?correct WHERE {?correct a ?workingQuery}";

	@Test
	public void testIncorrectQueryInput() {
		try {
			new SpabApi().addPositive(incorrectQuery);
			fail("Incorrect input should throw Exception.");
		} catch (InputRuntimeException e) {
		}
	}

	@Test
	public void testCorrectQueryInput() {
		try {
			new SpabApi().addPositive(correctQuery);
		} catch (InputRuntimeException e) {
			fail("Incorrect input should not throw Exception.");
		}
	}
}