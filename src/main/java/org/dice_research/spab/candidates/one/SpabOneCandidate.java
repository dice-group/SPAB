package org.dice_research.spab.candidates.one;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.Query;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Abstract class with general data and methods for implementations.
 * 
 * @author Adrian Wilke
 */
public abstract class SpabOneCandidate implements Candidate {

	/**
	 * SPARQL query prefixes
	 * 
	 * @see SPARQL grammar https://www.w3.org/TR/sparql11-query/#rQuery
	 */
	protected static final String[] queryPrefixes = { "SELECT", "CONSTRUCT", "DESCRIBE", "ASK" };

	/**
	 * SPARQL update prefixes
	 * 
	 * @see SPARQL grammar https://www.w3.org/TR/sparql11-query/#rUpdate1
	 */
	protected static final String[] updatePrefixes = { "LOAD", "CLEAR", "DROP", "ADD", "MOVE", "COPY", "CREATE",
			"INSERT DATA", "DELETE DATA", "DELETE WHERE" };

	protected CandidateVertex candidateVertex;
	protected List<Candidate> children = new LinkedList<Candidate>();
	protected Query query;
	protected String regex;

	public SpabOneCandidate() {
	}

	public SpabOneCandidate(Query query) {
		this.query = query;
	}

	public SpabOneCandidate(String regex) {
		this.regex = regex;
	}

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException {
		return children;
	}

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public abstract String getRegEx() throws CandidateRuntimeException;


	/**
	 * Sets the related vertex in the candidate graph.
	 */
	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}
}