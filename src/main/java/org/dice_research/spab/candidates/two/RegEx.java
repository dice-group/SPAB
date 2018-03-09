package org.dice_research.spab.candidates.two;

import java.util.SortedMap;

import org.dice_research.spab.candidates.two.Features.Feature;

/**
 * Generation of regular expressions based on features.
 * 
 * @author Adrian Wilke
 */
public class RegEx {

	protected SortedMap<Feature, String> featureMap;

	public RegEx(Features features) {
		featureMap = features.featureMap;
	}

	public String generate() {
		StringBuilder regEx = new StringBuilder();
		regEx.append(".*");

		// Type, e.g. SELECT
		if (featureMap.containsKey(Feature.TYPE)) {
			regEx.append(featureMap.get(Feature.TYPE));
			regEx.append(".*");
		}

		// Where clause
		if (featureMap.containsKey(Feature.WHERE_CLAUSE)) {
			regEx.append(featureMap.get(Feature.WHERE_CLAUSE));
			regEx.append(".*");
			regEx.append("\\{");
			regEx.append(".*");
			regEx.append("\\}");
			regEx.append(".*");
		}

		return regEx.toString();
	}
}