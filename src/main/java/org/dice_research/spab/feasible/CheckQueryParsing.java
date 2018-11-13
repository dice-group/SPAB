package org.dice_research.spab.feasible;

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
 * @author Adrian Wilke
 */
public class CheckQueryParsing {

	protected FeasibleFileAccesor feasibleFileAccesor;

	int checkedSparqlQueries = 0;
	int checkedLineRepresentations = 0;

	public CheckQueryParsing() throws IOException {
		this.feasibleFileAccesor = new FeasibleFileAccesor();
	}

	public void run() throws FileNotFoundException, IOException {

		for (int querytype = 1; querytype <= 5; querytype++) {
			for (int dataset = 1; dataset <= 2; dataset++) {
				allChecks(querytype, dataset);
			}
		}

		System.out.println("Checked SPARQL queries: " + checkedSparqlQueries);
		System.out.println("Checked line representations: " + checkedLineRepresentations);
	}

	public void allChecks(int querytype, int dataset) throws FileNotFoundException, IOException {
		List<String> queries = feasibleFileAccesor.getQueries(querytype, dataset);
		System.out.println(
				"--- Checking " + queries.size() + " queries for type " + querytype + ", dataset " + dataset + " ---");
		allChecks(queries);
	}

	public void allChecks(List<String> queryStrings) {
		int counter = 1;
		for (String queryString : queryStrings) {
			System.out.println(counter + " "
					+ queryString.replaceAll("\\r?\\n|\\r", " ").substring(0, Math.min(100, queryString.length())));
			SparqlQuery sparqlQuery = checkSparqlQuery(queryString);
			checkLineRepresentation(sparqlQuery);
			counter++;
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

	public static void main(String[] args) throws IOException {
		new CheckQueryParsing().run();
	}
}