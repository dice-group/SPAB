package org.aksw.spab;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.candidates.DummyCandidate;

/**
 * Configuration of candidate implementations for SPAB developers.
 * 
 * @author Adrian Wilke
 */
public class Configuration {

	/**
	 * Returns root instance of used {@link Candidate} implementation.
	 */
	public static Candidate getRoot() {
		return new DummyCandidate();
	}
	
}