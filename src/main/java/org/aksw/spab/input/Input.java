package org.aksw.spab.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.spab.exceptions.UserInputException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Container for SPAB configuration and input queries.
 * 
 * @author Adrian Wilke
 */
public class Input {

	public static final boolean CHECK_PERFECT_SOLUTION = true;
	public static final float LAMBDA = 0.2f;
	public static final int MAX_ITERATIONS = 10;

	protected boolean checkPerfectSolution = CHECK_PERFECT_SOLUTION;
	protected float lambda = LAMBDA;
	protected int maxIterations = MAX_ITERATIONS;

	protected Model model;

	protected List<InputQuery> negatives;
	protected List<InputQuery> positives;

	/**
	 * Initializes model and sets namespace prefixes for RDF, RDFS, and SPIN.
	 */
	public Input() {
		positives = new LinkedList<InputQuery>();
		negatives = new LinkedList<InputQuery>();

		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("rdfs", RDFS.getURI());
	}

	/**
	 * Adds prefix for namespace
	 */
	public void addNamespacePrefix(String prefix, String uri) {
		model.setNsPrefix(prefix, uri);
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) {
		negatives.add(new InputQuery(sparqlQuery, this));
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) {
		positives.add(new InputQuery(sparqlQuery, this));
	}

	/**
	 * Sets, if algorithm should stop on discovery of perfect solution. If true, the
	 * overall execution time can become better. If false, the final score of the
	 * best candidate can become better.
	 */
	public void checkPerfectSolution(boolean checkPerfectSolution) {
		this.checkPerfectSolution = checkPerfectSolution;
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
	 * Gets prefixes for namespaces
	 */
	public Map<String, String> getNamespaces() {
		return model.getNsPrefixMap();
	}

	/**
	 * Gets set of negatie inputs.
	 */
	public List<InputQuery> getNegatives() {
		return negatives;
	}

	/**
	 * Gets set of positive inputs.
	 */
	public List<InputQuery> getPositives() {
		return positives;
	}

	/**
	 * Gets info, if algorithm should stop on discovery of perfect solution.
	 */
	public boolean isPerfectSolutionChecked() {
		return checkPerfectSolution;
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
		if (lambda < 0 || lambda >= 1) {
			throw new UserInputException("Lambda has to be 0 <= L < 1. Given value: " + lambda);
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