package structures;

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

	protected int depth = CandidateVertex.START_GENERATION;
	protected Graph<CandidateVertex, DefaultEdge> graph;
	protected CandidateVertex root = null;

	/**
	 * Initializes graph.
	 */
	public CandidateGraph() {
		graph = new DefaultDirectedGraph<CandidateVertex, DefaultEdge>(DefaultEdge.class);
	}

	/**
	 * Adds candidate vertex, which represents root of the graph.
	 * 
	 * @throws SpabException
	 *             if a second root vertex is found
	 */
	public void addCandidate(CandidateVertex candidate) throws SpabException {
		addCandidate(candidate, null);
	}

	/**
	 * Adds candidate vertex. If parent is set, adds edge. Updates graph depth by
	 * generation of candidate.
	 * 
	 * @throws SpabException
	 *             if a second root vertex is found
	 */
	public void addCandidate(CandidateVertex candidate, CandidateVertex parent) throws SpabException {

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
	public void addCandidates(Collection<CandidateVertex> candidates, CandidateVertex parent) throws SpabException {
		for (CandidateVertex candidate : candidates) {
			addCandidate(candidate, parent);
		}
	}

	/**
	 * Get all vertices in the graph.
	 */
	public Set<CandidateVertex> getAllCandidates() {
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
	public Graph<CandidateVertex, DefaultEdge> getGraph() {
		return graph;
	}

	/**
	 * Gets root vertex of graph.
	 */
	public CandidateVertex getRoot() {
		return root;
	}
}