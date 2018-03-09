package org.dice_research.spab;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.exceptions.InputRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateGraph;
import org.dice_research.spab.structures.CandidateQueue;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * SPAB API: SPARQL Benchmark Query Generalization
 * 
 * <ul>
 * <li>Configuration: Use the set-methods.
 * <li>Input: Use the add-methods.
 * <li>Execution: Use the run-method.
 * <li>Information: Use the get-methods.
 * </ul>
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
		SPAB_ONE, UNIT_TEST
	}

	/**
	 * Default candidate implementation.
	 */
	public static final CandidateImplementation CANDIDATE_IMPLEMENTATION = CandidateImplementation.SPAB_ONE;

	/**
	 * Default setting for stopping on discovery of perfect solution.
	 */
	public static final boolean CHECK_PERFECT_SOLUTION = true;

	/**
	 * Default lambda value.
	 */
	public static final float LAMBDA = 0.2f;

	/**
	 * Default number of iterations.
	 */
	public static final int MAX_ITERATIONS = 10;

	/**
	 * SPAB instance
	 */
	final protected SpabAlgorithm spab = new SpabAlgorithm();

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) throws InputRuntimeException {
		spab.getInput().addNegative(sparqlQuery);
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) throws InputRuntimeException {
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
	 * Runs SPAB.
	 * 
	 * @param matcher
	 *            The matching algorithm to use. If null, the default implementation
	 *            implemented by {@link CandidateVertex#matches(Candidate, String)}
	 *            is used.
	 * 
	 * @return The best candidate found.
	 * 
	 * @throws SpabException
	 *             on errors in SPAB algorithm.
	 */
	public CandidateVertex run(Matcher matcher) throws SpabException {
		try {

			// Run for maximum number of iterations
			return spab.execute(matcher);

		} catch (CandidateRuntimeException e) {

			// Handle runtime exception in candidate implementation
			throw new SpabException(e);

		} catch (InputRuntimeException e) {

			// Handle runtime exception representing a wrong (user) input
			throw new SpabException(e);

		}
	}

	/**
	 * Sets the implementation used for SPARQL query candidates. These candidates
	 * represent general SPARQL queries, which are compared with SPARQL query
	 * inputs.
	 * 
	 * 
	 * Default value: {@link SpabApi#MAX_ITERATIONS}
	 */
	public void setCandidateImplementation(CandidateImplementation candidateImplementation) {
		spab.getConfiguration().setCandidateImplementation(candidateImplementation);
	}

	/**
	 * Sets, if algorithm should stop on discovery of perfect solution. If true, the
	 * overall execution time can become better. If false, the final score of the
	 * best candidate can become better.
	 * 
	 * Default value: {@link SpabApi#CHECK_PERFECT_SOLUTION}
	 */
	public void setCheckPerfectSolution(boolean checkPerfectSolution) {
		spab.getConfiguration().checkPerfectSolution(checkPerfectSolution);
	}

	/**
	 * Checks and sets lambda. Has to be 0 <= L < 1. If lambda is 0, only the
	 * f-measure of candidates is used. With higher values, shorter candidates will
	 * be rated better.
	 * 
	 * Default value: {@link SpabApi#LAMBDA}
	 * 
	 * @throws InputRuntimeException
	 *             if lambda is not in scope.
	 */
	public void setLambda(float lambda) throws InputRuntimeException {
		spab.getConfiguration().setLambda(lambda);
	}

	/**
	 * Set maximum number of iterations.
	 * 
	 * Default value: {@link SpabApi#MAX_ITERATIONS}
	 */
	public void setMaxIterations(int maxIterations) {
		spab.getConfiguration().setMaxIterations(maxIterations);
	}
}