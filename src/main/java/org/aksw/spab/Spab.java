package org.aksw.spab;

import java.util.List;

import org.aksw.spab.exceptions.ParseException;
import org.aksw.spab.exceptions.PerfectSolutionException;
import org.aksw.spab.exceptions.UserInputException;
import org.aksw.spab.input.Input;

/**
 * SPAB: SPARQL Benchmark Query Generator
 * 
 * @author Adrian Wilke
 */
public class Spab {

	private Input input;
	private CandidateGraph graph;
	private CandidateQueue queue;

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
	 * Adds query to set of positive inputs.
	 * 
	 * @throws ParseException
	 *             if query can not be parsed
	 */
	public void addPositive(String sparqlQuery) throws ParseException {
		input.addPositive(sparqlQuery);
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws ParseException
	 *             if query can not be parsed
	 */
	public void addNegative(String sparqlQuery) throws ParseException {
		input.addNegative(sparqlQuery);
	}

	/**
	 * Checks and sets lambda. Has to be 0 <= L < 1. If lambda is 0, only the
	 * f-measure of candidates is used. With higher values, shorter candidates will
	 * be rated better.
	 * 
	 * @throws UserInputException
	 *             if lambda is not in scope.
	 */
	public void setLambda(float lambda) throws UserInputException {
		input.setLambda(lambda);
	}

	/**
	 * Set maximum number of iterations
	 */
	public void setMaxIterations(int maxIterations) {
		input.setMaxIterations(maxIterations);
	}

	/**
	 * Executes SPAB.
	 */
	public Candidate run() {

		// Generate and add first candidate
		Candidate firstCandidate = new Candidate(null);
		graph.addCandidate(firstCandidate, null);
		try {
			firstCandidate.calculateScore(input, graph.getDepth());
		} catch (PerfectSolutionException e) {
			return e.getCandidate();
		}
		queue.add(firstCandidate);

		// For specified number of iterations run algorithm
		for (int i = 1; i <= input.getMaxIterations(); i++) {

			// Get best candidate, generate children, and add them into graph
			Candidate bestCandidate = queue.poll();
			List<Candidate> bestCandidateChildren = bestCandidate.generateChildren();
			graph.addCandidates(bestCandidateChildren, bestCandidate);

			// Graph depth changes, as new children were generated and added.
			// Graph depth influences score of all candidates.
			// Scores of all current candidates have to be re-calculated.
			// Reset of queue needed to maintain priorities based on new scores.
			for (Candidate queueCandidate : queue.reset()) {
				try {
					queueCandidate.calculateScore(input, graph.getDepth());
				} catch (PerfectSolutionException e) {
					return e.getCandidate();
				}
				queue.add(queueCandidate);
			}

			// Calculate scores of new children and add them to queue
			for (Candidate bestCandidateChild : bestCandidateChildren) {
				try {
					bestCandidateChild.calculateScore(input, graph.getDepth());
				} catch (PerfectSolutionException e) {
					return e.getCandidate();
				}
				queue.add(bestCandidateChild);
			}
		}

		// Return best candidate
		Candidate bestCandidate = firstCandidate;
		for (Candidate candidate : graph.getGraph().vertexSet()) {
			if (candidate.getScore() > bestCandidate.getScore()) {
				bestCandidate = candidate;
			}
		}
		return bestCandidate;
	}

	/**
	 * Gets input container.
	 */
	public Input getInput() {
		return input;
	}

	/**
	 * Gets candidate graph.
	 */
	public CandidateGraph getGraph() {
		return graph;
	}

	/**
	 * Gets candidate priority queue.
	 */
	public CandidateQueue getQueue() {
		return queue;
	}
}