package org.dice_research.spab;

import org.dice_research.spab.input.Input;

/**
 * Utilities for manual tests.
 * 
 * @author Adrian Wilke
 */
public abstract class Utils {

	public static String getStringRepresentation(String sparqlQuery) {
		Input input = new Input();
		input.addPositive(sparqlQuery);
		return input.getPositives().get(0).getLineRepresentation();
	}

	public static String getJenaStringRepresentation(String sparqlQuery) {
		Input input = new Input();
		input.addPositive(sparqlQuery);
		return input.getPositives().get(0).getJenaStringRepresentation();
	}

	public static void main(String[] args) {
		System.out.println("Input                    " + args[0]);
		System.out.println("JenaStringRepresentation " + getJenaStringRepresentation(args[0]));
		System.out.println("StringRepresentation     " + getStringRepresentation(args[0]));
	}
}