package org.aksw.spab;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.SpabApi;
import org.junit.Test;

public class QueryFormatTest extends SpabTestCase {

	public static final String QUERY0 = "SELECT ?subject ?object WHERE {?subject ?predicate ?object}";
	public static final String QUERY1 = "SELECT ?object ?subject WHERE {?subject ?predicate ?object}";

	@Test
	public void testStandardizedQueries() {

		// Put queries in list to navigate by indexes
		List<String> queries = new LinkedList<String>();
		queries.add(QUERY0);
		queries.add(QUERY1);

		// Put queries in SPAB
		SpabApi spabApi = new SpabApi();
		for (String query : queries) {
			spabApi.addPositive(query);
		}

		// WHERE clause should be the same
		String where0 = spabApi.getInput().getPositives().get(0).getStringRepresentation();
		where0 = where0.substring(where0.indexOf("WHERE"));
		String where1 = spabApi.getInput().getPositives().get(1).getStringRepresentation();
		where1 = where1.substring(where1.indexOf("WHERE"));
		assertTrue(where0.equals(where1));

		// Print result
		if (PRINT) {
			for (int i = 0; i < queries.size(); i++) {
				System.out.println("Input: " + queries.get(i));
				System.out.println("Used:  " + spabApi.getInput().getPositives().get(i));
				System.out.println();
			}
		}
	}

}
