package org.aksw.spab.input;

import java.util.Set;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Hierarchical representation of a SPARQL query in SPIN format.
 * 
 * @author Adrian Wilke
 */
public class InputGraph {

	private Graph<InputVertex, InputEdge> graph;
	private InputVertex root;

	/**
	 * Initializes graph and creates root vertex.
	 */
	public InputGraph() {

		// Create graph
		graph = new DefaultDirectedGraph<InputVertex, InputEdge>(InputEdge.class);

		// Create root vertex
		root = new InputVertex(null);
		graph.addVertex(root);
	}

	/**
	 * Creates vertex containing resource. Creates edge containing predicate from
	 * sourceVertex to new vertex. Returns new vertex.
	 */
	public InputVertex createTriple(InputVertex sourceVertex, Property predicate, RDFNode resource) {
		InputVertex vertex = new InputVertex(resource);
		InputEdge edge = new InputEdge(predicate);
		graph.addVertex(vertex);
		graph.addEdge(sourceVertex, vertex, edge);
		return vertex;
	}

	/**
	 * Gets root vertex.
	 */
	public InputVertex getRoot() {
		return root;
	}

	/**
	 * Gets string representation of input graph.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		return toString(root, sb, 0).toString();
	}

	/**
	 * Recursively builds string for vertex.
	 */
	private StringBuilder toString(InputVertex vertex, StringBuilder sb, int indent) {

		if (vertex.resource != null) {
			sb.append("\n");
			for (int i = 0; i < indent; i++) {
				sb.append("|  ");
			}
			indent += 1;
		}
		sb.append(vertex);

		Set<InputEdge> edgesOut = graph.outgoingEdgesOf(vertex);

		for (InputEdge edgeOut : edgesOut) {
			sb.append("\n");
			for (int i = 0; i < indent; i++) {
				sb.append("|  ");
			}
			sb.append(edgeOut.toString());
			toString(graph.getEdgeTarget(edgeOut), sb, indent + 1);
		}
		return sb;
	}
}