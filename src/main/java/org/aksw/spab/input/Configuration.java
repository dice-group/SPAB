package org.aksw.spab.input;

import org.aksw.spab.SpabApi;
import org.aksw.spab.SpabApi.CandidateImplementation;
import org.aksw.spab.exceptions.InputRuntimeException;

/**
 * Container for configuration values.
 * 
 * @author Adrian Wilke
 */
public class Configuration {

	protected CandidateImplementation candidateImplementation = SpabApi.CANDIDATE_IMPLEMENTATION;
	protected boolean checkPerfectSolution = SpabApi.CHECK_PERFECT_SOLUTION;
	protected float lambda = SpabApi.LAMBDA;
	protected int maxIterations = SpabApi.MAX_ITERATIONS;

	/**
	 * Sets, if algorithm should stop on discovery of perfect solution. If true, the
	 * overall execution time can become better. If false, the final score of the
	 * best candidate can become better.
	 */
	public void checkPerfectSolution(boolean checkPerfectSolution) {
		this.checkPerfectSolution = checkPerfectSolution;
	}

	/**
	 * Gets the implementation used for SPARQL query candidates. These candidates
	 * represent general SPARQL queries, which are compared with SPARQL query
	 * inputs.
	 */
	public CandidateImplementation getCandidateImplementation() {
		return candidateImplementation;
	}

	/**
	 * Gets lambda.
	 */
	public float getLambda() {
		return lambda;
	}

	/**
	 * Gets maximum number of iterations
	 */
	public int getMaxIterations() {
		return maxIterations;
	}

	/**
	 * Gets info, if algorithm should stop on discovery of perfect solution.
	 */
	public boolean isPerfectSolutionChecked() {
		return checkPerfectSolution;
	}

	/**
	 * Sets the implementation used for SPARQL query candidates. These candidates
	 * represent general SPARQL queries, which are compared with SPARQL query
	 * inputs.
	 */
	public void setCandidateImplementation(CandidateImplementation candidateImplementation) {
		this.candidateImplementation = candidateImplementation;
	}

	/**
	 * Checks and sets lambda. Has to be 0 <= L < 1. If lambda is 0, only the
	 * f-measure of candidates is used. With higher values, shorter candidates will
	 * be rated better.
	 * 
	 * @throws InputRuntimeException
	 *             if lambda is not in scope.
	 */
	public void setLambda(float lambda) throws InputRuntimeException {
		if (lambda < 0 || lambda >= 1) {
			throw new InputRuntimeException("Lambda has to be 0 <= L < 1. Given value: " + lambda);
		}
		this.lambda = lambda;
	}

	/**
	 * Set maximum number of iterations
	 */
	public void setMaxIterations(int maxIterations) {
		this.maxIterations = maxIterations;
	}
}