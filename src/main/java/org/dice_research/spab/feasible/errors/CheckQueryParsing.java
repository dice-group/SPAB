package org.dice_research.spab.feasible.errors;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.dice_research.spab.feasible.files.FeasibleFileAccesor;
import org.dice_research.spab.input.SparqlQuery;

/**
 * Generates SPAB queries for all FEASIBLE queries.
 * 
 * During that, exceptions may be thrown or
 * 
 * loggers may print warnings or errors.
 * 
 * Assumes directories are set by system properties
 * {@link FeasibleFileAccesor#SYSTEM_KEY_QUERIES} and
 * {@link FeasibleFileAccesor#SYSTEM_KEY_RESULTS}.
 * 
 * Reason for this class: Parsing errors are not thrown as Exceptions, but
 * logged. With the output of this class, parsing errors can be determined.
 * 
 * @author Adrian Wilke
 */
public class CheckQueryParsing {

	// Set to ~50 to keep order of console lines
	protected final static long SLEEP_MILLIS = 0;

	// Set invert to only display defective queries
	protected final static boolean INVERT_OUTPUT = false;

	protected FeasibleFileAccesor feasibleFileAccesor;

	int checkedSparqlQueries = 0;
	int checkedLineRepresentations = 0;

	public CheckQueryParsing() throws IOException {
		this.feasibleFileAccesor = new FeasibleFileAccesor();
	}

	public void run() throws FileNotFoundException, IOException, InterruptedException {

		for (int querytype = 0; querytype <= 4; querytype++) {
			for (int dataset = 0; dataset <= 1; dataset++) {
				allChecks(querytype, dataset);
			}
		}

		System.out.println("Checked SPARQL queries: " + checkedSparqlQueries);
		System.out.println("Checked line representations: " + checkedLineRepresentations);
	}

	public void allChecks(int querytype, int dataset) throws FileNotFoundException, IOException, InterruptedException {
		List<String> queries = feasibleFileAccesor.getQueries(querytype, dataset);
		System.out.println(
				"--- Checking " + queries.size() + " queries for type " + querytype + ", dataset " + dataset + " ---");
		allChecks(queries, querytype, dataset);
	}

	@SuppressWarnings("unused")
	public void allChecks(List<String> queryStrings, int querytype, int dataset) throws InterruptedException {
		int counter = 0;
		for (String queryString : queryStrings) {
			counter++;

			// Ignore defective queries
			boolean isParseDefective = DefectiveQueries.getParse(querytype, dataset).contains(counter);
			boolean isSpabDefective = DefectiveQueries.getSpab(querytype, dataset).contains(counter);
			if ((isParseDefective || isSpabDefective) && !INVERT_OUTPUT) {
				continue;
			} else if ((!isParseDefective && !isSpabDefective) && INVERT_OUTPUT) {
				continue;
			}

			System.out.println(counter + " "
					+ queryString.replaceAll("\\r?\\n|\\r", " ").substring(0, Math.min(200, queryString.length())));
			SparqlQuery sparqlQuery = checkSparqlQuery(queryString);
			checkLineRepresentation(sparqlQuery);
			Thread.sleep(SLEEP_MILLIS);
		}
	}

	public SparqlQuery checkSparqlQuery(String queryString) {
		checkedSparqlQueries++;
		return new SparqlQuery(queryString, null);
	}

	public String checkLineRepresentation(SparqlQuery sparqlQuery) {
		checkedLineRepresentations++;
		return sparqlQuery.getLineRepresentation();
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		new CheckQueryParsing().run();
	}
}