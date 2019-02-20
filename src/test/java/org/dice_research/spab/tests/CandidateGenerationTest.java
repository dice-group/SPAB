package org.dice_research.spab.tests;

import java.util.Set;

import org.dice_research.spab.AbstractTestCase;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.SparqlUnit;
import org.dice_research.spab.structures.CandidateGraph;
import org.dice_research.spab.structures.CandidateVertex;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

/**
 * Tests the structure of generating candidates.
 * 
 * @author Adrian Wilke
 */
public class CandidateGenerationTest extends AbstractTestCase {

	final static public boolean PRINT = false;

	public static final String SELECT1 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o }";
	public static final String SELECT2 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o . ?o dbpedia-owl:pubchem ?s }";
	public static final String SELECT3 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o . ?o dbpedia-owl:Person ?s }";
	public static final String SELECT4 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o . ?o dbpedia-owl:JavaDeveloper ?s }";
	public static final String SELECT5 = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?o dbpedia-owl:Person ?s }";

	@Test
	public void test() throws SpabException {

		// Input
		SpabApi spabApi = new SpabApi();
		spabApi.setLambda(0.0f);

		spabApi.addPositive(SELECT1);
		spabApi.addPositive(SELECT2);

		spabApi.addNegative(SELECT3);
		spabApi.addNegative(SELECT4);
		spabApi.addNegative(SELECT5);

		if (PRINT) {
			System.out.println("Positives:");
			for (SparqlUnit sparqlUnit : spabApi.getInput().getPositives()) {
				System.out.println(" " + sparqlUnit.getLineRepresentation());
			}
			System.out.println("Negatives:");
			for (SparqlUnit sparqlUnit : spabApi.getInput().getNegatives()) {
				System.out.println(" " + sparqlUnit.getLineRepresentation());
			}
			System.out.println("Resources:");
			for (String resource : spabApi.getInput().getResources()) {
				System.out.println(" " + resource);
			}
		}

		// Run
		CandidateVertex bestCandidate = spabApi.run();
		printCandidateVertex(bestCandidate, "Best", PRINT);

		// Root
		CandidateGraph candidateGraph = spabApi.getGraph();
		CandidateVertex root = candidateGraph.getRoot();
		printCandidateVertex(root, "Root", PRINT);

		// Children of root
		Graph<CandidateVertex, DefaultEdge> graph = candidateGraph.getGraph();
		Set<DefaultEdge> rootEdges = graph.edgesOf(root);
		for (DefaultEdge rootEdge : rootEdges) {
			CandidateVertex rootChild = graph.getEdgeTarget(rootEdge);
			printCandidateVertex(rootChild, "RootChild", PRINT);
		}

		// Next candidates in queue
		CandidateVertex nextCandidate = bestCandidate;
		int i = 0;
		while (nextCandidate != null) {
			printCandidateVertex(nextCandidate, "Queue-" + i, PRINT);
			nextCandidate = spabApi.getQueue().peekBestCandidate(i++);
		}

		// Visited candidates
		i = 0;
		for (CandidateVertex candidateVertex : spabApi.getStack()) {
			printCandidateVertex(candidateVertex, "Stack-" + i++, PRINT);
		}

		// Test: There is one negative input without dbpedia-owl:pubchem. Therefore, the
		// best candidate should include dbpedia-owl:pubchem.
		assertTrue(bestCandidate.getCandidate().getRegEx().contains("http://dbpedia.org/ontology/pubchem"));
	}

}
