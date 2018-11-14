package org.dice_research.spab.examples;

import java.util.List;

import org.dice_research.spab.InfoStrings;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.SpabApi.CandidateImplementation;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.six.CandidateSix;
import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.Resources;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Example tests SPAB algorithm.
 * 
 * @author Adrian Wilke
 */
public class SpabExample {

	/**
	 * Configuration: Number of iterations. Test run with 1000 iterations took about
	 * 9 seconds
	 */
	public static final float LAMBDA = .0f;

	/**
	 * Configuration: Number of iterations. Test run with 1000 iterations took about
	 * 9 seconds
	 */
	public static final int MAX_ITERATIONS = 100;

	/**
	 * Configuration: Use Fuseki or Virtuoso file.
	 */
	public static final boolean USE_FUSEKI = true;

	/**
	 * Configuration: Number of SPARQL queries added to set of negative examples.
	 */
	public static final int NUMBER_OF_NEGATIVES = 10;

	/**
	 * Configuration: Number of SPARQL queries added to set of positives examples.
	 */
	public static final int NUMBER_OF_POSITIVES = 10;

	public static final String RESOURCE_IGUANA_FUSEKI_NEGATIVE = "iguana-2018-01-20/Fuseki-negative.txt";
	public static final String RESOURCE_IGUANA_FUSEKI_POSITIVE = "iguana-2018-01-20/Fuseki-positive.txt";

	public static final String RESOURCE_IGUANA_VIRTUOSO_NEGATIVE = "iguana-2018-01-20/Virtuoso-negative.txt";
	public static final String RESOURCE_IGUANA_VIRTUOSO_POSITIVE = "iguana-2018-01-20/Virtuoso-positive.txt";

	public static void main(String[] args) throws SpabException {
		new SpabExample().run();
	}

	public SpabApi run() throws SpabException {

		String negFile = RESOURCE_IGUANA_VIRTUOSO_NEGATIVE;
		String posFile = RESOURCE_IGUANA_VIRTUOSO_POSITIVE;
		if (USE_FUSEKI) {
			negFile = RESOURCE_IGUANA_FUSEKI_NEGATIVE;
			posFile = RESOURCE_IGUANA_FUSEKI_POSITIVE;
		}

		// Deprication is allowed, as this example will not be used in generated jar.
		@SuppressWarnings("deprecation")
		List<String> negatives = FileReader.readFileToList(Resources.getResource(negFile).getPath(), true,
				FileReader.UTF8);
		@SuppressWarnings("deprecation")
		List<String> positives = FileReader.readFileToList(Resources.getResource(posFile).getPath(), true,
				FileReader.UTF8);

		SpabApi spabApi = new SpabApi();

		int n = NUMBER_OF_NEGATIVES;
		for (String query : negatives) {
			spabApi.addNegative(query);
			if (--n == 0) {
				break;
			}
		}

		int p = NUMBER_OF_POSITIVES;
		for (String query : positives) {
			spabApi.addPositive(query);
			if (--p == 0) {
				break;
			}
		}

		spabApi.setLambda(LAMBDA);
		spabApi.setMaxIterations(MAX_ITERATIONS);
		spabApi.setCheckPerfectSolution(true);
		spabApi.setCandidateImplementation(CandidateImplementation.SPAB_SIX);

		System.out.println(InfoStrings.getAllInput(spabApi));

		@SuppressWarnings("unused")
		CandidateVertex bestCandidate = spabApi.run();

		System.out.print(InfoStrings.getAllOutput(spabApi, 30));

		int candidateToShow = 4;
		if (spabApi.getBestCandidates().size() >= candidateToShow) {
			printInternalRepresentation(spabApi.getBestCandidates().get(candidateToShow).getCandidate());
		}

		return spabApi;
	}

	/**
	 * Prints sequence of {@link CandidateSix} instance.
	 */
	void printInternalRepresentation(Candidate<?> candidate) {
		if (candidate instanceof CandidateSix) {

			@SuppressWarnings("unchecked")
			Candidate<Expression> castedCandidate = (Candidate<Expression>) candidate;
			Expression expression = castedCandidate.getInternalRepresentation(Expression.class);

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Hierarchy:");
			stringBuilder.append(System.lineSeparator());
			expression.getHierarchy(stringBuilder);
			System.out.println(stringBuilder.toString());
		}
	}
}