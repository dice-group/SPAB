package org.dice_research.spab.input;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.Prologue;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class SparqlQuery extends SparqlUnit {

	/**
	 * The parsed query
	 */
	protected Query query;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL query.
	 * 
	 * @param sparqlUnit
	 *            A SPARQL query
	 * @param input
	 *            The {@link Input} this query belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public SparqlQuery(String sparqlQuery, Input input) {
		super(sparqlQuery, input);
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
		for (Entry<String, String> namespace : INPUT.getNamespaces().entrySet()) {
			queryPrologue.setPrefix(namespace.getKey(), namespace.getValue());
		}

		// Create query
		try {
			String queryReplacedVars = replaceVariables(queryPrologue, getOriginalString());
			query = createJenaQuery(queryPrologue, queryReplacedVars);
		} catch (QueryParseException e) {
			throw new InputRuntimeException(e);
		}
	}

	/**
	 * Creates query using given namespaces and query-string
	 */
	protected Query createJenaQuery(Prologue queryPrologue, String queryString) {
		return QueryFactory.parse(new Query(queryPrologue), queryString, null, null);
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
	public String getLineRepresentation() {
		return toOneLiner(getQuery(true).toString());
	}

	@Override
	public String getStringRepresentation() {
		return getQuery(true).toString();
	}

	/**
	 * Replaces variables in SPARQL query
	 */
	protected String replaceVariables(Prologue queryPrologue, String queryString) {
		Query tmpQuery = createJenaQuery(queryPrologue, queryString);
		List<String> tmpResultVars = tmpQuery.getResultVars();
		Collections.sort(tmpResultVars);
		String tmpQueryString = tmpQuery.toString();
		for (int i = 0; i < tmpResultVars.size(); i++) {
			if (i > variableNames.size()) {
				break;
			} else {
				tmpQueryString = tmpQueryString.replace("?" + tmpResultVars.get(i), variableNames.get(i));
			}
		}
		return tmpQueryString;
	}

}