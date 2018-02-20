package org.aksw.spab.examples;

import org.aksw.spab.Spab;
import org.aksw.spab.exceptions.UserInputException;

/**
 * Example tests incorrect input.
 * 
 * @author Adrian Wilke
 */
public class IncorrectInputExample {

	public static String query = "SELECT ?nonsense WHERE {?nonsense a}";

	public static void main(String[] args) {
		try {
			new Spab().addPositive(query);
		} catch (UserInputException e) {
			e.printStackTrace();
			System.out.println("CATCHED!");
		}
	}
}