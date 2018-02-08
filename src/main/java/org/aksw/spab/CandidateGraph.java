package org.aksw.spab;

import java.util.Collection;

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

	private Graph<Candidate, DefaultEdge> graph;
	private Candidate root = null;
	private int depth = 0;

	/**
	 * Initializes graph.
	 */
	public CandidateGraph() {
		graph = new DefaultDirectedGraph<Candidate, DefaultEdge>(DefaultEdge.class);
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

	/**
	 * Gets depth of graph.
	 */
	public int getDepth() {
		return depth;
	}
}