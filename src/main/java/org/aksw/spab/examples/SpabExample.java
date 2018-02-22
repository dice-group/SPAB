package org.aksw.spab.examples;

import org.aksw.spab.SpabApi;
import org.aksw.spab.SpabApi.CandidateImplementation;
import org.aksw.spab.exceptions.SpabException;
import org.aksw.spab.structures.CandidateVertex;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabExample {

	public static String query1 = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";
	public static String query2 = "SELECT ?name ?mbox\n" + "WHERE\n" + "  { ?x foaf:name ?name .\n"
			+ "    ?x foaf:mbox ?mbox }";
	public static String query3 = "SELECT ?title\n" + "WHERE\n" + "{\n"
			+ "  <http://example.org/book/book1> <http://purl.org/dc/elements/1.1/title> ?title .\n" + "}";
	public static String insert = "INSERT { <http://example/egbook> dc:title  \"This is an example title\" } WHERE {}";
	public static String construct = "CONSTRUCT   { <http://example.org/person#Alice> vcard:FN ?name }\n"
			+ "			WHERE       { ?x foaf:name ?name }";

	public static void main(String[] args) throws SpabException {

		System.out.println(query1);

		SpabApi spab = new SpabApi();
		spab.addNamespacePrefix("foaf", "http://xmlns.com/foaf/0.1/");

		// spab.addPositive(query1);
		// spab.addPositive(query2);
		spab.addNegative(query3);

		// spab.addPositive(query1);
		// spab.addPositive(query2);

		spab.addNamespacePrefix("vcard", "http://www.w3.org/2001/vcard-rdf/3.0#");
		spab.addPositive(construct);

		// spab.addNamespacePrefix("dc", "http://purl.org/dc/elements/1.1/");
		// spab.addPositive(insert);

		spab.setLambda(.5f);
		spab.setMaxIterations(30);
		spab.setCheckPerfectSolution(true);
		spab.setCandidateImplementation(CandidateImplementation.SPAB_ONE);

		CandidateVertex bestCandidate = spab.run();

		System.out.println("Final score of best candidate: " + bestCandidate.getScore());
		System.out.println("F-measure of best candidate:   " + bestCandidate.getfMeasure());
		System.out.println("Generation of best candidate: " + bestCandidate.getGeneration());
		System.out.println("Generated generations:        " + spab.getGraph().getDepth());
		System.out.println("Number of remaining candidates in queue: " + spab.getQueue().getQueue().size());
		System.out.print("Next best scores: ");
		while (!spab.getQueue().getQueue().isEmpty()) {
			System.out.print(spab.getQueue().getBestCandidate().getScore() + " ");
		}
		System.out.println();
		System.out.println("Number generated candidates: " + spab.getGraph().getAllCandidates().size());
		System.out.println("RegEx of best candidate: " + bestCandidate.getCandidate().getRexEx());
	}
}