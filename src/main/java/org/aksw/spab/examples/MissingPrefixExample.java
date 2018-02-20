package org.aksw.spab.examples;

import org.aksw.spab.SpabApi;
import org.aksw.spab.exceptions.InputRuntimeException;

/**
 * Example tests missing prefix.
 * 
 * @author Adrian Wilke
 */
public class MissingPrefixExample {

	public static String query = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";

	public static void main(String[] args) {

		// Try a query without needed prefix
		try {
			new SpabApi().addPositive(query);
		} catch (InputRuntimeException e) {
			e.printStackTrace();
			System.out.println("CATCHED!");
		}

		// Try a query after providing the needed prefix
		SpabApi spab = new SpabApi();
		spab.addNamespacePrefix("foaf", "<http://xmlns.com/foaf/0.1/>");
		spab.addPositive(query);
		System.out.println("Done.");
	}
}