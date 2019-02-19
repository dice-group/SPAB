package org.dice_research.spab.feasible.errors;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dice_research.spab.feasible.files.FeasibleFileAccesor;

/**
 * Handles defective queries (which e.g. cause parsing errors).
 * 
 * Data is set in static block.
 * 
 * Data can be accessed by public get methods.
 * 
 * @author Adrian Wilke
 */
public abstract class DefectiveQueries {

	/**
	 * Querytype -> Dataset -> Index
	 */
	private static Map<Integer, Map<Integer, List<Integer>>> defectiveQueriesParse;

	/**
	 * Querytype -> Dataset -> Index
	 */
	private static Map<Integer, Map<Integer, List<Integer>>> defectiveQueriesSpab;

	static {

		// Create objects

		defectiveQueriesParse = new HashMap<>();
		for (int querytype = 1; querytype <= 5; querytype++) {
			Map<Integer, List<Integer>> map = new HashMap<>();
			defectiveQueriesParse.put(querytype, map);
			for (int dataset = 1; dataset <= 2; dataset++) {
				map.put(dataset, new LinkedList<>());
			}
		}
		defectiveQueriesSpab = new HashMap<>();
		for (int querytype = 1; querytype <= 5; querytype++) {
			Map<Integer, List<Integer>> map = new HashMap<>();
			defectiveQueriesSpab.put(querytype, map);
			for (int dataset = 1; dataset <= 2; dataset++) {
				map.put(dataset, new LinkedList<>());
			}
		}

		// Set data

		put(false, 1, 1, 18);
		put(false, 1, 1, 26);
		put(false, 1, 1, 37);
		put(false, 1, 1, 38);
		put(false, 1, 1, 40);
		put(false, 1, 1, 63);
		put(false, 5, 1, 44);
		put(false, 5, 1, 172);

		put(true, 1, 2, 4);
		put(true, 1, 2, 5);
		put(true, 1, 2, 8);
		put(true, 1, 2, 32);
		put(true, 1, 2, 40);
		put(true, 2, 2, 4);
		put(true, 2, 2, 23);
		put(true, 3, 1, 21);
		put(true, 3, 1, 25);
	}

	private static void put(boolean spab, Integer querytype, Integer dataset, int number) {
		if (spab) {
			defectiveQueriesSpab.get(querytype).get(dataset).add(number);
		} else {
			defectiveQueriesParse.get(querytype).get(dataset).add(number);
		}
	}

	/**
	 * Gets defective query indices (type SPAB).
	 * 
	 * @param querytype Index specified in {@link FeasibleFileAccesor}
	 * @param dataset   Index specified in {@link FeasibleFileAccesor}
	 * @return list of defective query indices.
	 */
	public static List<Integer> getSpab(Integer querytype, Integer dataset) {
		return defectiveQueriesSpab.get(querytype).get(dataset);
	}

	/**
	 * Gets defective query indices (type parse).
	 * 
	 * @param querytype Index specified in {@link FeasibleFileAccesor}
	 * @param dataset   Index specified in {@link FeasibleFileAccesor}
	 * @return list of defective query indices.
	 */
	public static List<Integer> getParse(Integer querytype, Integer dataset) {
		return defectiveQueriesParse.get(querytype).get(dataset);
	}
}
