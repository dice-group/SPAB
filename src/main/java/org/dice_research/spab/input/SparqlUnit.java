package org.dice_research.spab.input;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL unit (queries and update-requests).
 * 
 * @author Adrian Wilke
 */
public abstract class SparqlUnit {

	/**
	 * List of 26 names for variables.
	 */
	static List<String> variableNames = new LinkedList<String>();

	static {
		for (int i = 10; i <= 35; i++) {
			variableNames.add("?" + Character.forDigit(i, 36));
		}
	}

	/**
	 * The {@link Input} this unit belongs to
	 */
	final protected Input INPUT;

	/**
	 * The original input string of the unit
	 */
	final protected String ORIGINAL_STRING;

	/**
	 * Sets the passed parameters.
	 * 
	 * Parses the SPARQL unit.
	 * 
	 * @param sparqlUnit
	 *            A SPARQL unit (query or update-request).
	 * @param input
	 *            The {@link Input} this unit belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	public SparqlUnit(String sparqlUnit, Input input) throws InputRuntimeException {
		this.ORIGINAL_STRING = sparqlUnit;
		this.INPUT = input;
		create();
	}

	/**
	 * Creates the query / update-request.
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	protected abstract void create() throws InputRuntimeException;

	/**
	 * Gets the string representation of a Jena SPARQL query / update-request.
	 * 
	 * Uses cache. The original string is only parsed one time.
	 */
	public abstract String getJenaStringRepresentation();

	/**
	 * Gets a line representation of a SPARQL query / update-request.
	 * 
	 * Namespace prefixes are replaced.
	 * 
	 * Uses {@link SparqlUnit#toOneLiner(String)}.
	 * 
	 * Uses cache.
	 */
	public abstract String getLineRepresentation();

	/**
	 * Gets the original input string of the unit.
	 */
	public String getOriginalString() {
		return ORIGINAL_STRING;
	}

	/**
	 * Gets resources used in query.
	 */
	public abstract Set<String> getResources();

	/**
	 * Replaces namespace prefixes.
	 */
	protected String replacePrefixes(String jenaString, Map<String, String> namespaces) {

		// Remove prefix lines
		StringBuilder removedPrefixes = new StringBuilder();
		for (String line : jenaString.split("\\r?\\n")) {
			if (!line.startsWith("PREFIX ") && !line.isEmpty()) {
				removedPrefixes.append(line);
				removedPrefixes.append(System.lineSeparator());
			}
		}

		// Prepend replacing wrong namespaces: Start with longer ones
		SortedMap<String, String> orderedNamespaces = new TreeMap<String, String>(new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return b.compareTo(a);
			}
		});
		orderedNamespaces.putAll(namespaces);

		// Replace prefixes
		String replacedPrefixes = removedPrefixes.toString();
		StringBuffer sb = new StringBuffer();

		for (Entry<String, String> entry : orderedNamespaces.entrySet()) {
			if (entry.getKey().isEmpty()) {
				// Empty namespace prefixes ":" not used in this version.
				continue;
			}

			Pattern pattern = Pattern.compile("(" + entry.getKey() + ":)(.+?)(^|\\s)");
			Matcher matcher = pattern.matcher(replacedPrefixes);
			while (matcher.find()) {
				matcher.appendReplacement(sb, "<" + entry.getValue() + matcher.group(2) + ">" + matcher.group(3));
			}
			matcher.appendTail(sb);
		}

		if (sb.length() == 0) {
			// No prefix replaced
			return replacedPrefixes;
		} else {
			// RegEx result
			return sb.toString();
		}
	}

	/**
	 * Line breaks are substituted with blank spaces. Afterwards, multiple blank
	 * spaces are reduced to one blank space.
	 */
	protected String toOneLiner(String multiLiner) {
		return multiLiner.replaceAll("\n", " ").replaceAll("\r", " ").replaceAll(" +", " ");
	}

	/**
	 * Returns a string representation. Uses {@link #getLineRepresentation()}.
	 */
	@Override
	public String toString() {
		return getLineRepresentation();
	}
}