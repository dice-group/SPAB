package org.dice_research.spab.candidates.two;

import org.dice_research.spab.candidates.two.Features.Feature;
import org.dice_research.spab.exceptions.CandidateRuntimeException;

/**
 * Creates children.
 * 
 * @author Adrian Wilke
 */
public class SpabTwoCandidate extends SpabTwoAbstractCandidate {

	/**
	 * Constructs candidate with features.
	 * 
	 * @param features
	 *            If null, a new features object is created
	 */
	public SpabTwoCandidate(Features features) {
		super(features);
	}

	/**
	 * Adds children to {@link #children}.
	 */
	@Override
	protected void generateChildren() {

		if (!getFeatures().featureMap.containsKey(Feature.TYPE)) {

			for (String query : Features._002_QUERIES) {
				Features childFeatures = new Features(getFeatures());
				childFeatures.featureMap.put(Feature.TYPE, query);
				children.add(new SpabTwoCandidate(childFeatures));
			}

			for (String updateRequest : Features._30_UPDATES) {
				Features childFeatures = new Features(getFeatures());
				childFeatures.featureMap.put(Feature.TYPE, updateRequest);
				children.add(new SpabTwoCandidate(childFeatures));
			}
		}

		if (!getFeatures().featureMap.containsKey(Feature.WHERE_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.WHERE_CLAUSE, Features._017_WHERE_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	@Override
	public String getRegEx() throws CandidateRuntimeException {
		return new RegEx(getFeatures()).generate();
	}
}