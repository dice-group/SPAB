package org.dice_research.spab;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dice_research.spab.input.SparqlUnit;
import org.junit.Test;

/**
 * Test correct rewriting of SPARQL queries.
 * 
 * @author Adrian Wilke
 */
public class QueryReplacementsTest extends AbstractTestCase {

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

	public static final String EMPTY_PREFIX = "PREFIX : <http://dbpedia.org/resource/> "
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> " + "PREFIX dbpedia2: <http://dbpedia.org/property/> "
			+ "PREFIX dbpedia3: <http://dbpedia.org/resource/> " + "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " + "PREFIX yago: <http://dbpedia.org/class/yago/> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
			+ "PREFIX category: <http://dbpedia.org/resource/Category:> "
			+ "PREFIX dbo: <http://dbpedia.org/property/> " + "PREFIX dbpedia: <http://dbpedia.org/> "
			+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX dbpprop: <http://dbpedia.org/property/> " + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
			+ "SELECT * WHERE { ?x foaf:name ?name . ?x rdf:type dbpedia-owl:Artist . "
			+ "?x dbpedia-owl:nationality :Spain . ?x dbpedia-owl:birthDate ?nacimiento }";

	public static final String IGUANA_FUSEKI = "PREFIX  dc:   <http://purl.org/dc/elements/1.1/>  "
			+ "PREFIX  :     <http://dbpedia.org/resource/>  PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>  "
			+ "PREFIX  dbpedia2: <http://dbpedia.org/property/>  PREFIX  foaf: <http://xmlns.com/foaf/0.1/>  "
			+ "PREFIX  owl:  <http://www.w3.org/2002/07/owl#>  PREFIX  xsd:  <http://www.w3.org/2001/XMLSchema#>  "
			+ "PREFIX  dbpedia: <http://dbpedia.org/>  PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>  "
			+ "PREFIX  skos: <http://www.w3.org/2004/02/skos/core#>   "
			+ "SELECT  *  WHERE    { ?data rdf:type <http://dbpedia.org/ontology/FormulaOneRacer> .      ?wins <http://dbpedia.org/ontology/wins> 10    }";

	/**
	 * Tests, if prefixes, including the empty prefix, are replaced correctly.
	 */
	@Test
	public void testEmptyPrefix() {
		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(EMPTY_PREFIX);
		SparqlUnit sparqlUnit = spabApi.getInput().getPositives().get(0);

		Set<String> resources = sparqlUnit.getResources();
		assertTrue(resources.contains("http://xmlns.com/foaf/0.1/name"));
		assertTrue(resources.contains("http://dbpedia.org/ontology/Artist"));
		assertTrue(resources.contains("http://dbpedia.org/ontology/nationality"));
		assertTrue(resources.contains("http://dbpedia.org/resource/Spain"));
		assertTrue(resources.contains("http://dbpedia.org/ontology/birthDate"));
		assertTrue(resources.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));

		String line = sparqlUnit.getLineRepresentation();
		assertTrue(line.contains("http://xmlns.com/foaf/0.1/name"));
		assertTrue(line.contains("http://dbpedia.org/ontology/Artist"));
		assertTrue(line.contains("http://dbpedia.org/ontology/nationality"));
		assertTrue(line.contains("http://dbpedia.org/resource/Spain"));
		assertTrue(line.contains("http://dbpedia.org/ontology/birthDate"));
		assertTrue(line.contains("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
	}

	/**
	 * Tests, if resources of both, positive and negative inputs are extracted.
	 */
	@Test
	public void testResourceSets() {
		SpabApi spabApi = new SpabApi();

		// dbpedia-owl:pubchem
		// dbpedia-owl:Person
		spabApi.addPositive(SELECT3);

		// owl:DatatypeProperty
		spabApi.addNegative(DESCRIBE);

		// dbpedia-owl:pubchem
		spabApi.addNegative(SELECT);

		Set<String> extractedResources = spabApi.getInput().getResources();
		Set<String> positiveResources = spabApi.getInput().getPositives().get(0).getResources();
		Set<String> negativeResources = spabApi.getInput().getNegatives().get(0).getResources();
		negativeResources.addAll(spabApi.getInput().getNegatives().get(1).getResources());
		Set<String> posNegResources = positiveResources;
		posNegResources.addAll(negativeResources);

		// Check existence of replaced strings, contained in pos+neg
		assertTrue(extractedResources.contains("http://dbpedia.org/ontology/pubchem"));
		assertTrue(extractedResources.contains("http://dbpedia.org/ontology/Person"));
		assertTrue(extractedResources.contains("http://www.w3.org/2002/07/owl#DatatypeProperty"));

		// Compare extracted and pos+neg
		assertTrue(extractedResources.containsAll(posNegResources));
		assertTrue(posNegResources.containsAll(extractedResources));
	}

	@Test
	public void testPrefixReplacement() {

		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(SELECT);
		spabApi.addPositive(SELECT2);
		spabApi.addPositive(SELECT3);
		spabApi.addPositive(DESCRIBE);
		spabApi.addPositive(CONSTRUCT);
		spabApi.addPositive(IGUANA_FUSEKI);

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

			System.out.println();
			System.out.println(spabApi.getInput().getPositives().get(5).getOriginalString());
			System.out.println(spabApi.getInput().getPositives().get(5).getLineRepresentation());
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