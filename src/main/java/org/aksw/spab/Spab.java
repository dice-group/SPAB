package org.aksw.spab;

import java.util.List;

import org.aksw.spab.exceptions.PerfectSolutionException;
import org.aksw.spab.exceptions.SpabException;
import org.aksw.spab.exceptions.UserInputException;
import org.aksw.spab.input.Input;

/**
 * SPAB: SPARQL Benchmark Query Generator
 * 
 * Configuration: Use the set-methods.
 * 
 * Input: Use the add-methods.
 * 
 * Execution: Use the run-method.
 * 
 * Information: Use the get-methods.
 * 
 * @author Adrian Wilke
 */
public class Spab {

	protected CandidateGraph graph;
	protected Input input;
	protected CandidateQueue queue;

	/**
	 * Initializes SPAB.
	 */
	public Spab() {
		input = new Input();
		graph = new CandidateGraph();
		queue = new CandidateQueue();
	}

	/**
	 * Ads a prefix for a namespace. Has to be called before input queries are
	 * added.
	 */
	public void addNamespacePrefix(String prefix, String uri) {
		input.addNamespacePrefix(prefix, uri);
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) {
		input.addNegative(sparqlQuery);
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) {
		input.addPositive(sparqlQuery);
	}

	/**
	 * Gets candidate graph.
	 */
	public CandidateGraph getGraph() {
		return graph;
	}

	/**
	 * Gets input container.
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Gets candidate priority queue.
	 */
	public CandidateQueue getQueue() {
		return queue;
	}

	/**
	 * Executes SPAB.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 */
	public CandidateVertex run() throws SpabException {
		try {

			// Run for maximum number of iterations
			return spab();

		} catch (PerfectSolutionException e) {

			// Perfect candidate was found before reaching maximum number of iterations
			return e.getCandidate();
		}
	}

	/**
	 * Sets, if algorithm should stop on discovery of perfect solution. If true, the
	 * overall execution time can become better. If false, the final score of the
	 * best candidate can become better.
	 * 
	 * Default value: {@link Input#CHECK_PERFECT_SOLUTION}
	 */
	public void setCheckPerfectSolution(boolean checkPerfectSolution) {
		input.checkPerfectSolution(checkPerfectSolution);
	}

	/**
	 * Checks and sets lambda. Has to be 0 <= L < 1. If lambda is 0, only the
	 * f-measure of candidates is used. With higher values, shorter candidates will
	 * be rated better.
	 * 
	 * Default value: {@link Input#LAMBDA}
	 * 
	 * @throws UserInputException
	 *             if lambda is not in scope.
	 */
	public void setLambda(float lambda) throws UserInputException {
		input.setLambda(lambda);
	}

	/**
	 * Set maximum number of iterations.
	 * 
	 * Default value: {@link Input#MAX_ITERATIONS}
	 */
	public void setMaxIterations(int maxIterations) {
		input.setMaxIterations(maxIterations);
	}

	/**
	 * Executes SPAB.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 * @throws PerfectSolutionException
	 *             if candidate is found, which has no false positives or false
	 *             negatives
	 */
	protected CandidateVertex spab() throws SpabException, PerfectSolutionException {

		// Generate and add first candidate
		CandidateVertex firstCandidate = new CandidateVertex(Configuration.getRoot());
		graph.addCandidate(firstCandidate);
		firstCandidate.calculateScore(input, graph.getDepth());
		queue.add(firstCandidate);

		// For specified number of iterations run algorithm
		for (int i = 1; i <= input.getMaxIterations(); i++) {

			// Get best candidate, generate children, and add them into graph
			CandidateVertex bestCandidate = queue.getBestCandidate();
			List<CandidateVertex> bestCandidateChildren = bestCandidate.generateChildren();
			graph.addCandidates(bestCandidateChildren, bestCandidate);

			// Graph depth increases by 1, as new children were generated and added.
			// The graph depth influences score of all candidates.
			// Therefore, the scores of all current candidates have to be re-calculated.
			// A reset of the priority queue is needed to maintain changed
			// priorities, represented by scores.
			for (CandidateVertex queueCandidate : queue.reset()) {
				queueCandidate.calculateScore(input, graph.getDepth());
				queue.add(queueCandidate);
			}

			// Calculate scores of new children and add them to queue
			for (CandidateVertex bestCandidateChild : bestCandidateChildren) {
				bestCandidateChild.calculateScore(input, graph.getDepth());
				queue.add(bestCandidateChild);
			}
		}

		// Return best candidate
		CandidateVertex bestCandidate = firstCandidate;
		for (CandidateVertex candidate : graph.getAllCandidates()) {
			if (candidate.getScore() > bestCandidate.getScore()) {
				bestCandidate = candidate;
			}
		}
		return bestCandidate;
	}
}