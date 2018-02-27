package org.dice_research.spab.candidates.one;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;

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
		// SPARQL update-prefixes are not added, as they are not supported in input.
		List<String> prefixes = new LinkedList<String>();
		prefixes.addAll(Arrays.asList(queryPrefixes));
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
	public String getRegEx() throws CandidateRuntimeException {

		// Will not match any query
		return "";
	}
}