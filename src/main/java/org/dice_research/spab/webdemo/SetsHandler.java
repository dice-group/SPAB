package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.benchmark.Benchmark;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.benchmark.InputSetsCreator;
import org.dice_research.spab.benchmark.Query;

public class SetsHandler extends AbstractHandler {

	protected final static int METHOD_STDDEV = 1;
	protected final static int METHOD_PERCENTACE = 2;
	protected final static int METHOD_MAXSIZE = 3;

	protected String triplestore = null;
	protected Boolean smallIsPositive = null;
	protected Integer genMethod = null;

	protected Double stdDevFactor = null;
	protected Double percentageDeviation = null;
	protected Integer maxNumberOfElements = null;

	@Override
	public void handle() throws WebserverIoException {

		// Get user parameters

		Map<String, String> parameters = new HashMap<String, String>();
		try {
			fillParameters(parameters);
		} catch (Exception e) {
			setInternalServerError(e);
			return;
		}

		// Check
		List<String> errors = checkParameters(parameters);

		// Create benchmark or display errors
		StringBuilder stringBuilder = new StringBuilder();
		if (errors.isEmpty()) {

			Benchmark benchmark = Benchmark
					.readJson(StringEscapeUtils.unescapeHtml4(parameters.get(Templates.SETS_ID_BENCHMARK)));
			InputSetsCreator inputSetsCreator = new InputSetsCreator(benchmark);
			InputSets inputSets;

			if (this.genMethod.equals(METHOD_STDDEV)) {
				inputSets = inputSetsCreator.createStandardDeviationSets(this.stdDevFactor, this.smallIsPositive);
			} else if (this.genMethod.equals(METHOD_PERCENTACE)) {
				inputSets = inputSetsCreator.createPercentualSets(this.percentageDeviation, this.smallIsPositive);
			} else if (this.genMethod.equals(METHOD_MAXSIZE)) {
				inputSets = inputSetsCreator.createMaxSizeSets(this.maxNumberOfElements, this.smallIsPositive);
			} else {
				setInternalServerError("Could not find input set method. " + getClass().getSimpleName());
				return;
			}

			try {
				stringBuilder.append(
						getForm(inputSets.getPositives(this.triplestore), inputSets.getNegatives(this.triplestore)));
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
	 * Prepares form template
	 */
	protected String getForm(List<Query> positives, List<Query> negatives) throws IOException {

		StringBuilder pos = new StringBuilder();
		boolean isFirst = true;
		for (Query query : positives) {
			if (isFirst) {
				isFirst = false;
			} else {
				pos.append(System.lineSeparator());
				pos.append(System.lineSeparator());
				pos.append(System.lineSeparator());
			}
			pos.append(query.getQueryString());
		}

		StringBuilder neg = new StringBuilder();
		isFirst = true;
		for (Query query : negatives) {
			if (isFirst) {
				isFirst = false;
			} else {
				neg.append(System.lineSeparator());
				neg.append(System.lineSeparator());
				neg.append(System.lineSeparator());
			}
			neg.append(query.getQueryString());
		}

		String form = new String();
		form = getResource(Templates.FORM);

		form = form.replaceFirst(Templates.FORM_MARKER_POSITIVES, pos.toString());
		form = form.replaceFirst(Templates.FORM_MARKER_NEGATIVES, neg.toString());
		form = form.replaceFirst(Templates.FORM_MARKER_LAMBDA, "0.1");
		form = form.replaceFirst(Templates.FORM_MARKER_ITERATIONNS, "100");

		return form;
	}

	/**
	 * Checks parameters and fills object variables on success.
	 */
	protected List<String> checkParameters(Map<String, String> parameters) {
		List<String> errors = new LinkedList<String>();
		String parameter;

		String triplestore = null;
		parameter = parameters.get(Templates.SETS_ID_TRIPLESTORE).trim();
		if (parameter == null) {
			errors.add("Parameter not set: Triplestore");
		} else if (parameter.isEmpty()) {
			errors.add("Parameter not set: Triplestore");
		} else {
			triplestore = parameter;
		}

		Boolean smallIsPositive = null;
		parameter = parameters.get(Templates.SETS_ID_SMALLISPOSITIVE).trim();
		if (parameter == null) {
			errors.add("Parameter not set: Small-is-Positive");
		} else if (parameter.equals("true")) {
			smallIsPositive = true;
		} else if (parameter.equals("false")) {
			smallIsPositive = false;
		} else {
			errors.add("Parameter unknown: Small-is-Positive");
		}

		Integer genMethod = null;
		Double stdDevFactor = null;
		Double percentageDeviation = null;
		Integer maxNumberOfElements = null;
		String methodInput = null;
		parameter = parameters.get(Templates.SETS_ID_METHOD).trim();
		if (parameter == null) {
			errors.add("Parameter not set: Method");

		} else if (parameter.equals("standarddeviation")) {
			genMethod = METHOD_STDDEV;
			methodInput = parameters.get(Templates.SETS_ID_STDDEV).trim();
			try {
				stdDevFactor = Double.parseDouble(methodInput);
				// TODO: Check range
			} catch (NumberFormatException e) {
				errors.add("Can not parse floating point value (double) for standdard deviation: " + methodInput);
			}

		} else if (parameter.equals("percentage")) {
			genMethod = METHOD_PERCENTACE;
			methodInput = parameters.get(Templates.SETS_ID_PERSENTACE).trim();
			try {
				percentageDeviation = Double.parseDouble(methodInput);
				// TODO: Check range
			} catch (NumberFormatException e) {
				errors.add("Can not parse floating point value (double) for percentage deviation: " + methodInput);
			}

		} else if (parameter.equals("maxsize")) {
			genMethod = METHOD_MAXSIZE;
			methodInput = parameters.get(Templates.SETS_ID_MAXSIZE).trim();
			try {
				maxNumberOfElements = Integer.parseInt(methodInput);
				// TODO: Check range
			} catch (NumberFormatException e) {
				errors.add("Can not parse max number of elements: " + methodInput);
			}

		} else {
			errors.add("Parameter unknown: Method");
		}

		if (errors.isEmpty()) {
			this.triplestore = triplestore;
			this.smallIsPositive = smallIsPositive;
			this.genMethod = genMethod;
			this.stdDevFactor = stdDevFactor;
			this.percentageDeviation = percentageDeviation;
			this.maxNumberOfElements = maxNumberOfElements;
		}

		return errors;
	}

}
