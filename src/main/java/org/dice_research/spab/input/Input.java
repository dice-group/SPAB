package org.dice_research.spab.input;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	protected Set<String> resources;

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
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	protected void addQuery(String sparqlQuery, boolean positive) throws InputRuntimeException {

		// Try to add SPARQL query
		try {
			if (positive) {
				positives.add(new SparqlQuery(sparqlQuery, this));
			} else {
				negatives.add(new SparqlQuery(sparqlQuery, this));
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

				} catch (InputRuntimeException updateRequestException) {

					// Query could not be parsed for two times. Use heuristics to pass exception
					if (sparqlQuery.toUpperCase().contains("SELECT") || sparqlQuery.toUpperCase().contains("CONSTRUCT")
							|| sparqlQuery.toUpperCase().contains("DESCRIBE")
							|| sparqlQuery.toUpperCase().contains("ASK")) {
						throw originalException;
					} else {
						throw updateRequestException;
					}
				}

			} else {
				// No QueryParseException, just pass original exception
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

	/**
	 * Gets all used resources in inputs. Uses cache.
	 */
	public Set<String> getResources() {
		if (resources == null) {
			resources = new HashSet<String>();
			for (SparqlUnit sparqlUnit : negatives) {
				resources.addAll(sparqlUnit.getResources());
			}
			for (SparqlUnit sparqlUnit : positives) {
				resources.addAll(sparqlUnit.getResources());
			}
		}
		return resources;
	}

	/**
	 * Gets through the set of positive inputs, counts occurrences of reserved word
	 * UNION, and returns maximum.
	 */
	public int getMaxUnions() {
		int max = 0;
		for (SparqlUnit sparqlUnit : positives) {
			int i = 0;
			Pattern p = Pattern.compile("UNION");
			Matcher m = p.matcher(sparqlUnit.getLineRepresentation());
			while (m.find()) {
				i++;
			}
			if (i > max) {
				max = i;
			}
		}
		return max;
	}

	/**
	 * Gets through the set of positive inputs, counts occurrences of distinct
	 * variables, and returns maximum.
	 */
	public int getMaxVariables() {
		int max = 0;
		for (SparqlUnit sparqlUnit : positives) {
			Set<String> matches = new HashSet<String>();
			// ? means lazy matching instead of greedy matching
			Pattern p = Pattern.compile("\\?.+? ");
			Matcher m = p.matcher(sparqlUnit.getLineRepresentation());
			while (m.find()) {
				matches.add(m.toString());
			}
			if (matches.size() > max) {
				max = matches.size();
			}
		}
		return max;
	}
}