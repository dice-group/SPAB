package org.dice_research.spab.feasible;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.benchmark.InputSets;
import org.dice_research.spab.feasible.enumerations.Dataset;
import org.dice_research.spab.feasible.enumerations.QueryType;
import org.dice_research.spab.feasible.enumerations.Triplestore;

public class ExperimentResult {

	enum InputSetsCreationType {
		STDDEV, PERCENTUAL, SIZE;

		public String getShortHand() {
			if (this.equals(STDDEV)) {
				return "STD";
			} else if (this.equals(PERCENTUAL)) {
				return "PER";
			} else if (this.equals(SIZE)) {
				return "SIZ";
			} else {
				return "???";
			}
		}
	}

	QueryType queryType;
	Dataset dataset;

	InputSetsCreationType inputSetsCreationType;
	float inputSetsCreationTypeArgument;
	InputSets inputSets;
	public List<Triplestore> triplestores;

	public List<SpabApi> spabApis = new LinkedList<>();

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(queryType.getShortHand());
		stringBuilder.append(" ");
		stringBuilder.append(dataset.getShortHand());
		stringBuilder.append(" | ");
		stringBuilder.append(inputSetsCreationType.getShortHand() + " " + inputSetsCreationTypeArgument);
		stringBuilder.append(" | ");
		for (Triplestore triplestore : triplestores) {
			stringBuilder.append(triplestore.name().substring(0, 3));
			stringBuilder.append(" ");
			stringBuilder.append(inputSets.getPositives(triplestore.getCsvHeader()).size());
			stringBuilder.append("/");
			stringBuilder.append(inputSets.getNegatives(triplestore.getCsvHeader()).size());
			stringBuilder.append(", ");
		}
		for (SpabApi spabApi : spabApis) {
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(" ");
			stringBuilder.append(spabApi.getBestCandidates().get(0).getInfoLine());
		}
		return stringBuilder.toString();
	}
}