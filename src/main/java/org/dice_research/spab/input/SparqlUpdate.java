package org.dice_research.spab.input;

import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL update request.
 * 
 * @author Adrian Wilke
 */
public class SparqlUpdate extends SparqlUnit {

	/**
	 * The parsed update request
	 */
	protected UpdateRequest updateRequest;

	public SparqlUpdate(String sparqlUpdateRequest, Input input) {
		super(sparqlUpdateRequest, input);
	}

	/**
	 * Creates the update request.
	 * 
	 * Uses namespaces of {@link Input}.
	 * 
	 * @throws InputRuntimeException
	 *             if update request string could not be parsed
	 */
	protected void create() throws InputRuntimeException {
		// Create update request
		updateRequest = UpdateFactory.create(getOriginalString());
	}

	/**
	 * Gets a string representation of a SPARQL update request.
	 * 
	 * Line breaks are substituted with blank spaces. Afterwards, multiple blank
	 * spaces are reduced to one blank space.
	 * 
	 * Uses cache.
	 */
	public String getLineRepresentation() {
		return toOneLiner(getUpdateRequest(true).toString());
	}

	@Override
	public String getStringRepresentation() {
		return getUpdateRequest(true).toString();
	}

	/**
	 * Gets the SPARQL update request.
	 * 
	 * Uses cache.
	 */
	public UpdateRequest getUpdateRequest() {
		return getUpdateRequest(true);
	}

	/**
	 * Gets the SPARQL update request.
	 */
	public UpdateRequest getUpdateRequest(boolean useCache) {
		if (!useCache) {
			create();
		}
		return updateRequest;
	}
}