package org.dice_research.spab.input;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.sparql.core.Prologue;
import org.dice_research.spab.Statistics;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL query.
 * 
 * @see https://www.w3.org/TR/2013/REC-sparql11-query-20130321/#rQuery
 * 
 * @author Adrian Wilke
 */
public class SparqlQuery extends SparqlUnit {

	/**
	 * The parsed query
	 */
	protected Query jenaQuery;

	/**
	 * Cache for line representation.
	 */
	protected String lineRepresentationCache;

	/**
	 * Cache for resource URIs
	 */
	protected Set<String> resourcesCache;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL query.
	 * 
	 * @param originalString
	 *            A SPARQL query, given by user
	 * @param input
	 *            The {@link Input} this query belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public SparqlQuery(String originalString, Input input) throws InputRuntimeException {
		super(originalString, input);
	}

	/**
	 * Replaces variables and parses query using Jena.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	@Override
	protected void create() throws InputRuntimeException {
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
	protected Query createJenaQuery(String queryString) {
		try {
			return QueryFactory.create(queryString);
		} catch (QueryParseException e) {
			throw new InputRuntimeException("Could not parse: " + queryString, e);
		}
	}

	/**
	 * Gets the SPARQL query.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	protected Query getJenaQuery() {
		return getJenaQuery(true);
	}

	/**
	 * Gets the SPARQL query.
	 */
	protected Query getJenaQuery(boolean useCache) {
		if (!useCache) {
			create();
		}
		return jenaQuery;
	}

	/**
	 * Gets the string representation of the Jena SPARQL query.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	@Override
	public String getJenaStringRepresentation() {
		return getJenaQuery(true).toString();
	}

	/**
	 * Gets a line representation of the SPARQL query. Namespace prefixes,
	 * abbreviated notation ("a", ";"), and line breaks are replaced.
	 * 
	 * Uses {@link SparqlUnit#replacePrefixes(String, Map)},
	 * {@link SparqlUnit#replaceAbbreviatedNotation(String)}, and
	 * {@link SparqlUnit#toOneLiner(String)}.
	 * 
	 * Uses cache.
	 */
	@Override
	public String getLineRepresentation() {
		if (lineRepresentationCache == null) {
			long time = System.currentTimeMillis();
			lineRepresentationCache = sortTriples(replaceAbbreviatedNotation(toOneLiner(
					replacePrefixes(getJenaQuery().toString(), getJenaQuery().getPrefixMapping().getNsPrefixMap()))));
			Statistics.addQueryLineStats(time, System.currentTimeMillis());
		}
		return lineRepresentationCache;
	}

	/**
	 * Replaces variables in SPARQL query
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

	/**
	 * Gets resources used in query.
	 */
	@Override
	public Set<String> getResources() {
		if (resourcesCache == null) {
			resourcesCache = new HashSet<String>();
			Pattern pattern = Pattern.compile("<(.*?)>");
			// Formerly jenaQuery.getQueryPattern().toString().
			// But this contains e.g. 'a' instead of rdf:type.
			Matcher matcher = pattern.matcher(getLineRepresentation());
			while (matcher.find()) {
				resourcesCache.add(matcher.group(1));
			}
		}
		return resourcesCache;
	}

}