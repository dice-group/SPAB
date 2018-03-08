package org.dice_research.spab.input;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Representations for a single SPARQL unit (queries and update requests).
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
	 *            A SPARQL unit (query or update request).
	 * @param input
	 *            The {@link Input} this unit belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	public SparqlUnit(String sparqlUnit, Input input) {
		this.ORIGINAL_STRING = sparqlUnit;
		this.INPUT = input;

		create();
	}

	/**
	 * Creates the query / update request.
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	protected abstract void create() throws InputRuntimeException;

	/**
	 * Gets the original input string of the unit.
	 */
	public String getOriginalString() {
		return ORIGINAL_STRING;
	}

	/**
	 * Gets a string representation of a SPARQL query / update request.
	 * 
	 * No substitutions.
	 * 
	 * Uses cache.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public abstract String getStringRepresentation();

	/**
	 * Gets a string representation of a SPARQL query / update request.
	 * 
	 * Line breaks are substituted with blank spaces. Afterwards, multiple blank
	 * spaces are reduced to one blank space.
	 * 
	 * Uses cache.
	 * 
	 * Uses namespaces of {@link Input}.
	 */
	public abstract String getLineRepresentation();

	/**
	 * Line breaks are substituted with blank spaces. Afterwards, multiple blank
	 * spaces are reduced to one blank space.
	 */
	protected String toOneLiner(String multiLiner) {
		return multiLiner.replaceAll("\n", " ").replaceAll("\r", "").replaceAll(" +", " ");
	}

	/**
	 * Returns a string representation
	 */
	@Override
	public String toString() {
		return getLineRepresentation();
	}
}