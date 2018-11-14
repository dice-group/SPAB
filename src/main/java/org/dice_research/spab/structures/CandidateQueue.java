package org.dice_research.spab.structures;

import java.util.Iterator;
import java.util.PriorityQueue;

import org.dice_research.spab.CandidateComparator;

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
		queue = new PriorityQueue<CandidateVertex>(new CandidateComparator());
	}

	/**
	 * Inserts candidate into queue.
	 */
	public boolean add(CandidateVertex candidate) {
		return queue.add(candidate);
	}

	/**
	 * Gets best candidate (which is the head of the underlying priority queue) and
	 * REMOVES IT from queue, or returns null if queue is empty.
	 */
	public CandidateVertex pollBestCandidate() {
		return queue.poll();
	}

	/**
	 * Gets best candidate (which is the head of the underlying priority queue).
	 * Returns null if queue is empty.
	 */
	public CandidateVertex peekBestCandidate() {
		return queue.peek();
	}

	/**
	 * Gets best candidate with specified index out of queue. An index of 0 will
	 * return the next best candidate. Returns null, if index is not in queue.
	 */
	public CandidateVertex peekBestCandidate(int index) {
		if (queue.size() < index + 1) {
			return null;
		} else {
			CandidateVertex candidate = null;
			Iterator<CandidateVertex> it = queue.iterator();
			while (index >= 0) {
				candidate = it.next();
				index--;
			}
			return candidate;
		}
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
		queue = new PriorityQueue<CandidateVertex>(new CandidateComparator());
		return candidates;
	}
}