package org.aksw.spab.examples;

import org.aksw.spab.Spab;

/**
 * Example tests incorrect input.
 * 
 * @author Adrian Wilke
 */
public class IncorrectInputExample {

	public static String query = "SELECT ?nonsense WHERE {?nonsense a}";

	public static void main(String[] args) throws Exception {
		new Spab().addPositive(query);
	}
}