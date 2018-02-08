package org.aksw.spab.input;

import org.apache.jena.rdf.model.RDFNode;

/**
 * Vertex in input query graph. Holds a subject or respectively an object of a
 * SPO triple. This can be a resource or a literal.
 * 
 * Anonymous resources should be initialized with NULL.
 * 
 * @author Adrian Wilke
 */
public class InputVertex {

	RDFNode resource;

	/**
	 * Initializes vertex.
	 */
	public InputVertex(RDFNode resource) {
		this.resource = resource;
	}

	/**
	 * Gets resource represented by vertex.
	 */
	public RDFNode getResource() {
		return resource;
	}

	/**
	 * Returns string representation of vertex.
	 */
	@Override
	public String toString() {
		if (resource == null) {
			return "";
		} else if (resource.isLiteral()) {
			return "'" + resource.toString() + "' ";
		} else {
			return "[" + resource.toString() + "] ";
		}
	}
}