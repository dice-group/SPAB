package org.dice_research.spab.input;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * Cache for line representation.
	 */
	protected String lineRepresentationCache;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL update-request.
	 * 
	 * @param originalString
	 *            A SPARQL update-request, given by user
	 * @param input
	 *            The {@link Input} this unit belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	public SparqlUpdate(String originalString, Input input) throws InputRuntimeException {
		super(originalString, input);
	}

	/**
	 * Parses update-request by using Jena.
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
	 * Gets a line representation of the update-request. Namespace prefixes,
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
			lineRepresentationCache = replaceAbbreviatedNotation(toOneLiner(replacePrefixes(
					getJenaUpdateRequest().toString(), getJenaUpdateRequest().getPrefixMapping().getNsPrefixMap())));
		}
		return lineRepresentationCache;
	}

	/**
	 * Gets resources used in query.
	 */
	@Override
	public Set<String> getResources() {
		Set<String> resources = new HashSet<String>();
		Pattern pattern = Pattern.compile("<(.*?)>");
		Matcher matcher = pattern.matcher(getLineRepresentation());
		while (matcher.find()) {
			resources.add(matcher.group(1));
		}
		return resources;
	}
}