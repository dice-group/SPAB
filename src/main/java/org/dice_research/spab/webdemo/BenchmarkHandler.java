package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.BenchmarkNullException;
import org.dice_research.spab.benchmark.Query;
import org.dice_research.spab.benchmark.Result;
import org.dice_research.spab.benchmark.TripleStore;
import org.dice_research.spab.exceptions.InputRuntimeException;

public class BenchmarkHandler extends AbstractHandler {

	SortedMap<Integer, String> queryMap;
	List<CSVRecord> recordList;

	@Override
	public void handle() throws WebserverIoException {

		// Check static (GET) or dynamic (POST)

		boolean isStatic = getHttpExchange().getRequestMethod().toUpperCase().equals("GET");

		// Get user parameters

		Map<String, String> parameters = new HashMap<String, String>();
		try {
			fillParameters(parameters);
		} catch (Exception e) {
			setInternalServerError(e);
			return;
		}

		// Create HTML form

		String form = new String();
		try {
			form = getResource(Templates.BENCHMARK);
			if (isStatic) {
				form = form.replace(Templates.BENCHMARK_MARKER_QUERIES, getResource("data/benchmark_queries.txt"));
				form = form.replace(Templates.BENCHMARK_MARKER_RESULTS, getResource("data/benchmark_results.tsv"));
			} else {
				String parameter = parameters.get(Templates.BENCHMARK_ID_QUERIES);
				form = form.replace(Templates.BENCHMARK_MARKER_QUERIES, parameter == null ? "" : parameter);
				parameter = parameters.get(Templates.BENCHMARK_ID_RESULTS);
				form = form.replace(Templates.BENCHMARK_MARKER_RESULTS, parameter == null ? "" : parameter);
			}
		} catch (Exception e) {
			setInternalServerError(e);
			return;
		}

		// Serve only static content

		if (isStatic) {
			setOkWithBody("<h2>Specify the input parameters</h2>" + form
					+ "<p><a href=\"javascript:history.back()\">Back to previous page</a></p>");
			return;
		}

		// Check
		List<String> errors = checkParameters(parameters);

		// Create benchmark or display errors
		StringBuilder stringBuilder = new StringBuilder();
		if (errors.isEmpty()) {

			Benchmark benchmark = null;
			SortedMap<String, TripleStore> benchmarkTriplestoreMap = null;
			try {

				benchmark = new Benchmark("SPAB webdemo benchmark");

				// Queries
				SortedMap<String, Query> benchmarkQueryMap = new TreeMap<String, Query>();
				for (Entry<Integer, String> queryEntry : queryMap.entrySet()) {
					benchmarkQueryMap.put(queryEntry.getKey().toString(),
							benchmark.addQuery(queryEntry.getKey().toString(), queryEntry.getValue()));
				}

				// Triplestores
				benchmarkTriplestoreMap = new TreeMap<String, TripleStore>();
				for (CSVRecord csvRecord : recordList) {
					String triplestoreId = csvRecord.get(1).trim();
					if (!benchmarkTriplestoreMap.containsKey(triplestoreId)) {
						benchmarkTriplestoreMap.put(triplestoreId, benchmark.addTripleStore(triplestoreId));
					}
				}

				// Benchmark results
				for (CSVRecord csvRecord : recordList) {
					int queryId = Integer.parseInt(csvRecord.get(0).trim());
					String triplestoreId = csvRecord.get(1).trim();
					double runtime = Double.parseDouble(csvRecord.get(2).trim());

					benchmark.addResult(benchmarkTriplestoreMap.get(triplestoreId),
							benchmarkQueryMap.get(Integer.toString(queryId)), runtime);
				}
			} catch (BenchmarkNullException e) {
				throw new WebserverIoException(e);
			}

			stringBuilder.append("<h2>Check parsed input</h2>");
			stringBuilder.append("<p>Please check if the identified data is correct.<br />");
			stringBuilder.append(
					"Afterwards, you can correct your input data or create input sets at the bottom of this page.</p>");

			stringBuilder.append("<h3>SPARQL Queries</h3>");
			StringBuilder queryHtmlBuilder = new StringBuilder();
			queryHtmlBuilder.append("<table id=\"benchmark-table\">");
			queryHtmlBuilder.append("<tr>");
			queryHtmlBuilder.append("<th nowrap>Query ID</th>");
			queryHtmlBuilder.append("<th>Query</th>");
			queryHtmlBuilder.append("</tr>");

			for (Query benchmarkQuery : benchmark.getQueries()) {
				queryHtmlBuilder.append("<tr>");

				queryHtmlBuilder.append("<td>");
				queryHtmlBuilder.append(benchmarkQuery.getQueryId());
				queryHtmlBuilder.append("</td>");

				queryHtmlBuilder.append("<td>");
				queryHtmlBuilder.append(StringEscapeUtils.escapeHtml4(benchmarkQuery.getQueryString()));
				queryHtmlBuilder.append("</td>");

				queryHtmlBuilder.append("</tr>");
			}
			queryHtmlBuilder.append("</table>");
			stringBuilder.append(queryHtmlBuilder);

			stringBuilder.append("<h3>Benchmark results</h3>");
			StringBuilder benchmarkHtmlBuilder = new StringBuilder();
			benchmarkHtmlBuilder.append("<table id=\"benchmark-table\">");
			benchmarkHtmlBuilder.append("<tr>");
			benchmarkHtmlBuilder.append("<th nowrap>Query ID</th>");
			benchmarkHtmlBuilder.append("<th>Triplestore ID</th>");
			benchmarkHtmlBuilder.append("<th>Result</th>");
			benchmarkHtmlBuilder.append("</tr>");

			for (Result benchmarkResult : benchmark.getResults()) {
				benchmarkHtmlBuilder.append("<tr>");

				benchmarkHtmlBuilder.append("<td>");
				benchmarkHtmlBuilder.append(benchmarkResult.getQuery().getQueryId());
				benchmarkHtmlBuilder.append("</td>");

				benchmarkHtmlBuilder.append("<td>");
				benchmarkHtmlBuilder
						.append(StringEscapeUtils.escapeHtml4(benchmarkResult.getTripleStore().getTripleStoreId()));
				benchmarkHtmlBuilder.append("</td>");

				benchmarkHtmlBuilder.append("<td>");
				benchmarkHtmlBuilder.append(benchmarkResult.getResult());
				benchmarkHtmlBuilder.append("</td>");

				benchmarkHtmlBuilder.append("</tr>");
			}
			benchmarkHtmlBuilder.append("</table>");
			stringBuilder.append(benchmarkHtmlBuilder);

			// SPAB form
			stringBuilder.append("<h2>Correct data</h2>");
			stringBuilder.append(form);

			// Input sets form
			try {
				stringBuilder.append("<h2>Create input sets</h2>");
				String setsForm = getResource(Templates.SETS);

				// Triplestores
				StringBuilder tsBuilder = new StringBuilder();
				boolean isFirst = true;
				for (String triplestoreId : benchmarkTriplestoreMap.keySet()) {
					tsBuilder.append("<input type=\"radio\" name=\"triplestore\" id=\"" + triplestoreId + "\" value=\""
							+ triplestoreId + "\"");
					if (isFirst) {
						isFirst = false;
						tsBuilder.append(" checked=\"checked\">");
					} else {
						tsBuilder.append(">");
					}
					tsBuilder.append(
							"<label for=\"" + triplestoreId + "\" class=\"radiolabel\">" + triplestoreId + "</label>");
					tsBuilder.append("<br />");
				}
				setsForm = setsForm.replace(Templates.SETS_MARKER_TRIPLESTORE, tsBuilder.toString());

				// Benchmark
				setsForm = setsForm.replace(Templates.SETS_MARKER_BENCHMARK,
						StringEscapeUtils.escapeHtml4(benchmark.toJson()));
				stringBuilder.append(setsForm);
			} catch (Exception e) {
				setInternalServerError(e);
				return;
			}

		} else {
			// Display errors

			stringBuilder.append("<h2 class=\"error\">Input errors</h2>");
			stringBuilder.append("<ul>");
			for (String error : errors) {
				stringBuilder.append("<li>" + error + "</li>");
			}
			stringBuilder.append("</ul>");
		}

		stringBuilder.append("<p><a href=\"javascript:history.back()\">Back to previous page</a></p>");

		setOkWithBody(stringBuilder.toString());
		return;

	}

