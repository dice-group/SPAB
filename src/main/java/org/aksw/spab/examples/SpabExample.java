package org.aksw.spab.examples;

import org.aksw.spab.Candidate;
import org.aksw.spab.Spab;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabExample {

	public static final boolean TEST_SPAB = true;

	public String query1 = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";
	public String query2 = "SELECT ?name ?mbox\n" + "WHERE\n" + "  { ?x foaf:name ?name .\n"
			+ "    ?x foaf:mbox ?mbox }";
	public String query3 = "SELECT ?title\n" + "WHERE\n" + "{\n"
			+ "  <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title .\n" + "}    ";

	public static void main(String[] args) throws Exception {
		SpabExample example = new SpabExample();

		if (TEST_SPAB) {
			example.testSpab();
		}
	}

	public void testSpab() throws Exception {

		Spab spab = new Spab();
		spab.addNamespacePrefix("foaf", "<http://xmlns.com/foaf/0.1/>");

		spab.addPositive(query1);
		spab.addPositive(query2);
		spab.addPositive(query1);
		spab.addPositive(query2);
		spab.addPositive(query1);
		spab.addNegative(query3);
		spab.addNegative(query3);
		spab.addNegative(query3);
		spab.addNegative(query3);

		spab.setLambda(.3f);
		spab.setMaxIterations(30);

		Candidate bestCandidate = spab.run();

		System.out.println("Best score: " + bestCandidate.getScore());
		System.out.println("Number of candidates: " + spab.getGraph().getGraph().vertexSet().size());
		System.out.print("Next best scores: ");
		while (!spab.getQueue().getQueue().isEmpty()) {
			System.out.print(spab.getQueue().getQueue().poll().getScore() + " ");
		}
	}
}