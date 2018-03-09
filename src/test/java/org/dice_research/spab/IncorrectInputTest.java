package org.dice_research.spab;

import org.dice_research.spab.exceptions.InputRuntimeException;
import org.junit.Test;

/**
 * Tests for exception handling of incorrect inputs of SPARQL queries.
 * 
 * @author Adrian Wilke
 */
public class IncorrectInputTest extends AbstractTestCase {

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
			fail("Correct input should not throw Exception.");
		}
	}
}