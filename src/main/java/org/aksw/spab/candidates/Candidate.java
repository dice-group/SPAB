package org.aksw.spab.candidates;

import java.util.List;

import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * Interface for candidate implementations.
 * 
 * @author Adrian Wilke
 */
public interface Candidate {

	/**
	 * Returns generated children.
	 */
	public List<Candidate> getChildren() throws CandidateRuntimeException;

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRexEx() throws CandidateRuntimeException;

	/**
	 * Returns, if candidate matches a SPARQL query.
	 * 
	 * @deprecated Only for development and presentation of {@link DummyCandidate}
	 *             functionality. For real implementations use
	 *             {@link Candidate#getRexEx()} instead.
	 */
	public boolean matches(String query) throws CandidateRuntimeException;
}