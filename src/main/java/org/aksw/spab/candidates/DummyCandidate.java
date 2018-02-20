package org.aksw.spab.candidates;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * Non-deterministic and non-realistic candidate implementation.
 * 
 * @author Adrian Wilke
 */
public class DummyCandidate implements Candidate {

	final public int CHILDREN_MAX = 3;
	final public int CHILDREN_MIN = 1;
	final public double MATCHING_PROBABILITY = .5;

	/**
	 * Generates dummy children between {@link CHILDREN_MIN} and
	 * {@link CHILDREN_MAX}
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {
		try {

			List<Candidate> list = new LinkedList<Candidate>();
			int numberOfCandidates = ThreadLocalRandom.current().nextInt(CHILDREN_MIN, CHILDREN_MAX + 1);
			for (int i = 0; i < numberOfCandidates; i++) {
				Candidate candidate = new DummyCandidate();
				list.add(candidate);
			}
			return list;

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}

	/**
	 * Returns null.
	 */
	public String getRexEx() throws CandidateRuntimeException {
		try {

			return null;

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}

	/**
	 * Matches with a probability of {@link MATCHING_PROBABILITY}
	 */
	public boolean matches(String query) throws CandidateRuntimeException {
		try {

			if (Math.random() < MATCHING_PROBABILITY) {
				return true;
			} else {
				return false;
			}

		} catch (Exception e) {
			throw new CandidateRuntimeException(e);
		}
	}
}