package org.dice_research.spab.candidates.two;

import java.util.LinkedList;
import java.util.List;

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

	protected static final boolean TMP_GENERATE_WHERE_RESOURCE_CHILDREN = false;

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

		if (!getFeatures().featureMap.containsKey(Feature.WHERE_CLAUSE)) {
			for (WhereClause whereClause : Features.WhereClause.values()) {

				if (whereClause.equals(Features.WhereClause.WHERE_RESOURCES)) {

					// TODO: Integrate and create RegEx
					if (TMP_GENERATE_WHERE_RESOURCE_CHILDREN) {
						continue;
					}

					// Handle resources in WHERE clause
					for (String resource : input.getResources()) {
						Features childFeatures = new Features(getFeatures());
						childFeatures.featureMap.put(Feature.WHERE_CLAUSE, whereClause.toString());
						children.add(new SpabTwoCandidate(childFeatures));
						List<String> resourcesList = new LinkedList<String>();
						resourcesList.add(resource);
						childFeatures.setResources(resourcesList);
					}

				} else {
					// Other WHERE variations
					Features childFeatures = new Features(getFeatures());
					childFeatures.featureMap.put(Feature.WHERE_CLAUSE, whereClause.toString());
					children.add(new SpabTwoCandidate(childFeatures));
				}
			}
		}

		if (!getFeatures().featureMap.containsKey(Feature.GROUP_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.GROUP_CLAUSE, Features.GROUP_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}

		if (!getFeatures().featureMap.containsKey(Feature.HAVING_CLAUSE)) {
			Features childFeatures = new Features(getFeatures());
			childFeatures.featureMap.put(Feature.HAVING_CLAUSE, Features.HAVING_CLAUSE.toString());
			children.add(new SpabTwoCandidate(childFeatures));
		}

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
		return new RegEx(getFeatures()).generate();
	}
}