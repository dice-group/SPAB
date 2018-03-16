package org.dice_research.spab.candidates.two;

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
		TYPE, WHERE_CLAUSE, GROUP_CLAUSE, HAVING_CLAUSE, ORDER_CLAUSE
	}

	/**
	 * SPARQL query prefixes
	 * 
	 * @see SPARQL grammar https://www.w3.org/TR/sparql11-query/#rQuery
	 */
	public static final String[] _002_QUERIES = { "SELECT", "CONSTRUCT", "DESCRIBE", "ASK" };

	public static enum WhereClause {
		WHERE, WHERE_2_TRIPLES, WHERE_3_TRIPLES, WHERE_4_TRIPLES
	}

	public static final String _019_GROUP_CLAUSE = "GROUP BY";
	public static final String _021_HAVING_CLAUSE = "HAVING";
	public static final String _023_ORDER_CLAUSE = "ORDER BY";

	/**
	 * SPARQL update prefixes
	 * 
	 * @see SPARQL grammar https://www.w3.org/TR/sparql11-query/#rUpdate1
	 */
	public static final String[] _30_UPDATES = { "LOAD", "CLEAR", "DROP", "ADD", "MOVE", "COPY", "CREATE",
			"INSERT DATA", "DELETE DATA", "DELETE WHERE" };;

	/**
	 * Map of features.
	 */
	public SortedMap<Feature, String> featureMap = new TreeMap<Feature, String>();

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
}