package org.dice_research.spab.benchmark;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Container for input sets.
 * 
 * @author Adrian Wilke
 */
public class InputSets {

	/**
	 * <TripleStoreId, <positive or negative set, queries>>
	 */
	private Map<String, Map<Boolean, List<Query>>> triplestoreSettypeQueries;

	/**
	 * Initializes data structures.
	 */
	public InputSets(List<String> tripleStoreIds) {

		triplestoreSettypeQueries = new HashMap<String, Map<Boolean, List<Query>>>();

		for (String tripleStoreId : tripleStoreIds) {
			triplestoreSettypeQueries.put(tripleStoreId, new HashMap<Boolean, List<Query>>());
			Map<Boolean, List<Query>> triplestoreMap = triplestoreSettypeQueries.get(tripleStoreId);
			triplestoreMap.put(Boolean.valueOf(true), new LinkedList<Query>());
			triplestoreMap.put(Boolean.valueOf(false), new LinkedList<Query>());
		}
	}

	/**
	 * Adds a query to the positive or negative set of a triple store.
	 */
	public void addQuery(String tripleStoreId, boolean isPositiveSet, Query query) {
		triplestoreSettypeQueries.get(tripleStoreId).get(isPositiveSet).add(query);
	}

	/**
	 * Gets set of queries.
	 */
	public List<Query> get(boolean getPositiveSet, String tripleStoreId) {
		return triplestoreSettypeQueries.get(tripleStoreId).get(getPositiveSet);
	}

	/**
	 * Gets positive set of queries.
	 */
	public List<Query> getPositives(String tripleStoreId) {
		return triplestoreSettypeQueries.get(tripleStoreId).get(true);
	}

	/**
	 * Gets negative set of queries.
	 */
	public List<Query> getNegatives(String tripleStoreId) {
		return triplestoreSettypeQueries.get(tripleStoreId).get(false);
	}
}