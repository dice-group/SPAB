package org.aksw.spab.examples;

import org.aksw.spab.SpabApi;
import org.aksw.spab.exceptions.InputRuntimeException;

/**
 * Example tests incorrect input.
 * 
 * @author Adrian Wilke
 */
public class IncorrectInputExample {

	public static String incorrectQuery = "SELECT ?nonsense WHERE {?nonsense a}";
	public static String correctQuery = "SELECT ?correct WHERE {?correct a ?workingQuery}";

	public static void main(String[] args) {

		// Try an incorrect query
		try {
			new SpabApi().addPositive(incorrectQuery);
		} catch (InputRuntimeException e) {
			e.printStackTrace();
			System.out.println("CATCHED!");
		}

		// Try a correct query
		new SpabApi().addPositive(correctQuery);
		System.out.println("Done.");
	}
}