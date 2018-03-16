package org.dice_research.spab;

import org.dice_research.spab.candidates.two.Features;
import org.dice_research.spab.candidates.two.Features.Feature;
import org.dice_research.spab.candidates.two.RegEx;
import org.junit.Before;
import org.junit.Test;

/**
 * Test generation of regular expressions based on features.
 * 
 * @author Adrian Wilke
 */
public class RegExTest extends AbstractTestCase {

	final static public boolean PRINT = false;

	protected String askWhere = "ASK WHERE { <http://dbpedia.org/resource/Connecticut> ?y ?z }";
	protected String selectWhere = "SELECT ?subject ?object WHERE {?subject ?predicate ?object}";
	protected String selectWhere2Triples = "SELECT ?subject ?object WHERE {?subject ?predicate ?object . ?object ?predicate ?subject}";

	/**
	 * Parses queries and transforms them in input format.
	 */
	@Override
	@Before
	public void setUp() {
		SpabApi spabApi;

		spabApi = new SpabApi();
		spabApi.addPositive(askWhere);
		askWhere = spabApi.getInput().getPositives().get(0).getLineRepresentation();

		spabApi = new SpabApi();
		spabApi.addPositive(selectWhere);
		selectWhere = spabApi.getInput().getPositives().get(0).getLineRepresentation();

		spabApi = new SpabApi();
		spabApi.addPositive(selectWhere2Triples);
		selectWhere2Triples = spabApi.getInput().getPositives().get(0).getLineRepresentation();

		if (PRINT) {
			System.out.println(askWhere);
			System.out.println(selectWhere);
			System.out.println(selectWhere2Triples);
		}
	}

	@Test
	public void test() {

		// Root candidate, should match all queries
		Features features = new Features();
		RegEx regEx = new RegEx(features);
		assertTrue(selectWhere.matches(regEx.generate()));

		// add SELECT
		features.featureMap.put(Feature.TYPE, Features._002_QUERIES[0]);
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(selectWhere.matches(regEx.generate()));
		assertFalse(askWhere.matches(regEx.generate()));

		// add WHERE clause
		features.featureMap.put(Feature.WHERE_CLAUSE, Features.WhereClause.WHERE.toString());
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(selectWhere.matches(regEx.generate()));

		// Only WHERE
		features = new Features();
		features.featureMap.put(Feature.WHERE_CLAUSE, Features.WhereClause.WHERE.toString());
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertTrue(askWhere.matches(regEx.generate()));
		assertTrue(selectWhere.matches(regEx.generate()));

		// WHERE with two triples
		features = new Features();
		features.featureMap.put(Feature.WHERE_CLAUSE, Features.WhereClause.WHERE_2_TRIPLES.toString());
		regEx = new RegEx(features);
		if (PRINT) {
			System.out.println(regEx.generate());
		}
		assertFalse(askWhere.matches(regEx.generate()));
		assertFalse(selectWhere.matches(regEx.generate()));
		assertTrue(selectWhere2Triples.matches(regEx.generate()));
	}
}