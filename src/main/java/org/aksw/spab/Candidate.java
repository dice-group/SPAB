package org.aksw.spab;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.aksw.spab.exceptions.PerfectSolutionException;
import org.aksw.spab.input.Input;
import org.aksw.spab.input.InputQuery;

/**
 * Candidate for a comprehensive SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class Candidate {

	public final static int START_GENERATION = 0;

	protected Float fMeasure = null;
	protected int generation;
	protected Float score = null;

	/**
	 * Initializes candidate, which has no parent
	 */
	public Candidate() {
		this(null);
	}

	/**
	 * Initializes candidate by setting generation depending on parent.
	 */
	public Candidate(Candidate parent) {
		if (parent == null) {
			generation = START_GENERATION;
		} else {
			generation = parent.getGeneration() + 1;
		}
	}

	/**
	 * Calculates score of this candidate.
	 * 
	 * @throws PerfectSolutionException
	 *             if candidate has no false positives or false negatives
	 */
	public void calculateScore(Input input, int maxDepth) throws PerfectSolutionException {

		boolean firstCall = false;
		int truePositives = 0;
		int falsePositives = 0;
		int falseNegatives = 0;

		// Input and matching-method never changes. f-Measure can be cached.
		if (fMeasure == null) {

			firstCall = true;

			for (InputQuery inputQuery : input.getPositives()) {
				if (matches(inputQuery.getQuery().toString())) {
					truePositives++;
				} else {
					falseNegatives++;
				}
			}
			for (InputQuery inputQuery : input.getNegatives()) {
				if (matches(inputQuery.getQuery().toString())) {
					falsePositives++;
				}
			}

			float precision;
			float precisionDenomiator = truePositives + falsePositives;
			// If division by zero, set fraction to 1
			if (precisionDenomiator == 0f) {
				precision = 1f;
			} else {
				precision = truePositives / precisionDenomiator;
			}

			float recall;
			float recallDenominator = truePositives + falseNegatives;
			// If division by zero, set fraction to 1
			if (recallDenominator == 0f) {
				recall = 1f;
			} else {
				recall = truePositives / recallDenominator;
			}

			// If no precision and no recall given, set F-measure to 0
			if (precision == 0f && recall == 0f) {
				fMeasure = 0f;
			} else {
				fMeasure = 2f * ((precision * recall) / (precision + recall));
			}
		}

		// Calculate score
		float lengthRelation;
		// If only one node in graph, set maximum bonus
		if (maxDepth == 0) {
			lengthRelation = 0;
		} else {
			lengthRelation = getGeneration() / maxDepth;
		}
		score = (1f - input.getLambda()) * fMeasure + input.getLambda() * (1f - lengthRelation);

		// At first call of this method: Check for perfect solution
		if (input.isPerfectSolutionChecked() && firstCall && falsePositives == 0 && falseNegatives == 0) {
			throw new PerfectSolutionException(this);
		}
	}

	/**
	 * Generates children for this candidate.
	 */
	public List<Candidate> generateChildren() {

		List<Candidate> list = new LinkedList<Candidate>();

		// TODO: Please, get more realistic.
		int numberOfCandidates = ThreadLocalRandom.current().nextInt(1, 3 + 1);
		for (int i = 0; i < numberOfCandidates; i++) {
			Candidate candidate = new Candidate(this);
			list.add(candidate);
		}
		return list;
	}

	/**
	 * Gets the F-measure (F-score, F1 score) of candidate
	 */
	public Float getfMeasure() {
		return fMeasure;
	}

	/**
	 * Gets generation of this candidate.
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Gets score of candidate. Score has to be calculated by
	 * {@link Candidate#calculateScore(Input)}.
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Checks, if regular expression of candidate matches query string.
	 */
	public boolean matches(String query) {

		// TODO: Somewhat more deterministic, please ...
		if (Math.random() < .5) {
			return true;
		} else {
			return false;
		}
	}
}