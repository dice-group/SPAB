package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.input.SparqlUnit;
import org.dice_research.spab.structures.CandidateVertex;

import junit.framework.TestCase;

/**
 * Template for test cases.
 * 
 * @author Adrian Wilke
 */
public abstract class AbstractTestCase extends TestCase {

	final static public boolean PRINT = false;

	/**
	 * Prints info line of candidate vertex.
	 * 
	 * @param candidateVertex
	 *            Data source
	 * @param info
	 *            Info string, which is printed before candidate information
	 * @param print
	 *            Control flag
	 */
	public void printCandidateVertex(CandidateVertex candidateVertex, String info, boolean print) {
		if (print) {
			System.out.print(info + " ");
			System.out.println(candidateVertex.getInfoLine());
		}
	}

	/**
	 * Prints overall result.
	 * 
	 * @param bestCandidateVertex
	 *            Data source of best candidate vertex
	 * @param spabApi
	 *            Data source for overall algorithm information
	 * @param info
	 *            Info string, which is printed before result information
	 * @param print
	 *            Control flag
	 */
	public void printResult(CandidateVertex bestCandidateVertex, SpabApi spabApi, String info, boolean print) {
		if (print) {
			System.out.println();
			System.out.println(info);
			System.out.println("Final score of best candidate: " + bestCandidateVertex.getScore());
			System.out.println("F-measure of best candidate:   " + bestCandidateVertex.getfMeasure());
			System.out.println("Generation of best candidate: " + bestCandidateVertex.getGeneration());
			System.out.println("Generated generations:        " + spabApi.getGraph().getDepth());
			System.out.println("Number of remaining candidates in queue: " + spabApi.getQueue().getQueue().size());
			System.out.print("Next best scores: ");
			while (!spabApi.getQueue().getQueue().isEmpty()) {
				System.out.print(spabApi.getQueue().pollBestCandidate().getScore() + " ");
			}
			System.out.println();
			System.out.println("Number generated candidates: " + spabApi.getGraph().getAllCandidates().size());
			System.out.println("RegEx of best candidate: " + bestCandidateVertex.getCandidate().getRegEx());
		}
	}

	/**
	 * Prints regular expressions of all generated candidates.
	 * 
	 * @param spabApi
	 *            Data source for overall algorithm information
	 * @param info
	 *            Info string, which is printed before result information
	 * @param print
	 *            Control flag
	 */
	public void printGeneratedCandidates(SpabApi spabApi, String info, boolean print) {
		if (print) {
			System.out.println();
			System.out.println(info);
			for (CandidateVertex candidateVertex : spabApi.getGraph().getAllCandidates()) {
				System.out.println(candidateVertex.getCandidate().getRegEx());
			}
		}
	}

	/**
	 * Prints line representations of inputs.
	 * 
	 * @param spabApi
	 *            Data source for overall algorithm information
	 * @param positives
	 *            True for positives, false for negatives
	 * @param info
	 *            Info string, which is printed before result information
	 * @param print
	 *            Control flag
	 */
	public void printInput(SpabApi spabApi, boolean positives, String info, boolean print) {
		if (print) {
			System.out.println();
			System.out.println(info);
			List<SparqlUnit> sparqlUnits = positives ? spabApi.getInput().getPositives()
					: spabApi.getInput().getNegatives();
			for (SparqlUnit sparqlUnit : sparqlUnits) {
				System.out.println(sparqlUnit.getLineRepresentation());
			}
		}
	}
}