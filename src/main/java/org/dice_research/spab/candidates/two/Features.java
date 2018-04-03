package org.dice_research.spab.candidates.two;

import java.util.LinkedList;
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

	/**
	 * SPAQL unit: Query
	 */
	public static final String[] TYPE_QUERIES = { "SELECT", "CONSTRUCT", "DESCRIBE", "ASK" };

	/**
	 * SPAQL unit: Update
	 */
	public static final String[] UPDATES = { "LOAD", "CLEAR", "DROP", "ADD", "MOVE", "COPY", "CREATE", "INSERT DATA",
			"DELETE DATA", "DELETE WHERE" };

	/**
	 * Where features
	 */
	public static enum WhereClause {
		WHERE, WHERE_2_TRIPLES, WHERE_3_TRIPLES, WHERE_4_TRIPLES, WHERE_RESOURCES
	}

	public static final String GROUP_CLAUSE = "GROUP BY";
	public static final String HAVING_CLAUSE = "HAVING";
	public static final String ORDER_CLAUSE = "ORDER BY";

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
		this.resourcesWhereClause.addAll(features.resourcesWhereClause);
	}

	/**
	 * Map of features.
	 */
	public SortedMap<Feature, String> featureMap = new TreeMap<Feature, String>();

	/**
	 * Resources used in WHERE clause.
	 */
	public List<String> resourcesWhereClause = new LinkedList<String>();
}