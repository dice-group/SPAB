package org.dice_research.spab.candidates;

import java.util.List;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;

/**
 * Interface for candidate implementations.
 * 
 * New implementations of candidates must be added to the enumeration
 * CandidateImplementation in {@link SpabApi} and to {@link CandidateFactory}.
 * 
 * @author Adrian Wilke
 */
public interface Candidate<InternalRepresentation> {

	/**
	 * Returns generated children.
	 */
	public List<Candidate<InternalRepresentation>> getChildren(Input input) throws CandidateRuntimeException;

	/**
	 * Returns a regular expression to match SPARQL queries.
	 */
	public String getRegEx() throws CandidateRuntimeException;

	/**
	 * Returns internal representation of candidate.
	 */
	public InternalRepresentation getInternalRepresentation(Class<InternalRepresentation> internalRepresentationClass)
			throws CandidateRuntimeException;
}