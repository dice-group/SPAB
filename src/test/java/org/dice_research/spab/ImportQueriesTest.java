package org.dice_research.spab;

/**
 * Tests import of example queries.
 * 
 * @author Adrian Wilke
 */
public class ImportQueriesTest extends AbstractTestCase {

	public void test() {

		ImportFilesTest importFilesTest = new ImportFilesTest();
		SpabApi spabApi;

		spabApi = new SpabApi();
		for (String query : importFilesTest.getDbpediaAskQueries()) {
			spabApi.addPositive(query);
		}
		int commentedOut = 6;
		assertTrue(spabApi.getInput().getPositives().size() == 100 - commentedOut);

		spabApi = new SpabApi();
		for (String query : importFilesTest.getDbpediaConstructQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 100);

		spabApi = new SpabApi();
		for (String query : importFilesTest.getDbpediaDescribeQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 25);

		spabApi = new SpabApi();
		for (String query : importFilesTest.getDbpediaSelectQueries()) {
			spabApi.addPositive(query);
		}
		assertTrue(spabApi.getInput().getPositives().size() == 100);
	}
}