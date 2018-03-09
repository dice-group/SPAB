package org.dice_research.spab.input;

import java.util.Collections;
import java.util.List;

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
	protected Query jenaQuery;

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

		// Create query
		String queryReplacedVars = replaceVariables(getOriginalString());
		jenaQuery = createJenaQuery(queryReplacedVars);
	}

	/**
	 * Creates Jena query using given namespaces and query-string.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	protected Query createJenaQuery(Prologue queryPrologue, String queryString) {
		try {
			return QueryFactory.parse(new Query(queryPrologue), queryString, null, null);
		} catch (QueryParseException e) {
			throw new InputRuntimeException("Could not parse " + getOriginalString(), e);
		}
	}

	/**
	 * Creates Jena query.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public static Query createJenaQuery(String queryString) {
		try {
			return QueryFactory.create(queryString);
		} catch (QueryParseException e) {
			throw new InputRuntimeException("Could not parse: " + queryString, e);
		}
	}

	/**
	 * Gets the SPARQL query.
	 * 
	 * Uses cache.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public Query getJenaQuery() {
		return getJenaQuery(true);
	}

	/**
	 * Gets the SPARQL query.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public Query getJenaQuery(boolean useCache) {
		if (!useCache) {
			create();
		}
		return jenaQuery;
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
		return toOneLiner(getJenaQuery(true).toString());
	}

	@Override
	public String getStringRepresentation() {
		return getJenaQuery(true).toString();
	}

	/**
	 * Replaces variables in SPARQL query
	 * 
	 * TODO
	 */
	protected String replaceVariables(String queryString) {
		Query tmpQuery = createJenaQuery(queryString);
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