package org.dice_research.spab.candidates.one;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.QueryFactory;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;

/**
 * Candidates of type SELECT.
 * 
 * @author Adrian Wilke
 */
public class SpabOneSelectCandidate extends SpabOneCandidate {

	public SpabOneSelectCandidate() {
		query = QueryFactory.create();
		query.setQuerySelectType();
		query.setQueryResultStar(true);
	}

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {

		// TODO
		return new LinkedList<Candidate>();
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRegEx() throws CandidateRuntimeException {
		return (query.toString().replace("*", ".*").replace(" ", ""));
	}

}