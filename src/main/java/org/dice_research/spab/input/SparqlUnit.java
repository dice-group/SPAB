package org.dice_research.spab.input;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dice_research.spab.exceptions.InputRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representations for a single SPARQL unit (queries and update-requests).
 * 
 * @author Adrian Wilke
 */
public abstract class SparqlUnit {

	private static final Logger LOGGER = LoggerFactory.getLogger(SparqlUnit.class);

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
	 * @param originalString
	 *            A SPARQL unit (query or update-request), given by user
	 * @param input
	 *            The {@link Input} this unit belongs to
	 * 
	 * @throws InputRuntimeException
	 *             if unit string could not be parsed
	 */
	public SparqlUnit(String originalString, Input input) throws InputRuntimeException {
		this.ORIGINAL_STRING = originalString;
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
	 * Gets a line representation of a SPARQL query / update-request. Namespace
	 * prefixes, abbreviated notation ("a", ";"), and line breaks are replaced.
	 * 
	 * Uses {@link SparqlUnit#replacePrefixes(String, Map)},
	 * {@link SparqlUnit#replaceAbbreviatedNotation(String)}, and
	 * {@link SparqlUnit#toOneLiner(String)}.
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
				if (a.length() > b.length()) {
					return -1;
				} else if (a.length() < b.length()) {
					return 1;
				} else {
					return b.compareTo(a);
				}
			}
		});
		orderedNamespaces.putAll(namespaces);

		// Replace prefixes
		StringBuffer sb = new StringBuffer(removedPrefixes.toString());
		for (Entry<String, String> entry : orderedNamespaces.entrySet()) {
			String workingString = sb.toString();
			sb = new StringBuffer();
			if (entry.getKey().isEmpty()) {
				// Will be last called prefix, as comparator sorts by length.
				Pattern pattern = Pattern.compile("( :)(.+?)(^|\\s)");
				Matcher matcher = pattern.matcher(workingString);
				while (matcher.find()) {
					matcher.appendReplacement(sb, "<" + entry.getValue() + matcher.group(2) + ">" + matcher.group(3));
				}
				matcher.appendTail(sb);
			} else {
				Pattern pattern = Pattern.compile("(" + entry.getKey() + ":)(.+?)(^|\\s)");
				Matcher matcher = pattern.matcher(workingString);
				while (matcher.find()) {
					matcher.appendReplacement(sb, "<" + entry.getValue() + matcher.group(2) + ">" + matcher.group(3));
				}
				matcher.appendTail(sb);
			}
		}

		if (sb.length() == 0) {
			// No prefix replaced
			return removedPrefixes.toString();
		} else {
			// RegEx result
			return sb.toString();
		}
	}

	/**
	 * Sorts triples by sort order: Subject, predicate, object. Every s,p,o sorted
	 * by: Variable, resource, literal.
	 */
	protected String sortTriples(String sparqlLine) {

		// Container for triples
		class Triple {
			String s;
			String p;
			String o;

			public Triple(String s, String p, String o) {
				this.s = s;
				this.p = p;
				this.o = o;
			}

			@Override
			public String toString() {
				return s + " " + p + " " + o;
			}
		}

		// Aim of this method: Sort triples
		Comparator<Triple> tripleComparator = new Comparator<Triple>() {
			@Override
			public int compare(Triple a, Triple b) {
				try {
					if (compare(a.s, b.s) != 0) {
						return compare(a.s, b.s);
					} else if (compare(a.p, b.p) != 0) {
						return compare(a.p, b.p);
					} else {
						return compare(a.o, b.o);
					}
				} catch (Exception e) {
					LOGGER.warn("Unsupported SPARQL format (" + e.getMessage() + ", " + a + " | " + b);
					return 0;
				}
			}

			/**
			 * 1. Variables: '?' or '$'
			 * 
			 * 2. Resources: '<'
			 * 
			 * 3. Literals: '"' or "'"
			 */
			private int compare(String a, String b) throws Exception {
				int prioA = 0;
				int prioB = 0;
				a = a.substring(0, 1);
				b = b.substring(0, 1);
				if (a.equals("?") || a.equals("$")) {
					prioA = 3;
				} else if (a.equals("<")) {
					prioA = 2;
				} else if (a.equals("'") || a.equals("\"")) {
					prioA = 1;
				} else {
					throw new Exception(a);
				}
				if (b.equals("?") || b.equals("$")) {
					prioB = 3;
				} else if (b.equals("<")) {
					prioB = 2;
				} else if (b.equals("'") || b.equals("\"")) {
					prioB = 1;
				} else {
					throw new Exception(b);
				}
				return prioB - prioA;
			}
		};

		StringJoiner stringJoiner = new StringJoiner(" ");
		boolean searchTriples = false;
		LinkedList<Triple> triples = new LinkedList<Triple>();
		String[] parts = sparqlLine.split(" ");
		for (int i = 0; i < parts.length; i++) {
			if (searchTriples) {

				// Remember triples
				if (parts[i + 3].equals(".") || parts[i + 3].equals("}")) {
					triples.add(new Triple(parts[i], parts[i + 1], parts[i + 2]));
					i += 3;
				} else {

					// Triple sould only end with ',' or '}'
					LOGGER.warn(
							"Unsupported SPARQL format [index " + (i + 3) + ": " + parts[i + 3] + "]: " + sparqlLine);
					return sparqlLine;
				}

				// Add triples to return string
				if (parts[i].equals("}")) {
					searchTriples = false;
					triples.sort(tripleComparator);
					boolean firstTriple = true;
					for (Triple triple : triples) {
						if (firstTriple) {
							firstTriple = false;
						} else {
							stringJoiner.add(".");
						}
						stringJoiner.add(triple.toString());
					}
					triples.clear();
					stringJoiner.add("}");
				}

				// Control flow; add miscellaneous characters
			} else {
				if (parts[i].equals("{")) {
					stringJoiner.add("{");
					searchTriples = true;
				} else {
					stringJoiner.add(parts[i]);
				}
			}
		}

		return stringJoiner.toString();
	}

	/**
	 * Replaces "a" with RDF type.
	 * 
	 * Replaces ";" notation.
	 * 
	 * Replaces ". }"
	 */
	protected String replaceAbbreviatedNotation(String abbreviatedNotation) {

		List<String> parts = new ArrayList<String>(Arrays.asList(abbreviatedNotation.split(" ")));
		for (int i = 0; i < parts.size(); i++) {

			if (parts.get(i).equals("a")) {
				parts.set(i, "<http://www.w3.org/1999/02/22-rdf-syntax-ns#type>");

			} else if (parts.get(i).equals(";")) {
				parts.set(i, ".");
				parts.add(i + 1, parts.get(i - 3));
			}
		}

		StringJoiner stringJoiner = new StringJoiner(" ");
		for (String part : parts) {
			stringJoiner.add(part);
		}
		String query = stringJoiner.toString();

		return query.replaceAll("\\. }", "}");
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