package org.dice_research.spab.examples;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.input.SparqlUnit;

/**
 * Example shows different representations of input queries.
 * 
 * @author Adrian Wilke
 */
public class InputQueryExample {

	public static String exampleQuery = "PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
			+ "PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> \n" + "\n" + "SELECT ?person\n" + "WHERE \n" + "{\n"
			+ "    ?person rdf:type  foaf:Person .\n" + "    FILTER NOT EXISTS { ?person foaf:name ?name }\n" + "}   ";

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
		System.out.println(sparqlUnit.getJenaStringRepresentation());
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