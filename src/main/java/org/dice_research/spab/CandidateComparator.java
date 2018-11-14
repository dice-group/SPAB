package org.dice_research.spab;

import java.util.Comparator;

import org.dice_research.spab.structures.CandidateVertex;

public class CandidateComparator implements Comparator<CandidateVertex> {

	/**
	 * @see {@link Comparator#compare(Object, Object)}:
	 */
	@Override
	public int compare(CandidateVertex candidateVertexA, CandidateVertex candidateVertexB) {

		// Inversed sorting: Better candidates have higher scores. Better candidates
		// should return smaller values.

		// Scores are most important, as they include user specified lambda.

		int scores = Float.compare(candidateVertexB.getScore(), candidateVertexA.getScore());
		if (scores != 0) {
			return scores;
		}

		// fMeasures are important.

		int fmeasures = Float.compare(candidateVertexB.getfMeasure(), candidateVertexA.getfMeasure());
		if (fmeasures != 0) {
			return fmeasures;
		}

		// Use higher candidate generation to choose more specific results.

		int generations = Integer.compare(candidateVertexB.getGeneration(), candidateVertexA.getGeneration());
		if (generations != 0) {
			return generations;
		}

		// Ensure replicability/deteminism/same result for every run.
		// Do not return equal result a.k.a. 0.

		// In last step, use candidates, which have been generated first.
		// It is assumed, that the refinement operator generates those earlier.

		return Integer.compare(candidateVertexA.getNumber(), candidateVertexB.getNumber());

	}

}
