package org.aksw.spab.input;

import java.util.Map.Entry;

import org.aksw.spab.exceptions.UserInputException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.Prologue;

/**
 * Representations for a single SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class InputQuery {

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
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	public InputQuery(String sparqlQuery, Input input) {
		this.originalString = sparqlQuery;
		this.input = input;

		create();
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
	 * Creates the query.
	 * 
	 * Uses namespaces of {@link Input}.
	 * 
	 * @throws UserInputException
	 *             if query string could not be parsed
	 */
	protected void create() throws UserInputException {

		// Add namespaces
		Prologue queryPrologue = new Prologue();
		for (Entry<String, String> namespace : input.getNamespaces().entrySet()) {
			queryPrologue.setPrefix(namespace.getKey(), namespace.getValue());
		}

		// Create query
		try {
			query = QueryFactory.parse(new Query(queryPrologue), getOriginalString(), null, null);
		} catch (QueryParseException e) {
			throw new UserInputException(e);
		}
	}
}