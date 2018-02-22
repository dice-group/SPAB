package org.aksw.spab.structures;

import java.util.HashMap;
import java.util.Map;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;
import org.aksw.spab.exceptions.PerfectSolutionException;
import org.aksw.spab.input.Configuration;
import org.aksw.spab.input.Input;
import org.aksw.spab.input.SparqlQuery;

/**
 * Candidate for a comprehensive SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class CandidateVertex {

	public final static int START_GENERATION = 0;

	protected Candidate candidate;
	protected Float fMeasure = null;
	protected int generation;

	protected Float score = null;

	/**
	 * Initializes candidate, which has no parent.
	 * 
	 * Stores the {@link Candidate}.
	 */
	public CandidateVertex(Candidate candidate) {
		this(null, candidate);
	}

	/**
	 * Initializes candidate by setting generation depending on parent.
	 * 
	 * Stores the {@link Candidate}.
	 */
	public CandidateVertex(CandidateVertex parent, Candidate candidate) {
		if (parent == null) {
			generation = START_GENERATION;
		} else {
			generation = parent.getGeneration() + 1;
		}

		this.candidate = candidate;
	}

	/**
	 * Calculates score of this candidate.
	 * 
	 * @throws PerfectSolutionException
	 *             if candidate has no false positives or false negatives
	 */
	public void calculateScore(Input input, Configuration configuration, int maxDepth) throws PerfectSolutionException {

		boolean firstCall = false;
		int truePositives = 0;
		int falsePositives = 0;
		int falseNegatives = 0;

		// Input and matching-method never changes. f-Measure can be cached.
		if (fMeasure == null) {

			firstCall = true;

			for (SparqlQuery inputQuery : input.getPositives()) {
				if (matches(inputQuery.getQuery().toString())) {
					truePositives++;
				} else {
					falseNegatives++;
				}
			}
			for (SparqlQuery inputQuery : input.getNegatives()) {
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
		score = (1f - configuration.getLambda()) * fMeasure + configuration.getLambda() * (1f - lengthRelation);

		// At first call of this method: Check for perfect solution
		if (configuration.isPerfectSolutionChecked() && firstCall && falsePositives == 0 && falseNegatives == 0) {
			throw new PerfectSolutionException(this);
		}
	}

	/**
	 * Generates children for this candidate.
	 * 
	 * @throws CandidateRuntimeException
	 *             on Exceptions in {@link Candidate} implementations
	 */
	public Map<CandidateVertex, Candidate> generateChildren() throws CandidateRuntimeException {
		Map<CandidateVertex, Candidate> map = new HashMap<CandidateVertex, Candidate>();
		for (Candidate candidate : this.candidate.getChildren()) {
			map.put(new CandidateVertex(this, candidate), candidate);
		}
		return map;
	}

	/**
	 * Gets related candidate.
	 */
	public Candidate getCandidate() {
		return candidate;
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
	 * {@link CandidateVertex#calculateScore(Input)}.
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Checks, whether regular expression of candidate matches query string.
	 * 
	 * @throws CandidateRuntimeException
	 *             on Exceptions in {@link Candidate} implementations
	 */
	@SuppressWarnings("deprecation")
	public boolean matches(String query) throws CandidateRuntimeException {

		String candidateRegEx = candidate.getRexEx();
		if (candidateRegEx != null) {
			// Default implementation
			return query.matches(candidateRegEx);
		} else {
			// Fallback, e.g. used by {@link DummyCandidate}
			return candidate.matches(query);
		}
	}
}