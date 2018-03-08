package org.dice_research.spab.examples;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.input.SparqlUnit;

/**
 * Example shows different representations of input queries.
 * 
 * @author Adrian Wilke
 */
public class InputQueryExample {

	public static String exampleQuery = "SELECT ?person\n" + "WHERE {\n" + " ?person a rdf:Person .\n"
			+ " ?person rdf:age ?age .\n" + " FILTER (?age > 18) .\n" + "}";

	public static void main(String[] args) {

		SpabApi spab = new SpabApi();
		spab.addPositive(exampleQuery);
		SparqlUnit sparqlUnit = spab.getInput().getPositives().get(0);

		System.out.println("Original query string:");
		System.out.println();
		System.out.println(exampleQuery);
		System.out.println();
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Parsed query representation:");
		System.out.println();
		System.out.println(sparqlUnit.getStringRepresentation());
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Resulting string representation:");
		System.out.println();
		System.out.println(sparqlUnit.getLineRepresentation());
		System.out.println();
		System.out.println("----------------------------------------------------------------------------------------");
		System.out.println();
	}
}