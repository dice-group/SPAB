package org.aksw.spab.candidates.one;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * First implementation of Candidate, type: root.
 * 
 * Represents general SELECT.
 * 
 * @author Adrian Wilke
 */
public class SpabOneRootCandidate extends SpabOneCandidate {

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {

		// Get all prefixes except SELECT
		List<String> prefixes = new LinkedList<String>();
		prefixes.addAll(Arrays.asList(queryPrefixes));
		prefixes.addAll(Arrays.asList(updatePrefixes));
		boolean removed = false;
		for (int i = 0; i < prefixes.size(); i++) {
			if (prefixes.get(i).equals("SELECT")) {
				prefixes.remove(i);
				removed = true;
			}
		}
		if (!removed) {
			throw new CandidateRuntimeException("Could not remove select.");
		}

		// Generate children
		for (String queryStart : prefixes) {
			// TODO: These are no SELECTs.
			children.add(new SpabOneSelectCandidate(queryStart + ".*"));
		}

		// TODO: Refine SELECT in children.

		return children;
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRexEx() throws CandidateRuntimeException {
		// TODO: Check.
		return ("SELECT.*");
	}
}