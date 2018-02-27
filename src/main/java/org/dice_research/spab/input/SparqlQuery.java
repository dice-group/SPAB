package org.dice_research.spab.input;

import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.Prologue;
import org.apache.jena.update.UpdateFactory;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class SparqlQuery {

	/**
	 * The {@link Input} this query belongs to
	 */
	final protected Input input;

	/**
	 * The original input string of the query
	 */
	final protected String originalString;

	/**
	 * The parsed query
	 */
	protected Query query;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL query.
	 * 
	 * @param sparqlQuery
	 *            A SPARQL query
	 * @param input
	 *            The {@link Input} this query belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public SparqlQuery(String sparqlQuery, Input input) {
		this.originalString = sparqlQuery;
		this.input = input;

		create();
	}

	/**
	 * Creates the query.
	 * 
	 * Uses namespaces of {@link Input}.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	protected void create() throws InputRuntimeException {

		// Add namespaces
		Prologue queryPrologue = new Prologue();
		for (Entry<String, String> namespace : input.getNamespaces().entrySet()) {
			queryPrologue.setPrefix(namespace.getKey(), namespace.getValue());
		}

		// Create query
		try {
			query = QueryFactory.parse(new Query(queryPrologue), getOriginalString(), null, null);
		} catch (QueryParseException originalException) {

			// Check for SPARQL update queries. Add information, if exception occurred
			// because of an update instead of a query. Will not work, if no namespaces are
			// defined in original query.
			try {
				UpdateFactory.create(getOriginalString()).toString();
			} catch (QueryParseException notUsedException) {
				throw new InputRuntimeException(originalException);
			}
			throw new InputRuntimeException("SPARQL updates are not supported. Please use SPARQL queries.",
					originalException);
		}
	}

	/**
	 * Gets the original input string of the query.
	 */
	public String getOriginalString() {
		return originalString;
	}

	/**
	 * Gets the SPARQL query.
	 * 
	 * Uses cache.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public Query getQuery() {
		return getQuery(true);
	}

	/**
	 * Gets the SPARQL query.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public Query getQuery(boolean useCache) {
		if (!useCache) {
			create();
		}
		return query;
	}

	/**
	 * Gets a string representation of a SPARQL query.
	 * 
	 * Line breaks are substituted with blank spaces. Afterwards, multiple blank
	 * spaces are reduced to one blank space.
	 * 
	 * Uses cache.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public String getStringRepresentation() {
		return getQuery(true).toString().replaceAll("\n", " ").replaceAll("\r", "").replaceAll(" +", " ");
	}

	@Override
	public String toString() {
		return getStringRepresentation();
	}
}