package org.dice_research.spab.feasible.errors;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.dice_research.spab.feasible.files.FeasibleFileAccesor;
import org.dice_research.spab.input.SparqlQuery;

/**
 * Generates SPAB queries for all FEASIBLE queries.
 * 
 * During that, exceptions may be thrown or loggers may print warnings or
 * errors.
 * 
 * Reason for this class: Parsing errors are not thrown as Exceptions, but
 * logged. With the output of this class, parsing errors can be determined.
 * 
 * @author Adrian Wilke
 */
public class CheckQueryParsing {

	// Print single queries and numbers (indexes+1)
	protected final static boolean PRINT_QUERIES = true;

	// Set to ~50 to keep order of console lines
	protected final static long SLEEP_MILLIS = 0;

	// Skip (un-)defective queries
	// If false, all queries are checked
	protected final static boolean SKIP_QUERIES = true;

	// true: Display only defective queries
	// false: No errors should be displayed
	protected final static boolean INVERT_OUTPUT = false;

	protected FeasibleFileAccesor feasibleFileAccesor;

	int queriesRead = 0;
	int checkedSparqlQueries = 0;
	int checkedLineRepresentations = 0;

	public CheckQueryParsing() throws IOException {
		this.feasibleFileAccesor = new FeasibleFileAccesor();
	}

	public CheckQueryParsing(File directoryQueries, File directoryResults) throws IOException {
		this.feasibleFileAccesor = new FeasibleFileAccesor(directoryQueries, directoryResults);
	}

	public void run() throws FileNotFoundException, IOException, InterruptedException {

		for (int querytype = 0; querytype <= 4; querytype++) {
			for (int dataset = 0; dataset <= 1; dataset++) {
				allChecks(querytype, dataset);
			}
		}

		System.out.println("Read SPARQL queries: " + queriesRead);
		System.out.println("Checked SPARQL queries: " + checkedSparqlQueries);
		System.out.println("Checked line representations: " + checkedLineRepresentations);
	}

	public void allChecks(int querytype, int dataset) throws FileNotFoundException, IOException, InterruptedException {
		List<String> queries = feasibleFileAccesor.getQueries(querytype, dataset);
		queriesRead += queries.size();
		System.out.println(
				"--- Checking " + queries.size() + " queries for type " + querytype + ", dataset " + dataset + " ---");
		allChecks(queries, querytype, dataset);
	}

	@SuppressWarnings("unused")
	public void allChecks(List<String> queryStrings, int querytype, int dataset) throws InterruptedException {
		int counter = -1;
		for (String queryString : queryStrings) {
			counter++;

			// Ignore defective queries
			if (SKIP_QUERIES) {
				boolean isParseDefective = DefectiveQueries.getParse(querytype, dataset).contains(counter);
				boolean isSpabDefective = DefectiveQueries.getSpab(querytype, dataset).contains(counter);
				if ((isParseDefective || isSpabDefective) && !INVERT_OUTPUT) {
					continue;
				} else if ((!isParseDefective && !isSpabDefective) && INVERT_OUTPUT) {
					continue;
				}
			}
			if (PRINT_QUERIES) {
				System.out.println(counter + " "
						+ queryString.replaceAll("\\r?\\n|\\r", " ").substring(0, Math.min(200, queryString.length())));
			}
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

	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		try {
			// Uses system properties in {@link FeasibleFileAccesor#FeasibleFileAccesor()}
			new CheckQueryParsing().run();
		} catch (IOException e) {
			// Uses arguments in {@link FeasibleFileAccesor#FeasibleFileAccesor(File, File)}
			new CheckQueryParsing(new File(args[0]), new File(args[1])).run();
		}
	}
}