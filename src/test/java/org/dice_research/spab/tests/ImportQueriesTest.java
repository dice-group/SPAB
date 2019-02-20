package org.dice_research.spab.tests;

import org.dice_research.spab.AbstractTestCase;
import org.dice_research.spab.SpabApi;

/**
 * Tests import of example queries.
 * 
 * @author Adrian Wilke
 */
public class ImportQueriesTest extends AbstractTestCase {

	public void test() {

		SpabApi spabApi;

		spabApi = new SpabApi();
		for (String query : ImportFilesTest.getDbpediaAskQueries()) {
			spabApi.addPositive(query);
		}
		int commentedOut = 6;
		assertTrue(spabApi.getInput().getPositives().size() == 100 - commentedOut);

		spabApi = new SpabApi();
		for (String query : ImportFilesTest.getDbpediaConstructQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 100);

		spabApi = new SpabApi();
		for (String query : ImportFilesTest.getDbpediaDescribeQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 25);

		spabApi = new SpabApi();
		for (String query : ImportFilesTest.getDbpediaSelectQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 100);
	}
}