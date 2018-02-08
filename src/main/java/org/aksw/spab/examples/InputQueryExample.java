package org.aksw.spab.examples;

import org.aksw.spab.Spab;
import org.aksw.spab.input.InputQuery;

/**
 * Example shows different representations of input queries.
 * 
 * @author Adrian Wilke
 */
public class InputQueryExample {

	public static final boolean TEST_INPUT_QUERY = true;

	public String exampleQuery = "SELECT ?person\n" + "WHERE {\n" + " ?person a rdf:Person .\n"
			+ " ?person rdf:age ?age .\n" + " FILTER (?age > 18) .\n" + "}";

	public static void main(String[] args) throws Exception {
		InputQueryExample example = new InputQueryExample();

		// Input test
		if (TEST_INPUT_QUERY) {
			example.testInputQuery();
		}
	}

	public void testInputQuery() throws Exception {

		Spab spab = new Spab();
		spab.addPositive(exampleQuery);
		InputQuery inputQuery = spab.getInput().getPositives().get(0);

		System.out.println("Example query:");
		System.out.println();
		System.out.println(exampleQuery);
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Parsed query without prefixes:");
		System.out.println();
		System.out.println(inputQuery.getQueryWithoutPrefixes());
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Parsed query:");
		System.out.println();
		System.out.println(inputQuery.getQuery());
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("SPIN representation:");
		System.out.println();
		System.out.println(inputQuery.getSpin());
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();

		System.out.println("Graph representation:");
		System.out.println();
		System.out.println(inputQuery.getGraph());
		System.out.println();
		System.out.println("------------------------------------------------------------------------------");
		System.out.println();
	}
}