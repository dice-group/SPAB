package org.aksw.spab;

import java.util.Collection;

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
	 */
	public void addCandidate(Candidate candidate, Candidate parent) {
		graph.addVertex(candidate);
		if (parent != null) {
			graph.addEdge(parent, candidate);
		}
		if (candidate.getGeneration() > getDepth()) {
			depth = candidate.getGeneration();
		}
	}

	/**
	 * Adds multiple candidate vertices. If parent is set, adds edges. Updates graph
	 * depth by generation of candidates.
	 */
	public void addCandidates(Collection<Candidate> candidates, Candidate parent) {
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
	 * Gets depth of graph.
	 */
	public int getDepth() {
		return depth;
	}
}