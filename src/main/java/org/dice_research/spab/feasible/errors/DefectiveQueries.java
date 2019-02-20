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
		for (int querytype = 0; querytype <= 4; querytype++) {
			Map<Integer, List<Integer>> map = new HashMap<>();
			defectiveQueriesParse.put(querytype, map);
			for (int dataset = 0; dataset <= 1; dataset++) {
				map.put(dataset, new LinkedList<>());
			}
		}
		defectiveQueriesSpab = new HashMap<>();
		for (int querytype = 0; querytype <= 4; querytype++) {
			Map<Integer, List<Integer>> map = new HashMap<>();
			defectiveQueriesSpab.put(querytype, map);
			for (int dataset = 0; dataset <= 1; dataset++) {
				map.put(dataset, new LinkedList<>());
			}
		}

		// Set data

		put(false, 0, 0, 18 - 1);
		put(false, 0, 0, 26 - 1);
		put(false, 0, 0, 37 - 1);
		put(false, 0, 0, 38 - 1);
		put(false, 0, 0, 40 - 1);
		put(false, 0, 0, 63 - 1);
		put(false, 4, 0, 44 - 1);
		put(false, 4, 0, 172 - 1);

		put(true, 0, 1, 4 - 1);
		put(true, 0, 1, 5 - 1);
		put(true, 0, 1, 8 - 1);
		put(true, 0, 1, 32 - 1);
		put(true, 0, 1, 40 - 1);
		put(true, 1, 1, 4 - 1);
		put(true, 1, 1, 23 - 1);
		put(true, 2, 0, 21 - 1);
		put(true, 2, 0, 25 - 1);
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
	 * @return list of defective query numbers (index+1).
	 */
	public static List<Integer> getSpab(Integer querytype, Integer dataset) {
		return defectiveQueriesSpab.get(querytype).get(dataset);
	}

	/**
	 * Gets defective query indices (type parse).
	 * 
	 * @param querytype Index specified in {@link FeasibleFileAccesor}
	 * @param dataset   Index specified in {@link FeasibleFileAccesor}
	 * @return list of defective query numbers (index+1).
	 */
	public static List<Integer> getParse(Integer querytype, Integer dataset) {
		return defectiveQueriesParse.get(querytype).get(dataset);
	}
}
