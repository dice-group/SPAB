package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.input.SparqlUnit;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Information strings.
 *
 * @author Adrian Wilke
 */
public abstract class InfoStrings {

	public static String getAll(SpabApi spabApi, int maxNumberOfBestCandidates) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getAllInput(spabApi));
		stringBuilder.append(getAllOutput(spabApi, maxNumberOfBestCandidates));

		return stringBuilder.toString();
	}

	public static String getAllInput(SpabApi spabApi) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getInputSets(spabApi, true));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(getInputSets(spabApi, false));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(getConfiguration(spabApi));
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}

	public static String getAllOutput(SpabApi spabApi, int maxNumberOfBestCandidates) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getBestCandidates(spabApi, maxNumberOfBestCandidates));
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append(getSummary(spabApi));
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}

	public static String getInputSets(SpabApi spabApi, boolean positiveSet) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append((positiveSet ? "Positive" : "Negative") + " set of SPARQL queries:");
		stringBuilder.append(System.lineSeparator());
		for (SparqlUnit unit : positiveSet ? spabApi.getInput().getPositives() : spabApi.getInput().getNegatives()) {
			stringBuilder.append(unit.getLineRepresentation());
			stringBuilder.append(System.lineSeparator());
		}

		return stringBuilder.toString();
	}

	public static String getConfiguration(SpabApi spabApi) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Configuration:");
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Lambda:                 ");
		stringBuilder.append(spabApi.getConfiguration().getLambda());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Max iterations:         ");
		stringBuilder.append(spabApi.getConfiguration().getMaxIterations());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Implementation:         ");
		stringBuilder.append(spabApi.getConfiguration().getCandidateImplementation());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Check perfect solution: ");
		stringBuilder.append(spabApi.getConfiguration().isPerfectSolutionChecked() ? "True" : "False");
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}

	public static String getBestCandidates(SpabApi spabApi, int max) {
		StringBuilder stringBuilder = new StringBuilder();

		List<CandidateVertex> bestCandidates = spabApi.getBestCandidates();
		int number = Math.min(max, bestCandidates.size());
		stringBuilder.append("First " + number + " best candidates:");
		stringBuilder.append(System.lineSeparator());

		for (int i = 0; i < number; i++) {
			stringBuilder.append(bestCandidates.get(i).getInfoLine());
			stringBuilder.append(System.lineSeparator());
		}

		return stringBuilder.toString();
	}

	public static String getSummary(SpabApi spabApi) {
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("Generated candidates (graph): ");
		stringBuilder.append(spabApi.getGraph().getAllCandidates().size());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Refined candidates   (stack): ");
		stringBuilder.append(spabApi.getStack().size());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Remaining candidates (queue): ");
		stringBuilder.append(spabApi.getQueue().getQueue().size());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Generated generations:        ");
		stringBuilder.append(spabApi.getGraph().getDepth());
		stringBuilder.append(System.lineSeparator());

		stringBuilder.append("Runtime in seconds:           ");
		stringBuilder.append(spabApi.getRuntime());
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}
}
