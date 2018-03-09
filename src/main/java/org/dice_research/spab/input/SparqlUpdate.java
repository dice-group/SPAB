package org.dice_research.spab.input;

import org.apache.jena.query.QueryException;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL update-request.
 * 
 * @author Adrian Wilke
 */
public class SparqlUpdate extends SparqlUnit {

	/**
	 * The parsed update-request
	 */
	protected UpdateRequest jenaUpdateRequest;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL update-request.
	 * 
	 * @param sparqlUnit
	 *            A SPARQL unit (query or update-request).
	 * @param input
	 *            The {@link Input} this unit belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	public SparqlUpdate(String sparqlUpdateRequest, Input input) throws InputRuntimeException {
		super(sparqlUpdateRequest, input);
	}

	/**
	 * Creates the update-request.
	 * 
	 * @throws InputRuntimeException
	 *             if update-request string could not be parsed
	 */
	@Override
	protected void create() throws InputRuntimeException {
		try {
			jenaUpdateRequest = UpdateFactory.create(getOriginalString());
		} catch (QueryException e) {
			throw new InputRuntimeException("Could not parse: " + getOriginalString(), e);
		}
	}

	/**
	 * Gets the string representation of the Jena update-request.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	@Override
	public String getJenaStringRepresentation() {
		return getJenaUpdateRequest(true).toString();
	}

	/**
	 * Gets the SPARQL update-request.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	public UpdateRequest getJenaUpdateRequest() {
		return getJenaUpdateRequest(true);
	}

	/**
	 * Gets the SPARQL update-request.
	 */
	protected UpdateRequest getJenaUpdateRequest(boolean useCache) {
		if (!useCache) {
			create();
		}
		return jenaUpdateRequest;
	}

	/**
	 * Gets a line representation of the update-request.
	 * 
	 * Uses {@link SparqlUnit#toOneLiner(String)}.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	@Override
	public String getLineRepresentation() {
		return toOneLiner(getJenaUpdateRequest().toString());
	}
}