	/**
	 * Checks parameters and fills object variables on success.
	 */
	protected List<String> checkParameters(Map<String, String> parameters) {
		List<String> errors = new LinkedList<String>();
		SortedMap<Integer, String> queryMap = new TreeMap<Integer, String>();
		List<CSVRecord> records = null;

		// Check SPARQL queries

		String queries = parameters.get(Templates.BENCHMARK_ID_QUERIES);
		int queryCounter = 0;
		if (queries == null || queries.trim().isEmpty()) {
			errors.add("No SPARQL queries specified.");
		} else {
			// Split by at least 3 line breaks
			SpabApi spabApi = new SpabApi();
			for (String query : queries.split("(\\r?\\n)(\\r?\\n)(\\r?\\n)+")) {

				// Use SPAB to check SPARQL query
				queryCounter++;
				try {
					spabApi.addPositive(query);
				} catch (InputRuntimeException e) {
					errors.add("Could not parse query " + queryCounter + ":<br />" + "<code>"
							+ StringEscapeUtils.escapeHtml4(query) + "</code>");
				}

				queryMap.put(queryCounter, query.trim());
			}
		}

		String results = parameters.get(Templates.BENCHMARK_ID_RESULTS);
		if (results == null || results.trim().isEmpty()) {
			errors.add("No benchmark results specified.");
		} else {
			CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter('\t');
			CSVParser csvParser = null;
			try {
				csvParser = CSVParser.parse(results, csvFormat);
				records = csvParser.getRecords();

				if (records.size() < 4) {
					errors.add("Found " + records.size()
							+ " benchmark results. At least 4 results (2 queries x 2 Triplestores) are needed.");
					return errors;
				}

				int recordCounter = 0;
				for (CSVRecord csvRecord : records) {
					recordCounter++;
					if (csvRecord.size() != 3) {
						errors.add("Result " + recordCounter + " does not consist of 3 values (" + csvRecord.size()
								+ ").");
					} else {

						try {
							int queryId = Integer.parseInt(csvRecord.get(0).trim());
							if (queryId > queryMap.size()) {
								errors.add("Result " + recordCounter + " query ID is larger than the list of queries ("
										+ csvRecord.get(0) + ").");
							}
						} catch (NumberFormatException e) {
							errors.add("Result " + recordCounter + " query ID is not a valid number ("
									+ csvRecord.get(0) + ").");
						}

						if (csvRecord.get(1).trim().isEmpty()) {
							errors.add("Result " + recordCounter + " Triplestore ID is empty");
						}

						try {
							Double.parseDouble(csvRecord.get(2).trim());
						} catch (NumberFormatException e) {
							errors.add("Result " + recordCounter + " runtime is not a valid floating-point number ("
									+ csvRecord.get(2) + ").");
						}
					}

				}

			} catch (Exception e) {
				errors.add("Could not parse benchmark results: " + e.getMessage());
				return errors;
			} finally {
				try {
					csvParser.close();
				} catch (IOException e) {
					errors.add("Error closing CSV parser: " + e.getMessage());
					return errors;
				}
			}
		}

		if (errors.isEmpty()) {
			this.queryMap = queryMap;
			this.recordList = records;
		}

		return errors;
	}
}
