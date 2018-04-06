package org.dice_research.spab;

import java.util.Set;

import org.dice_research.spab.exceptions.SpabException;
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

	@Test
	public void test() throws SpabException {

		// Input
		SpabApi spabApi = new SpabApi();
		spabApi.addPositive(SELECT1);
		spabApi.addPositive(SELECT2);
		spabApi.addNegative(SELECT3);

		// Run
		CandidateVertex bestCandidate = spabApi.run();
		printCandidateVertex(bestCandidate, "best", PRINT);

		// Best
		CandidateGraph candidateGraph = spabApi.getGraph();
		CandidateVertex root = candidateGraph.getRoot();
		printCandidateVertex(root, "root", PRINT);

		// Children of best
		Graph<CandidateVertex, DefaultEdge> graph = candidateGraph.getGraph();
		Set<DefaultEdge> rootEdges = graph.edgesOf(root);
		for (DefaultEdge rootEdge : rootEdges) {
			CandidateVertex rootChild = graph.getEdgeTarget(rootEdge);
			printCandidateVertex(rootChild, "rchi", PRINT);
		}
	}

	public void printCandidateVertex(CandidateVertex candidateVertex, String info, boolean print) {
		if (print) {
			System.out.print(info);
			System.out.print(" S:"
					+ (candidateVertex.getScore() == -1 ? "-" : Math.round(candidateVertex.getScore() * 100) / 100d));
			System.out.print(" TP:" + candidateVertex.getNumberOfTruePositives());
			System.out.print(" TN:" + candidateVertex.getNumberOfTrueNegatives());
			System.out.print(" FP:" + candidateVertex.getNumberOfFalsePositives());
			System.out.print(" FN:" + candidateVertex.getNumberOfFalseNegatives());
			System.out.print(" G:" + candidateVertex.getGeneration());
			System.out.print(" " + candidateVertex.getCandidate().getRegEx());
			System.out.println();
		}
	}

}
