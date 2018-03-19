package org.dice_research.spab.candidates.two;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * SPARQL features based on SPARQL grammar.
 * 
 * @see https://www.w3.org/TR/2013/REC-sparql11-query-20130321/#sparqlGrammar
 * 
 * @author Adrian Wilke
 */
public class Features {

	/**
	 * Feature identifiers
	 */
	public static enum Feature {
		GROUP_CLAUSE, HAVING_CLAUSE, ORDER_CLAUSE, TYPE, WHERE_CLAUSE
	}

	public static enum WhereClause {
		WHERE, WHERE_2_TRIPLES, WHERE_3_TRIPLES, WHERE_4_TRIPLES, WHERE_RESOURCES
	}

	public static final String GROUP_CLAUSE = "GROUP BY";
	public static final String HAVING_CLAUSE = "HAVING";
	public static final String ORDER_CLAUSE = "ORDER BY";
	public static final String[] TYPE_QUERIES = { "SELECT", "CONSTRUCT", "DESCRIBE", "ASK" };
	public static final String[] UPDATES = { "LOAD", "CLEAR", "DROP", "ADD", "MOVE", "COPY", "CREATE", "INSERT DATA",
			"DELETE DATA", "DELETE WHERE" };

	/**
	 * Map of features.
	 */
	public SortedMap<Feature, String> featureMap = new TreeMap<Feature, String>();

	/**
	 * Resources used in WHERE clause.
	 */
	private List<String> resourcesWhereClause;

	/**
	 * Creates empty set of Features.
	 */
	public Features() {
	}

	/**
	 * Creates set of features using predefined values.
	 */
	public Features(Features features) {
		this.featureMap.putAll(features.featureMap);
	}

	/**
	 * Gets resources used in WHERE clause.
	 */
	public List<String> getResources() {
		return resourcesWhereClause;
	}

	/**
	 * Sets resources used in WHERE clause.
	 */
	public void setResources(List<String> resources) {
		this.resourcesWhereClause = resources;
	}
}