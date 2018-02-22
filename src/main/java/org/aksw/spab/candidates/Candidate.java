package org.aksw.spab.candidates;

import java.util.List;

import org.aksw.spab.SpabAlgorithm;
import org.aksw.spab.exceptions.CandidateRuntimeException;

/**
 * Interface for candidate implementations.
 * 
 * New implementations of candidates must be added to the enumeration
 * CandidateImplementation in {@link SpabApi} and to
 * {@link SpabAlgorithm#execute()}.
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
	 *             {@link Candidate#getRexEx()} instead and return false.
	 */
	public boolean matches(String query) throws CandidateRuntimeException;
}