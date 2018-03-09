package org.dice_research.spab.candidates.two;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Abstract class with general data and methods for implementations.
 * 
 * @author Adrian Wilke
 */
public abstract class SpabTwoAbstractCandidate implements Candidate {

	protected CandidateVertex candidateVertex;
	protected List<Candidate> children = new LinkedList<Candidate>();
	protected Features features;

	/**
	 * Constructs candidate with features.
	 * 
	 * @param features
	 *            If null, a new features object is created
	 */
	public SpabTwoAbstractCandidate(Features features) {
		if (features == null) {
			this.features = new Features();
		} else {
			this.features = features;
		}
	}

	/**
	 * Adds children to {@link #children}.
	 */
	protected abstract void generateChildren();

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {
		return children;
	}

	/**
	 * Gets features of candidate.
	 */
	protected Features getFeatures() {
		return features;
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public abstract String getRegEx() throws CandidateRuntimeException;

	/**
	 * Sets the related vertex in the candidate graph.
	 */
	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}
}