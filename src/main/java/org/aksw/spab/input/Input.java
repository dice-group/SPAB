package org.aksw.spab.input;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.aksw.spab.exceptions.InputRuntimeException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 * Container for input queries.
 * 
 * @author Adrian Wilke
 */
public class Input {

	protected Model model;
	protected List<SparqlQuery> negatives;
	protected List<SparqlQuery> positives;

	/**
	 * Initializes model and sets namespace prefixes for RDF, RDFS, and SPIN.
	 */
	public Input() {
		positives = new LinkedList<SparqlQuery>();
		negatives = new LinkedList<SparqlQuery>();

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
		positives.add(new SparqlQuery(sparqlQuery, this));
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
	public List<SparqlQuery> getNegatives() {
		return negatives;
	}

	/**
	 * Gets set of positive inputs.
	 */
	public List<SparqlQuery> getPositives() {
		return positives;
	}

}