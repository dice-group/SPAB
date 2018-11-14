package org.dice_research.spab.webdemo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.InfoStrings;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.InputRuntimeException;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Processes SPAB execution.
 *
 * @author Adrian Wilke
 */
public class SpabHandler extends AbstractHandler {

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
			form = getForm(isStatic, parameters);
		} catch (Exception e) {
			setInternalServerError(e);
			return;
		}

		// Serve only static content

		if (isStatic) {
			setOkWithBody("<h2>Specify the input parameters</h2>" + form);
			return;
		}

		// Check an run

		SpabApi spabApi = new SpabApi();
		List<String> errors = checkSpabInput(spabApi, parameters);

		StringBuilder stringBuilder = new StringBuilder();
		if (errors.isEmpty()) {
			// Run

			try {
				CandidateVertex bestCandidate = spabApi.run();

				stringBuilder.append("<h2>Results</h2>");

				stringBuilder.append("<h3>Summary</h3>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<ul>");
				stringBuilder.append("<li>Scores calculated for <b>" + spabApi.getGraph().getAllCandidates().size()
						+ "</b> candidates</li>");
				stringBuilder.append("<li>Best candidate F-measure: <b>" + bestCandidate.getfMeasure() + "</b></li>");
				stringBuilder.append("<li>Best candidate final score: <b>" + bestCandidate.getScore()
						+ "</b> (includes weighting of the position in the graph)</li>");
				stringBuilder.append("<li>Best candidate regular expression: <b><code>"
						+ StringEscapeUtils.escapeHtml4(bestCandidate.getCandidate().getRegEx()) + "</code></b></li>");
				stringBuilder.append("</ul>");
				stringBuilder.append(System.lineSeparator());

				stringBuilder.append("<h3>Explore best candidates</h3>");
				stringBuilder.append("<div id=\"cy\"></div>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<pre id=\"cydata\">Please select a candidate.</pre>");
				stringBuilder.append(System.lineSeparator());

				stringBuilder.append("<h3>Overview of candidates</h3>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<pre>");
				stringBuilder.append(StringEscapeUtils.escapeHtml4(InfoStrings.getAllOutput(spabApi, 30)));
				stringBuilder.append("</pre>");
				stringBuilder.append(System.lineSeparator());

				stringBuilder.append("<h3>Input in canonical form</h3>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<pre>");
				stringBuilder.append(StringEscapeUtils.escapeHtml4(InfoStrings.getAllInput(spabApi)));
				stringBuilder.append("</pre>");
				stringBuilder.append(System.lineSeparator());

				// TODO: Whats the best number of candidates to display?
				int maxBestCandidateVertices = 30;
				GraphConstructor graphConstructor = new GraphConstructor();
				String graphTemplate = getResource(Templates.GRAPH);
				graphTemplate = graphTemplate.replace(Templates.GRAPH_MARKER_ELEMENTS,
						graphConstructor.construct(spabApi, maxBestCandidateVertices));
				graphTemplate = graphTemplate.replace(Templates.GRAPH_MARKER_MAX,
						"" + graphConstructor.getMaxValue());
				graphTemplate = graphTemplate.replace(Templates.GRAPH_MARKER_MIN,
						"" + graphConstructor.getMinValue());
				stringBuilder.append(graphTemplate);

				stringBuilder.append(System.lineSeparator());

			} catch (Exception e) {
				// TODO: Check errors, return HTML
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

			// TODO: Other return type?
		}

		stringBuilder.append("<h2>Start a new run</h2>");
		stringBuilder.append(form);

		stringBuilder.append("<p><a href=\"javascript:history.back()\">Back to previous page</a></p>");

		setOkWithBody(stringBuilder.toString());
		return;
	}

	/**
	 * Checks SPAB input parameters.
	 * 
	 * Instead of throwing exceptions, error messages are returned.
	 */
	protected List<String> checkSpabInput(SpabApi spabApi, Map<String, String> parameters) {
		List<String> errors = new LinkedList<String>();

		// Check SPARQL queries

		String setType;
		String queryBlock;

		setType = "positive";
		queryBlock = parameters.get(setType + "s");
		if (queryBlock == null) {
			errors.add("No " + setType + " set of SPARQL queries specified.");
		} else {
			// Split by at least 3 line breaks
			for (String query : queryBlock.split("(\\r?\\n)(\\r?\\n)(\\r?\\n)+")) {
				try {
					if (setType.equals("positive")) {
						spabApi.addPositive(query);
					} else {
						spabApi.addNegative(query);
					}
				} catch (InputRuntimeException e) {
					errors.add("Could not parse " + setType + " query: <br />" + "<code>"
							+ StringEscapeUtils.escapeHtml4(query) + "</code>");
				}
			}
		}

		setType = "negative";
		queryBlock = parameters.get(setType + "s");
		if (queryBlock == null) {
			errors.add("No " + setType + " set of SPARQL queries specified.");
		} else {
			// Split by at least 3 line breaks
			for (String query : queryBlock.split("(\\r?\\n)(\\r?\\n)(\\r?\\n)+")) {
				try {
					if (setType.equals("positive")) {
						spabApi.addPositive(query);
					} else {
						spabApi.addNegative(query);
					}
				} catch (InputRuntimeException e) {
					errors.add("Could not parse " + setType + " query: <br />" + "<code>"
							+ StringEscapeUtils.escapeHtml4(query) + "</code>");
				}
			}
		}

		// Lambda
		// 0 <= Lambda < 1 is checked in
		// {@link Configuration#setLambda(float)}
		String lambda = parameters.get("lambda");
		if (lambda == null) {
			errors.add("No lambda specified.");
		} else {
			try {
				spabApi.setLambda(Float.valueOf(lambda));
			} catch (NumberFormatException e) {
				errors.add("Incorrect format for lambda: " + lambda);
			} catch (InputRuntimeException e) {
				errors.add(e.getMessage());
			}
		}

		// Iterations
		String iterations = parameters.get("iterations");
		if (iterations == null) {
			errors.add("No number of iterations specified.");
		} else {
			try {
				Integer it = Integer.valueOf(iterations);
				if (it > 1000) {
					errors.add("Max number of iterations in web demo is 1000.");
				} else if (it < 0) {
					errors.add("<a target=\"_blank\" "
							+ "href=\"https://goo.gl/78LgBK\">You found an easter egg! Get you reward</a>");
				}
				spabApi.setMaxIterations(it);
			} catch (NumberFormatException e) {
				errors.add("Incorrect format for maximum number of iterations: " + iterations);
			}
		}

		// TODO: spabApi.setCheckPerfectSolution(checkPerfectSolution);

		return errors;
	}

}
