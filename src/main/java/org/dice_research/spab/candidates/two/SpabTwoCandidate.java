package org.dice_research.spab.candidates.two;

import org.dice_research.spab.candidates.two.Features.Feature;
import org.dice_research.spab.candidates.two.Features.WhereClause;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;

/**
 * Creates children.
 * 
 * @author Adrian Wilke
 */
public class SpabTwoCandidate extends SpabTwoAbstractCandidate {

	protected RegEx regExCache;

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
	protected void generateChildren(Input input) {

		// Type

		if (!getFeatures().featureMap.containsKey(Feature.TYPE)) {
			for (String query : Features.TYPE_QUERIES) {
				Features childFeatures = new Features(getFeatures());
				childFeatures.featureMap.put(Feature.TYPE, query);
				children.add(new SpabTwoCandidate(childFeatures));
			}
			for (String updateRequest : Features.UPDATES) {
				Features childFeatures = new Features(getFeatures());
				childFeatures.featureMap.put(Feature.TYPE, updateRequest);
				children.add(new SpabTwoCandidate(childFeatures));
			}
		}

		// Where

		if (!getFeatures().featureMap.containsKey(Feature.WHERE_CLAUSE)) {
			for (WhereClause whereClause : Features.WhereClause.values()) {
				if (!whereClause.equals(Features.WhereClause.WHERE_RESOURCES)) {
					Features childFeatures = new Features(getFeatures());
					childFeatures.featureMap.put(Feature.WHERE_CLAUSE, whereClause.toString());
					children.add(new SpabTwoCandidate(childFeatures));
				}
			}
		}

		// Where resources

		if (!getFeatures().featureMap.containsKey(Feature.WHERE_CLAUSE) || getFeatures().featureMap
				.get(Feature.WHERE_CLAUSE).equals(Features.WhereClause.WHERE_RESOURCES.toString())) {
			for (String resource : input.getResources()) {
				if (getFeatures().resourcesWhereClause.contains(resource)) {
					continue;
				} else {
					Features childFeatures = new Features(getFeatures());
					childFeatures.featureMap.put(Feature.WHERE_CLAUSE, Features.WhereClause.WHERE_RESOURCES.toString());
					childFeatures.resourcesWhereClause.add(resource);
					children.add(new SpabTwoCandidate(childFeatures));
				}
			}
		}

		// Group

		if (!getFeatures().featureMap.containsKey(Feature.GROUP_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.GROUP_CLAUSE, Features.GROUP_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}

		// Having

		if (!getFeatures().featureMap.containsKey(Feature.HAVING_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.HAVING_CLAUSE, Features.HAVING_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}

		// Order

		if (!getFeatures().featureMap.containsKey(Feature.ORDER_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.ORDER_CLAUSE, Features.ORDER_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	@Override
	public String getRegEx() throws CandidateRuntimeException {
		if (regExCache == null) {
			regExCache = new RegEx(getFeatures());
		}
		return regExCache.generate();
	}
}