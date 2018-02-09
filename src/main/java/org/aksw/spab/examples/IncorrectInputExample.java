package org.aksw.spab.examples;

import org.aksw.spab.Spab;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class IncorrectInputExample {

	public static final boolean TEST_NONSENSE = true;

	public static void main(String[] args) throws Exception {
		IncorrectInputExample example = new IncorrectInputExample();

		if (TEST_NONSENSE) {
			example.testNonsense();
		}
	}

	public String query = "SELECT ?nonsense WHERE {?nonsense a}";

	public void testNonsense() throws Exception {

		new Spab().addPositive(query);

	}
}