package org.dice_research.spab.candidates.three;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Candidate implementation.
 * 
 * @author Adrian Wilke
 */
public class SpabThreeCandidate implements Candidate {

	protected CandidateVertex candidateVertex;
	protected Features features;

	/**
	 * Constructs candidate with features.
	 * 
	 * @param features
	 *            If null, a new features object is created
	 */
	public SpabThreeCandidate(Features features) {
		if (features == null) {
			this.features = new Features();
		} else {
			this.features = features;
		}
	}

	/**
	 * Generates and returns children.
	 */
	@Override
	public List<Candidate> getChildren(Input input) throws CandidateRuntimeException {
		List<Candidate> children = new LinkedList<Candidate>();
		for (Features subFeatures : features.generateSubFeatures(input)) {
			children.add(new SpabThreeCandidate(subFeatures));
		}
		return children;
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	@Override
	public String getRegEx() throws CandidateRuntimeException {
		return features.getRegex();
	}

	/**
	 * Sets the related vertex in the candidate graph.
	 */
	@Override
	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}
}