package org.aksw.spab;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Priority queue for candidates. Priority is calculated via the getScore method
 * of candidates.
 * 
 * @author Adrian Wilke
 */
public class CandidateQueue {

	protected PriorityQueue<Candidate> queue;

	/**
	 * Initializes queue.
	 */
	public CandidateQueue() {
		queue = new PriorityQueue<Candidate>(new Comparator<Candidate>() {
			public int compare(Candidate c1, Candidate c2) {
				return Float.compare(c2.getScore(), c1.getScore());
			}
		});
	}

	/**
	 * Inserts candidate into queue.
	 */
	public boolean add(Candidate candidate) {
		return queue.add(candidate);
	}

	/**
	 * Gets best candidate (which is the head of the underlying priority queue) and
	 * removes it from queue, or returns null if queue is empty.
	 */
	public Candidate getBestCandidate() {
		return queue.poll();
	}

	/**
	 * Gets underlying queue.
	 */
	public PriorityQueue<Candidate> getQueue() {
		return queue;
	}

	/**
	 * Resets queue and returns all contained candidates
	 */
	Candidate[] reset() {
		Candidate[] candidates = queue.toArray(new Candidate[0]);
		queue = new PriorityQueue<Candidate>(new Comparator<Candidate>() {
			public int compare(Candidate c1, Candidate c2) {
				return Float.compare(c2.getScore(), c1.getScore());
			}
		});
		return candidates;
	}
}