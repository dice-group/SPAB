package org.dice_research.spab;

import org.dice_research.spab.candidates.two.Features;
import org.dice_research.spab.candidates.two.Features.Feature;
import org.dice_research.spab.candidates.two.RegEx;
import org.junit.Test;

/**
 * Test generation of regular expressions based on features.
 * 
 * @author Adrian Wilke
 */
public class RegExTest extends AbstractTestCase {

	final static public boolean PRINT = false;
	public static final String ASK_WHERE = "ASK WHERE { <http://dbpedia.org/resource/Connecticut> ?y ?z }";
	public static final String SELECT = "SELECT ?subject ?object";
	public static final String SELECT_WHERE = "SELECT ?subject ?object WHERE {?subject ?predicate ?object}";

	@Test
	public void test() {

		// Root candidate, should match all queries
		Features features = new Features();
		RegEx regEx = new RegEx(features);
		assertTrue(SELECT_WHERE.matches(regEx.generate()));

		// add SELECT
		features.featureMap.put(Feature.TYPE, Features._002_QUERIES[0]);
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(SELECT_WHERE.matches(regEx.generate()));
		assertTrue(!ASK_WHERE.matches(regEx.generate()));

		// add WHERE clause
		features.featureMap.put(Feature.WHERE_CLAUSE, Features._017_WHERE_CLAUSE);
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(SELECT_WHERE.matches(regEx.generate()));
		assertTrue(!SELECT.matches(regEx.generate()));

		// Only WHERE
		features = new Features();
		features.featureMap.put(Feature.WHERE_CLAUSE, Features._017_WHERE_CLAUSE);
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(ASK_WHERE.matches(regEx.generate()));
		assertTrue(SELECT_WHERE.matches(regEx.generate()));
		assertTrue(!SELECT.matches(regEx.generate()));
	}
}