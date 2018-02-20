package org.aksw.spab.structures;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Priority queue for candidates. Priority is calculated via the getScore method
 * of candidates.
 * 
 * @author Adrian Wilke
 */
public class CandidateQueue {

	protected PriorityQueue<CandidateVertex> queue;

	/**
	 * Initializes queue.
	 */
	public CandidateQueue() {
		queue = new PriorityQueue<CandidateVertex>(new Comparator<CandidateVertex>() {
			public int compare(CandidateVertex c1, CandidateVertex c2) {
				return Float.compare(c2.getScore(), c1.getScore());
			}
		});
	}

	/**
	 * Inserts candidate into queue.
	 */
	public boolean add(CandidateVertex candidate) {
		return queue.add(candidate);
	}

	/**
	 * Gets best candidate (which is the head of the underlying priority queue) and
	 * removes it from queue, or returns null if queue is empty.
	 */
	public CandidateVertex getBestCandidate() {
		return queue.poll();
	}

	/**
	 * Gets underlying queue.
	 */
	public PriorityQueue<CandidateVertex> getQueue() {
		return queue;
	}

	/**
	 * Resets queue and returns all contained candidates
	 */
	public CandidateVertex[] reset() {
		CandidateVertex[] candidates = queue.toArray(new CandidateVertex[0]);
		queue = new PriorityQueue<CandidateVertex>(new Comparator<CandidateVertex>() {
			public int compare(CandidateVertex c1, CandidateVertex c2) {
				return Float.compare(c2.getScore(), c1.getScore());
			}
		});
		return candidates;
	}
}