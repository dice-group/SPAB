package org.dice_research.spab.input;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.QueryParseException;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Container for input queries.
 * 
 * @author Adrian Wilke
 */
public class Input {

	protected List<SparqlUnit> negatives;
	protected List<SparqlUnit> positives;

	/**
	 * Initializes model and sets namespace prefixes for RDF, RDFS, and SPIN.
	 */
	public Input() {
		positives = new LinkedList<SparqlUnit>();
		negatives = new LinkedList<SparqlUnit>();
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) throws InputRuntimeException {
		addQuery(sparqlQuery, false);
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) throws InputRuntimeException {
		addQuery(sparqlQuery, true);
	}

	/**
	 * Adds query to set of inputs.
	 * 
	 * TODO: Handle all types of inputs
	 */
	protected void addQuery(String sparqlQuery, boolean positive) {

		// Try to add SPARQL query
		try {
			SparqlQuery query = new SparqlQuery(sparqlQuery, this);
			if (positive) {
				positives.add(query);
			} else {
				negatives.add(query);
			}

		} catch (InputRuntimeException originalException) {

			// Try to add SPARQL update request
			if (originalException.getCause() != null && originalException.getCause() instanceof QueryParseException) {
				try {
					if (positive) {
						positives.add(new SparqlUpdate(sparqlQuery, this));
					} else {
						negatives.add(new SparqlUpdate(sparqlQuery, this));
					}
				} catch (Exception e) {
					throw originalException;
				}

			} else {
				throw originalException;
			}
		}
	}

	/**
	 * Gets set of negative inputs.
	 */
	public List<SparqlUnit> getNegatives() {
		return negatives;
	}

	/**
	 * Gets set of positive inputs.
	 */
	public List<SparqlUnit> getPositives() {
		return positives;
	}

}