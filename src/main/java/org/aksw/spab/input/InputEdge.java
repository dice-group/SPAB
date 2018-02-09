package org.aksw.spab.input;

import org.apache.jena.rdf.model.Property;
import org.jgrapht.graph.DefaultEdge;

/**
 * Edge in input query graph. Holds a predicate of a SPO triple.
 * 
 * @author Adrian Wilke
 */
public class InputEdge extends DefaultEdge {

	protected static final long serialVersionUID = 1L;
	protected Property predicate;

	/**
	 * Initializes edge.
	 */
	public InputEdge(Property predicate) {
		this.predicate = predicate;
	}

	/**
	 * Gets predicate represented by edge.
	 */
	public Property getPredicate() {
		return predicate;
	}

	/**
	 * Returns string representation of edge.
	 */
	@Override
	public String toString() {
		return predicate.toString();
	}
}