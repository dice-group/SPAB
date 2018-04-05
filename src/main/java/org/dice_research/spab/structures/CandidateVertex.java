package org.dice_research.spab.structures;

import java.util.HashMap;
import java.util.Map;

import org.dice_research.spab.Matcher;
import org.dice_research.spab.Statistics;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.exceptions.PerfectSolutionException;
import org.dice_research.spab.input.Configuration;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.input.SparqlUnit;

/**
 * Candidate for a comprehensive SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class CandidateVertex implements Matcher {

	public final static int START_GENERATION = 0;

	protected Candidate candidate;
	protected Float fMeasureCache = null;
	protected int generation;
	protected Input input;
	protected CandidateVertex parent;
	protected Float score = null;

	protected int truePositives = 0;
	protected int trueNegatives = 0;
	protected int falsePositives = 0;
	protected int falseNegatives = 0;

	/**
	 * Cache: Query matches regular expression of this candidate
	 */
	protected Map<String, Boolean> matcherCache = new HashMap<String, Boolean>();

	/**
	 * Initializes candidate, which has no parent.
	 * 
	 * Stores the {@link Candidate}.
	 */
	public CandidateVertex(Candidate candidate, Input input) {
		this(null, candidate, input);
	}

	/**
	 * Initializes candidate by setting generation depending on parent.
	 * 
	 * Stores the {@link Candidate}.
	 */
	public CandidateVertex(CandidateVertex parent, Candidate candidate, Input input) {
		if (parent == null) {
			generation = START_GENERATION;
		} else {
			generation = parent.getGeneration() + 1;
		}

		this.parent = parent;
		this.candidate = candidate;
		this.input = input;
	}

	/**
	 * Calculates score of this candidate.
	 * 
	 * @throws PerfectSolutionException
	 *             if candidate has no false positives or false negatives
	 */
	public void calculateScore(Configuration configuration, int maxDepth, Matcher matcher)
			throws PerfectSolutionException {
		long time = System.currentTimeMillis();

		boolean firstCall = false;

		// Input and matching-method never changes. f-Measure can be cached.
		if (fMeasureCache == null) {

			firstCall = true;

			long timeMatching = System.currentTimeMillis();
			for (SparqlUnit sparqlUnit : input.getPositives()) {
				if (matcher.matches(candidate, sparqlUnit.getLineRepresentation())) {
					truePositives++;
				} else {
					falseNegatives++;
				}
			}
			for (SparqlUnit sparqlUnit : input.getNegatives()) {
				if (matcher.matches(candidate, sparqlUnit.getLineRepresentation())) {
					falsePositives++;
				} else {
					trueNegatives++;
				}
			}
			Statistics.addMatchingStats(timeMatching, System.currentTimeMillis());

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
				fMeasureCache = 0f;
			} else {
				fMeasureCache = 2f * ((precision * recall) / (precision + recall));
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
		score = (1f - configuration.getLambda()) * fMeasureCache + configuration.getLambda() * (1f - lengthRelation);

		// At first call of this method: Check for perfect solution
		if (configuration.isPerfectSolutionChecked() && firstCall && falsePositives == 0 && falseNegatives == 0) {
			throw new PerfectSolutionException(this);
		}

		Statistics.addCalcScoreStats(time, System.currentTimeMillis());
	}

	/**
	 * Generates children for this candidate.
	 * 
	 * @throws CandidateRuntimeException
	 *             on Exceptions in {@link Candidate} implementations
	 */
	public Map<CandidateVertex, Candidate> generateChildren() throws CandidateRuntimeException {
		Map<CandidateVertex, Candidate> map = new HashMap<CandidateVertex, Candidate>();
		for (Candidate candidate : this.candidate.getChildren(getInput())) {
			map.put(new CandidateVertex(this, candidate, input), candidate);
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
		return fMeasureCache;
	}

	/**
	 * Gets generation of this candidate.
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Gets input of SPAB run.
	 */
	public Input getInput() {
		return this.input;
	}

	/**
	 * Gets parent vertex.
	 */
	public CandidateVertex getParent() {
		return parent;
	}

	/**
	 * Gets score of candidate. Score has to be calculated by
	 * {@link CandidateVertex#calculateScore(Input)}.
	 */
	public float getScore() {
		return score;
	}

	/**
	 * Checks, if the candidates regular expression and the query are matching.
	 * 
	 * Uses cache. Assumes that regular expression of candidates never change.
	 */
	public boolean matches(Candidate candidate, String query) throws CandidateRuntimeException {
		String cachingKey = candidate.getRegEx() + "|" + query;
		if (!matcherCache.containsKey(cachingKey)) {
			matcherCache.put(cachingKey, query.matches(candidate.getRegEx()));
		}
		return matcherCache.get(cachingKey);
	}

	public int getNumberOfTruePositives() {
		return truePositives;
	}

	public int getNumberOfFalsePositives() {
		return falsePositives;
	}

	public int getNumberOfFalseNegatives() {
		return falseNegatives;
	}

	public int getNumberOfTrueNegatives() {
		return trueNegatives;
	}
}