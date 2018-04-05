package org.dice_research.spab.candidates.two;

import java.util.List;
import java.util.SortedMap;
import java.util.regex.Pattern;

import org.dice_research.spab.Statistics;
import org.dice_research.spab.candidates.two.Features.Feature;

/**
 * Generation of regular expressions based on features.
 * 
 * @author Adrian Wilke
 */
public class RegEx {

	protected SortedMap<Feature, String> featureMap;
	protected List<String> whereResources;
	protected String regExCache;

	public RegEx(Features features) {
		featureMap = features.featureMap;
		whereResources = features.resourcesWhereClause;
	}

	public String generate() {
		if (regExCache == null) {
			long time = System.currentTimeMillis();

			StringBuilder regEx = new StringBuilder();
			regEx.append(".*");

			// Type, e.g. SELECT
			if (featureMap.containsKey(Feature.TYPE)) {
				regEx.append(featureMap.get(Feature.TYPE));
				regEx.append(".*");
			}

			if (featureMap.containsKey(Feature.WHERE_CLAUSE)) {
				if (featureMap.get(Feature.WHERE_CLAUSE).equals(Features.WhereClause.WHERE_RESOURCES.toString())) {
					regEx.append("WHERE.*");
					regEx.append("\\{.*");
					for (String resource : whereResources) {
						// TODO: Build an AND
						// regEx.append("(?=");
						regEx.append(Pattern.quote(resource));
						// regEx.append(")");
						regEx.append(".*");
					}
					regEx.append("\\}.*");
				} else if (featureMap.get(Feature.WHERE_CLAUSE)
						.equals(Features.WhereClause.WHERE_2_TRIPLES.toString())) {
					regEx.append("WHERE.*");
					regEx.append("\\{.*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\}.*");
				} else if (featureMap.get(Feature.WHERE_CLAUSE)
						.equals(Features.WhereClause.WHERE_3_TRIPLES.toString())) {
					regEx.append("WHERE.*");
					regEx.append("\\{.*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\}.*");
				} else if (featureMap.get(Feature.WHERE_CLAUSE)
						.equals(Features.WhereClause.WHERE_4_TRIPLES.toString())) {
					regEx.append("WHERE.*");
					regEx.append("\\{.*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\ \\.\\ .*");
					regEx.append("\\}.*");
				} else {
					regEx.append("WHERE.*");
					regEx.append("\\{.*");
					regEx.append("\\}.*");
				}
			}

			if (featureMap.containsKey(Feature.GROUP_CLAUSE)) {
				regEx.append(featureMap.get(Feature.GROUP_CLAUSE));
				regEx.append(".*");
			}

			if (featureMap.containsKey(Feature.HAVING_CLAUSE)) {
				regEx.append(featureMap.get(Feature.HAVING_CLAUSE));
				regEx.append(".*");
			}

			if (featureMap.containsKey(Feature.ORDER_CLAUSE)) {
				regEx.append(featureMap.get(Feature.ORDER_CLAUSE));
				regEx.append(".*");
			}

			Statistics.addRegExStats(time, System.currentTimeMillis());
			regExCache = regEx.toString();
		}
		return regExCache;
	}
}