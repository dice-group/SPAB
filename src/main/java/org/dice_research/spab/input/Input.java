package org.dice_research.spab.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Container for input queries.
 * 
 * @author Adrian Wilke
 */
public class Input {

	protected Model model;
	protected List<SparqlUnit> negatives;
	protected List<SparqlUnit> positives;

	/**
	 * Initializes model and sets namespace prefixes for RDF, RDFS, and SPIN.
	 */
	public Input() {
		positives = new LinkedList<SparqlUnit>();
		negatives = new LinkedList<SparqlUnit>();

		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("rdf", RDF.getURI());
		model.setNsPrefix("rdfs", RDFS.getURI());
	}

	/**
	 * Adds prefix for namespace
	 */
	public void addNamespacePrefix(String prefix, String uri) {
		model.setNsPrefix(prefix, uri);
	}

	/**
	 * Adds query to set of negative inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addNegative(String sparqlQuery) {
		negatives.add(new SparqlQuery(sparqlQuery, this));
	}

	/**
	 * Adds query to set of positive inputs.
	 * 
	 * @throws InputRuntimeException
	 *             if query string could not be parsed
	 */
	public void addPositive(String sparqlQuery) {

		// Try to add SPARQL query
		try {
			positives.add(new SparqlQuery(sparqlQuery, this));
		} catch (InputRuntimeException originalException) {

			// Try to add SPARQL update request
			if (originalException.getCause() != null && originalException.getCause() instanceof QueryParseException) {
				try {
					positives.add(new SparqlUpdate(sparqlQuery, this));
				} catch (Exception e) {
					throw originalException;
				}

			} else {
				throw originalException;
			}
		}

	}

	/**
	 * Gets prefixes for namespaces
	 */
	public Map<String, String> getNamespaces() {
		return model.getNsPrefixMap();
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

}