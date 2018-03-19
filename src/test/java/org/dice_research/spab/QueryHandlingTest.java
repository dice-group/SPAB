package org.dice_research.spab;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/**
 * Test correct rewriting of SPARQL queries.
 * 
 * @author Adrian Wilke
 */
public class QueryHandlingTest extends AbstractTestCase {

	public static final String CONSTRUCT = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> \n" + "CONSTRUCT { ?film rdf:tipo rdf:pelicula } \n"
			+ "WHERE { ?film skos:subject <http://dbpedia.org/resource/Category:French_films> }\n";
	public static final String DESCRIBE = "PREFIX owl: <http://www.w3.org/2002/07/owl#> \n"
			+ "DESCRIBE owl:DatatypeProperty";

	public static final String QUERY0 = "SELECT ?subject ?object WHERE {?subject ?predicate ?object}";
	public static final String QUERY1 = "SELECT ?object ?subject WHERE {?subject ?predicate ?object}";

	public static final String SELECT = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o }";
	public static final String SELECT2 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o . ?o dbpedia-owl:pubchem ?s }";
	public static final String SELECT3 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o . ?o dbpedia-owl:Person ?s }";

	@Test
	public void testResourceHandling() {
		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(SELECT3);
		spabApi.addNegative(DESCRIBE);
		spabApi.addNegative(SELECT);
		assertTrue(spabApi.getInput().getResources()
				.size() == spabApi.getInput().getPositives().get(0).getResources().size()
						+ spabApi.getInput().getNegatives().get(0).getResources().size());
	}

	@Test
	public void testPrefixReplacement() {

		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(SELECT);
		spabApi.addPositive(SELECT2);
		spabApi.addPositive(SELECT3);
		spabApi.addPositive(DESCRIBE);
		spabApi.addPositive(CONSTRUCT);

		if (PRINT) {
			System.out.println(spabApi.getInput().getPositives().get(0).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(0).getLineRepresentation());

			System.out.println();
			System.out.println(spabApi.getInput().getPositives().get(1).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(1).getLineRepresentation());

			System.out.println();
			System.out.println(spabApi.getInput().getPositives().get(2).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(2).getLineRepresentation());

			System.out.println();
			System.out.println(spabApi.getInput().getPositives().get(3).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(3).getLineRepresentation());

			System.out.println();
			System.out.println(spabApi.getInput().getPositives().get(4).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(4).getLineRepresentation());
		}

		assertTrue(spabApi.getInput().getPositives().get(0).getLineRepresentation()
				.contains("<http://dbpedia.org/ontology/pubchem>"));

		assertTrue(spabApi.getInput().getPositives().get(1).getLineRepresentation()
				.split("http://dbpedia.org/ontology/pubchem").length == 3);

		assertTrue(spabApi.getInput().getPositives().get(2).getLineRepresentation()
				.contains("<http://dbpedia.org/ontology/pubchem>"));
		assertTrue(spabApi.getInput().getPositives().get(2).getLineRepresentation()
				.contains("<http://dbpedia.org/ontology/Person>"));

		assertTrue(spabApi.getInput().getPositives().get(3).getLineRepresentation()
				.contains("<http://www.w3.org/2002/07/owl#DatatypeProperty>"));

		assertTrue(spabApi.getInput().getPositives().get(4).getLineRepresentation()
				.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#tipo>"));
		assertTrue(spabApi.getInput().getPositives().get(4).getLineRepresentation()
				.contains("<http://www.w3.org/1999/02/22-rdf-syntax-ns#pelicula>"));
		assertTrue(spabApi.getInput().getPositives().get(4).getLineRepresentation()
				.contains("<http://www.w3.org/2004/02/skos/core#subject>"));
		assertTrue(spabApi.getInput().getPositives().get(4).getLineRepresentation()
				.contains("<http://dbpedia.org/resource/Category:French_films>"));
	}

	@Test
	public void testResourceExtraction() {

		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(SELECT);
		spabApi.addPositive(SELECT2);
		spabApi.addPositive(SELECT3);
		spabApi.addPositive(DESCRIBE);
		Set<String> selectResources;

		selectResources = spabApi.getInput().getPositives().get(0).getResources();
		assertTrue(selectResources.size() == 1);
		assertTrue(selectResources.contains("http://dbpedia.org/ontology/pubchem"));

		selectResources = spabApi.getInput().getPositives().get(1).getResources();
		assertTrue(selectResources.size() == 1);
		assertTrue(selectResources.contains("http://dbpedia.org/ontology/pubchem"));

		selectResources = spabApi.getInput().getPositives().get(2).getResources();
		assertTrue(selectResources.size() == 2);
		assertTrue(selectResources.contains("http://dbpedia.org/ontology/pubchem"));
		assertTrue(selectResources.contains("http://dbpedia.org/ontology/Person"));

		selectResources = spabApi.getInput().getPositives().get(3).getResources();
		assertTrue(selectResources.size() == 1);
		assertTrue(selectResources.contains("http://www.w3.org/2002/07/owl#DatatypeProperty"));

	}

	@Test
	public void testVariableReplacement() {

		// Put queries in list to navigate by indexes
		List<String> queries = new LinkedList<String>();
		queries.add(QUERY0);
		queries.add(QUERY1);

		// Put queries in SPAB
		SpabApi spabApi = new SpabApi();
		for (String query : queries) {
			spabApi.addPositive(query);
		}

		// WHERE clause should be the same
		String where0 = spabApi.getInput().getPositives().get(0).getLineRepresentation();
		where0 = where0.substring(where0.indexOf("WHERE"));
		String where1 = spabApi.getInput().getPositives().get(1).getLineRepresentation();
		where1 = where1.substring(where1.indexOf("WHERE"));
		assertTrue(where0.equals(where1));

		// Print result
		if (PRINT) {
			for (int i = 0; i < queries.size(); i++) {
				System.out.println("Input: " + queries.get(i));
				System.out.println("Used:  " + spabApi.getInput().getPositives().get(i));
				System.out.println();
			}
		}
	}
}