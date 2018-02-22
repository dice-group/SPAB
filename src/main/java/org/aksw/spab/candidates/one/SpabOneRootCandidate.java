package org.aksw.spab.candidates.one;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * Root candidate. Generates children for SPARQL-queries and -updates.
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

		// Generate generic children children
		for (String queryStart : prefixes) {
			children.add(new SpabOneGenericCandidate(queryStart + ".*"));
		}

		// Create child for SELECT queries
		children.add(new SpabOneSelectCandidate());

		return children;
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRexEx() throws CandidateRuntimeException {

		// Will not match any query
		return "";
	}
}