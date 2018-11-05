package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.InfoStrings;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.InputRuntimeException;
import org.dice_research.spab.exceptions.SpabException;

/**
 * Processes SPAB execution.
 *
 * @author Adrian Wilke
 */
public class WebHandler extends AbstractHandler {

	@Override
	public void handle() throws WebserverIoException {

		// Check static (GET) or dynamic (POST)

		boolean isStatic = getHttpExchange().getRequestMethod().toUpperCase().equals("GET");

		// Get user parameters

		Map<String, String> parameters = new HashMap<String, String>();
		try {
			fillParameters(parameters);
		} catch (WebserverIoException e) {
			setInternalServerError("Error " + e.getMessage());
			return;
		}

		// Create HTML form

		String form = new String();
		try {
			form = getForm(isStatic, parameters);
		} catch (IOException e) {
			setInternalServerError("Error " + e.getMessage());
			return;
		}

		// Serve only static content

		if (isStatic) {
			setOkWithBody(form);
			return;
		}

		// Check an run

		SpabApi spabApi = new SpabApi();
		List<String> errors = checkSpabInput(spabApi, parameters);

		StringBuilder stringBuilder = new StringBuilder();
		if (errors.isEmpty()) {
			// Run

			try {
				spabApi.run();

				stringBuilder.append("<div id=\"cy\"></div>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<p id=\"cydata\">Please select a candidate.</p>");
				stringBuilder.append(System.lineSeparator());

				stringBuilder.append("<h2>Results</h2>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<pre>");
				stringBuilder.append(StringEscapeUtils.escapeHtml4(InfoStrings.getAllOutput(spabApi, 30)));
				stringBuilder.append("</pre>");
				stringBuilder.append(System.lineSeparator());

				stringBuilder.append("<h2>Used input</h2>");
				stringBuilder.append(System.lineSeparator());
				stringBuilder.append("<pre>");
				stringBuilder.append(StringEscapeUtils.escapeHtml4(InfoStrings.getAllInput(spabApi)));
				stringBuilder.append("</pre>");
				stringBuilder.append(System.lineSeparator());

				// TODO
				int maxBestCandidateVertices = 30;
				stringBuilder.append(getResource(Templates.GRAPH).replaceFirst(Templates.GRAPH_MARKER_ELEMENTS,
						GraphConstructor.construct(spabApi, maxBestCandidateVertices)));

				stringBuilder.append(System.lineSeparator());

			} catch (SpabException e) {
				// TODO: Check errors, return HTML
				setInternalServerError("Error " + e.getMessage());
				return;
			} catch (IOException e) {
				setInternalServerError("Error " + e.getMessage());
				return;
			}

		} else {
			// Display errors

			stringBuilder.append("<h2>Input errors</h2>");
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
					errors.add("Could not parse " + setType + " query: " + StringEscapeUtils.escapeHtml4(query));
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
					errors.add("Could not parse " + setType + " query: " + StringEscapeUtils.escapeHtml4(query));
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
		// TODO: Never checked, i.e. negative values
		String iterations = parameters.get("iterations");
		if (iterations == null) {
			errors.add("No number of iterations specified.");
		} else {
			try {
				spabApi.setMaxIterations(Integer.valueOf(iterations));
			} catch (NumberFormatException e) {
				errors.add("Incorrect format for maximum number of iterations: " + iterations);
			}
		}

		// TODO: spabApi.setCheckPerfectSolution(checkPerfectSolution);

		return errors;
	}

}
