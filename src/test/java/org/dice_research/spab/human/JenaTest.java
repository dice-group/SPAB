package org.dice_research.spab.human;

import org.dice_research.spab.input.Input;

public class JenaTest {

	public static final String FILTER1 = "PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
			+ "		PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> \n" + "\n" + "		SELECT ?person\n"
			+ "		WHERE \n" + "		{\n" + "		    ?person rdf:type  foaf:Person .\n"
			+ "		    FILTER NOT EXISTS { ?person foaf:name ?name }\n" + "		}";

	public static final String FILTER2 = "PREFIX  rdf:    <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n" + 
			"PREFIX  foaf:   <http://xmlns.com/foaf/0.1/> \n" + 
			"\n" + 
			"SELECT ?person\n" + 
			"WHERE \n" + 
			"{\n" + 
			"    FILTER NOT EXISTS { ?person foaf:name ?name }\n" + 
			"    ?person rdf:type  foaf:Person .\n" + 
			"}";
	
	public static void main(String[] args) {
		testFilter();
	}
	
	public static void testFilter() {
		Input input = new Input();
		input.addPositive(FILTER1);
		input.addPositive(FILTER2);
		System.out.println(input.getPositives().get(0).getLineRepresentation());
		System.out.println(input.getPositives().get(1).getLineRepresentation());
		// -> Jena does not sort queries and re-arrange filters
	}
}
