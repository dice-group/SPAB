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

	private int generation;
	private Float score = null;
	private Float fMeasure = null;

	/**
	 * Initializes candidate by setting generation depending on parent.
	 */
	public Candidate(Candidate parent) {
		if (parent == null) {
			generation = 1;
		} else {
			generation = parent.getGeneration() + 1;
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
	 * Checks, if regular expression of candidate matches query string.
	 */
	public boolean matches(String query) {

		// TODO: Somewhat more deterministic, please ...
		if (Math.random() < .3) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Calculates score of this candidate.
	 * 
	 * @throws PerfectSolutionException
	 *             if candidate has no false positives or false negatives
	 */
	public void calculateScore(Input input, int maxDepth) throws PerfectSolutionException {

		// Input and matching-method never changes. f-Measure can be cached.
		if (fMeasure == null) {

			int truePositives = 0;
			int falsePositives = 0;
			int falseNegatives = 0;

			for (InputQuery inputQuery : input.getPositives()) {
				if (matches(inputQuery.getQueryWithoutPrefixes())) {
					truePositives++;
				} else {
					falseNegatives++;
				}
			}
			for (InputQuery inputQuery : input.getNegatives()) {
				if (matches(inputQuery.getQueryWithoutPrefixes())) {
					falsePositives++;
				}
			}

			if (falsePositives == 0 && falseNegatives == 0) {
				fMeasure = 1f;
				score = 1f;
				throw new PerfectSolutionException(this);
			}

			float precision;
			float precisionDenomiator = truePositives + falsePositives;
			if (precisionDenomiator == 0f) {
				precision = 1f;
			} else {
				precision = truePositives / precisionDenomiator;
			}

			float recall;
			float recallDenominator = truePositives + falseNegatives;
			if (recallDenominator == 0f) {
				recall = 1f;
			} else {
				recall = truePositives / recallDenominator;
			}

			if (precision + recall == 0f) {
				fMeasure = 0f;
			} else {
				fMeasure = 2f * ((precision * recall) / (precision + recall));
			}
		}

		float lengthRelation = getGeneration() / maxDepth;
		score = (1f - input.getLambda()) * fMeasure + input.getLambda() * (1f - lengthRelation);
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
}