package org.aksw.spab.examples;

import org.aksw.spab.Spab;
import org.aksw.spab.input.InputQuery;

/**
 * Example shows different representations of input queries.
 * 
 * @author Adrian Wilke
 */
public class InputQueryExample {

	public static String exampleQuery = "SELECT ?person\n" + "WHERE {\n" + " ?person a rdf:Person .\n"
			+ " ?person rdf:age ?age .\n" + " FILTER (?age > 18) .\n" + "}";

	public static void main(String[] args) {

		Spab spab = new Spab();
		spab.addPositive(exampleQuery);
		InputQuery inputQuery = spab.getInput().getPositives().get(0);

		System.out.println("Original query string:");
		System.out.println();
		System.out.println(exampleQuery);
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Parsed query:");
		System.out.println();
		System.out.println(inputQuery.getQuery());
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();
	}
}