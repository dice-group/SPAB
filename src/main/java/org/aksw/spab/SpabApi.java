package org.aksw.spab;

import org.aksw.spab.exceptions.CandidateRuntimeException;
import org.aksw.spab.exceptions.InputRuntimeException;
import org.aksw.spab.exceptions.SpabException;
import org.aksw.spab.input.Configuration;
import org.aksw.spab.input.Input;
import org.aksw.spab.structures.CandidateGraph;
import org.aksw.spab.structures.CandidateQueue;
import org.aksw.spab.structures.CandidateVertex;

/**
 * SPAB API: SPARQL Benchmark Query Generator
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
public class SpabApi {

	/**
	 * Available implementations for SPARQL query candidates. These candidates
	 * represent general SPARQL queries, which are compared with SPARQL query
	 * inputs.
	 */
	public static enum CandidateImplementation {
		DUMMY
	}

	final protected SpabAlgorithm spab = new SpabAlgorithm();

	/**
	 * Ads a prefix for a namespace. Has to be called before input queries are
	 * added.
	 */
	public void addNamespacePrefix(String prefix, String uri) {
		spab.getInput().addNamespacePrefix(prefix, uri);
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) {
		spab.getInput().addNegative(sparqlQuery);
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) {
		spab.getInput().addPositive(sparqlQuery);
	}

	/**
	 * Gets candidate graph.
	 */
	public CandidateGraph getGraph() {
		return spab.getGraph();
	}

	/**
	 * Gets input container.
	 */
	public Input getInput() {
		return spab.getInput();
	}

	/**
	 * Gets candidate priority queue.
	 */
	public CandidateQueue getQueue() {
		return spab.getQueue();
	}

	/**
	 * Runs SPAB.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 */
	public CandidateVertex run() throws SpabException {
		try {

			// Run for maximum number of iterations
			return spab.execute();

		} catch (CandidateRuntimeException e) {

			// Handle runtime exception in candidate implementation
			throw new SpabException(e);

		} catch (InputRuntimeException e) {

			// Handle runtime exception representing a wrong (user) input
			throw new SpabException(e);

		}
	}

	/**
	 * Sets, if algorithm should stop on discovery of perfect solution. If true, the
	 * overall execution time can become better. If false, the final score of the
	 * best candidate can become better.
	 * 
	 * Default value: {@link Configuration#CHECK_PERFECT_SOLUTION}
	 */
	public void setCheckPerfectSolution(boolean checkPerfectSolution) {
		spab.getConfiguration().checkPerfectSolution(checkPerfectSolution);
	}

	/**
	 * Checks and sets lambda. Has to be 0 <= L < 1. If lambda is 0, only the
	 * f-measure of candidates is used. With higher values, shorter candidates will
	 * be rated better.
	 * 
	 * Default value: {@link Configuration#LAMBDA}
	 * 
	 * @throws InputRuntimeException
	 *             if lambda is not in scope.
	 */
	public void setLambda(float lambda) throws InputRuntimeException {
		spab.getConfiguration().setLambda(lambda);
	}

	/**
	 * Sets the implementation used for SPARQL query candidates. These candidates
	 * represent general SPARQL queries, which are compared with SPARQL query
	 * inputs.
	 * 
	 * 
	 * Default value: {@link Configuration#MAX_ITERATIONS}
	 */
	public void setCandidateImplementation(CandidateImplementation candidateImplementation) {
		spab.getConfiguration().setCandidateImplementation(candidateImplementation);
	}

	/**
	 * Set maximum number of iterations.
	 * 
	 * Default value: {@link Configuration#MAX_ITERATIONS}
	 */
	public void setMaxIterations(int maxIterations) {
		spab.getConfiguration().setMaxIterations(maxIterations);
	}
}