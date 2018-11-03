package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.InputRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;

public class WebHandler extends AbstractHandler {

	@Override
	public void handle() throws WebserverIoException {

		// Check static (GET) or dynamic (POST)

		boolean isStatic = getHttpExchange().getRequestMethod().toUpperCase().equals("GET");

		// Get user parameters

		Map<String, String> parameters = null;
		try {
			parameters = getParameters();
		} catch (WebserverIoException e) {
			setInternalServerError("Error " + e.getMessage());
			return;
		}

		// Create HTML form

		String form = new String();
		try {
			form = getResource("templates/form.html");
			// TODO set user input
			String pos = getResource("data/positive.txt");
			String neg = getResource("data/negative.txt");
			form = form.replaceFirst("<!--POSITIVES-->", pos);
			form = form.replaceFirst("<!--NEGATIVES-->", neg);
			form = form.replaceFirst("<!--LAMBDA-->", "0.1");
			form = form.replaceFirst("<!--ITERATIONNS-->", "100");
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
		if (errors.isEmpty()) {
			try {
				StringBuilder stringBuilder = new StringBuilder();
				spabApi.run();
				stringBuilder.append("<h2>Best candidates</h2>");
				stringBuilder.append("<ul>");
				for (CandidateVertex cv : spabApi.getBestCandidates()) {
					stringBuilder.append("<li>" + StringEscapeUtils.escapeHtml4(cv.getInfoLine()) + "</li>");
				}
				stringBuilder.append("</ul>");
				stringBuilder.append("<a href=\"javascript:history.back()\">Go back</a>");
				setOkWithBody(stringBuilder.toString());
				return;
			} catch (SpabException e) {
				// TODO: Check errors, return HTML
				setInternalServerError("Error " + e.getMessage());
				return;
			}

		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<h2>Input errors</h2>");
			stringBuilder.append("<ul>");
			for (String error : errors) {
				stringBuilder.append("<li>" + error + "</li>");
			}
			stringBuilder.append("</ul>");
			stringBuilder.append("<a href=\"javascript:history.back()\">Go back</a>");

			// TODO: Other return type?
			setOkWithBody(stringBuilder.toString());
			return;
		}
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
			for (String query : queryBlock.split("(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)*")) {
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
			for (String query : queryBlock.split("(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)*")) {
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
