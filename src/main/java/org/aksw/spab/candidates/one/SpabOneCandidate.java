package org.aksw.spab.candidates.one;

import java.util.LinkedList;
import java.util.List;

import org.aksw.spab.candidates.Candidate;
import org.aksw.spab.exceptions.CandidateRuntimeException;
import org.aksw.spab.structures.CandidateVertex;
import org.apache.jena.query.Query;

/**
 * First implementation of Candidate.
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

	protected List<Candidate> children = new LinkedList<Candidate>();
	protected SpabOneCandidate parent;
	protected Query query;
	protected String regex;
	protected CandidateVertex candidateVertex;

	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}

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
	public String getRexEx() throws CandidateRuntimeException {
		if (regex != null && !regex.isEmpty()) {
			return regex;
		} else {
			throw new CandidateRuntimeException("No regular expression set. " + this.getClass().getName());
		}
	}

	/**
	 * @deprecated
	 */
	public boolean matches(String query) throws CandidateRuntimeException {
		return false;
	}
}