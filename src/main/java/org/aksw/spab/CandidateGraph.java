package org.aksw.spab;

import java.util.Collection;
import java.util.Set;

import org.aksw.spab.exceptions.SpabException;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * Graph holding kinship of candidates.
 * 
 * @author Adrian Wilke
 */
public class CandidateGraph {

	protected int depth = 0;
	protected Graph<Candidate, DefaultEdge> graph;
	protected Candidate root = null;

	/**
	 * Initializes graph.
	 */
	public CandidateGraph() {
		graph = new DefaultDirectedGraph<Candidate, DefaultEdge>(DefaultEdge.class);
	}

	/**
	 * Adds candidate vertex, which represents root of the graph.
	 * 
	 * @throws SpabException
	 *             if a second root vertex is found
	 */
	public void addCandidate(Candidate candidate) throws SpabException {
		addCandidate(candidate, null);
	}

	/**
	 * Adds candidate vertex. If parent is set, adds edge. Updates graph depth by
	 * generation of candidate.
	 * 
	 * @throws SpabException
	 *             if a second root vertex is found
	 */
	public void addCandidate(Candidate candidate, Candidate parent) throws SpabException {

		// Add vertex to graph
		graph.addVertex(candidate);

		// Add edge to graph
		// Remember root vertex
		if (parent != null) {
			graph.addEdge(parent, candidate);
		} else {
			if (root == null) {
				root = candidate;
			} else {
				throw new SpabException("Second root vertex found.");
			}
		}

		// Update graph depth
		if (candidate.getGeneration() > getDepth()) {
			depth = candidate.getGeneration();
		}
	}

	/**
	 * Adds multiple candidate vertices. If parent is set, adds edges. Updates graph
	 * depth by generation of candidates.
	 * 
	 * @throws SpabException
	 *             if a second root vertex is found
	 */
	public void addCandidates(Collection<Candidate> candidates, Candidate parent) throws SpabException {
		for (Candidate candidate : candidates) {
			addCandidate(candidate, parent);
		}
	}

	/**
	 * Get all vertices in the graph.
	 */
	public Set<Candidate> getAllCandidates() {
		return graph.vertexSet();
	}

	/**
	 * Gets depth of graph.
	 */
	public int getDepth() {
		return depth;
	}

	/**
	 * Gets underling graph.
	 */
	public Graph<Candidate, DefaultEdge> getGraph() {
		return graph;
	}

	/**
	 * Gets root vertex of graph.
	 */
	public Candidate getRoot() {
		return root;
	}
}