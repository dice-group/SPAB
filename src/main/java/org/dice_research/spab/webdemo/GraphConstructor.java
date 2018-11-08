package org.dice_research.spab.webdemo;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.six.CandidateSix;
import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Creates data to display JS graph
 * 
 * @author Adrian Wilke
 */
public class GraphConstructor {

	protected Float maxFmeasure = Float.MIN_VALUE;
	protected Float minFmeasure = Float.MAX_VALUE;

	// Container for construction
	protected static class Vertex implements Comparable<Vertex> {
		int number;
		String regex;
		String hierarchy;
		float fMeasure;

		@Override
		public int compareTo(Vertex vertex) {
			return Integer.compare(number, vertex.number);
		}
	}

	// Container for construction
	protected static class Edge implements Comparable<Edge> {
		String id;
		int source;
		int target;

		static String createId(int source, int target) {
			return source + "-" + target;
		}

		@Override
		public int compareTo(Edge edge) {
			int sourceComp = Integer.compare(source, edge.source);
			if (sourceComp == 0) {
				return Integer.compare(target, edge.target);
			} else {
				return sourceComp;
			}
		}
	}

	public String construct(SpabApi spabApi, int maxBestCandidates) {

		// Create list of best candidates to use
		List<CandidateVertex> bestCandidates = spabApi.getBestCandidates();
		maxBestCandidates = Math.min(maxBestCandidates, bestCandidates.size());
		bestCandidates = bestCandidates.subList(0, maxBestCandidates);

		SortedMap<Integer, Vertex> vertices = new TreeMap<Integer, Vertex>();
		SortedMap<String, Edge> edges = new TreeMap<String, Edge>();

		for (CandidateVertex candidate : bestCandidates) {

			// Traverse from candidate to root
			traverseLoop: while (candidate != null) {
				CandidateVertex candidateParent = candidate.getParent();

				// Add unknown vertex
				Vertex vertex = null;
				if (!vertices.containsKey(candidate.getNumber())) {
					vertex = createVertex(candidate);
					vertices.put(vertex.number, vertex);
				}

				// Candidate is root, there is no parent to create an edge
				if (candidateParent == null) {
					break traverseLoop;
				}

				// Add unknown parent of vertex
				Vertex vertexParent = null;
				if (!vertices.containsKey(candidateParent.getNumber())) {
					vertexParent = createVertex(candidateParent);
					vertices.put(vertexParent.number, vertexParent);
				}

				// Ensure knowledge of vertices
				if (vertex == null) {
					vertex = vertices.get(candidate.getNumber());
				}
				if (vertexParent == null) {
					vertexParent = vertices.get(candidate.getParent().getNumber());
				}

				// Add edge, if at least one new vertex created
				if (!edges.containsKey(Edge.createId(vertexParent.number, vertex.number))) {
					Edge edge = new Edge();
					edge.id = Edge.createId(vertexParent.number, vertex.number);
					edge.source = vertexParent.number;
					edge.target = vertex.number;
					edges.put(edge.id, edge);
				}

				// Next iteration
				candidate = candidate.getParent();
			}

		}

		return generateJs(new TreeSet<Vertex>(vertices.values()), new TreeSet<Edge>(edges.values()));
	}

	protected Vertex createVertex(CandidateVertex candidateVertex) {
		Vertex vertex = new Vertex();
		vertex.number = candidateVertex.getNumber();
		vertex.regex = StringEscapeUtils.escapeHtml4(candidateVertex.getCandidate().getRegEx());
		vertex.fMeasure = candidateVertex.getfMeasure();

		Candidate<?> candidate = candidateVertex.getCandidate();
		if (candidate instanceof CandidateSix) {
			@SuppressWarnings("unchecked")
			Candidate<Expression> castedCandidate = (Candidate<Expression>) candidate;
			Expression expression = castedCandidate.getInternalRepresentation(Expression.class);

			StringBuilder stringBuilder = new StringBuilder();
			expression.getHierarchy(stringBuilder);
			vertex.hierarchy = StringEscapeUtils.escapeHtml4(stringBuilder.toString());
		} else {
			vertex.hierarchy = "";
		}

		// Update max and min of fMeasures
		if (vertex.fMeasure < minFmeasure) {
			minFmeasure = vertex.fMeasure;
		}
		if (vertex.fMeasure > maxFmeasure) {
			maxFmeasure = vertex.fMeasure;
		}

		return vertex;
	}

	protected String generateJs(SortedSet<Vertex> vertices, SortedSet<Edge> edges) {

		// Format:
		// cy.add([
		// { group: "nodes", data: { id: "n0", title: "n0" } },
		// { group: "nodes", data: { id: "n1", title: "n1" } },
		// { group: "edges", data: { id: "e0", source: "n0", target: "n1" } }
		// ]);

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("cy.add([");
		stringBuilder.append(System.lineSeparator());

		boolean firstIteration = true;
		for (Vertex vertex : vertices) {
			if (firstIteration) {
				firstIteration = false;
			} else {
				stringBuilder.append(",");
			}
			stringBuilder.append(System.lineSeparator());

			stringBuilder.append("{ group: \"nodes\", data: { id: \"" + vertex.number

					+ "\", title: \"" + vertex.number

					+ "\", hierarchy: \"" + vertex.hierarchy.replaceAll(System.lineSeparator(), "<br />")

					+ "\", regex: \"" + vertex.regex

					+ "\", fmeasure: " + vertex.fMeasure

					+ " } }");
		}

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("]);");
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("cy.add([");
		stringBuilder.append(System.lineSeparator());

		firstIteration = true;
		for (Edge edge : edges) {
			if (firstIteration) {
				firstIteration = false;
			} else {
				stringBuilder.append(",");
			}
			stringBuilder.append(System.lineSeparator());

			stringBuilder.append(
					"{ group: \"edges\", data: { source: \"" + edge.source + "\", target: \"" + edge.target + "\" } }");
		}

		stringBuilder.append(System.lineSeparator());
		stringBuilder.append("]);");
		stringBuilder.append(System.lineSeparator());

		return stringBuilder.toString();
	}

	/**
	 * Gets value to color good candidates.
	 * 
	 * Best candidates with good fM should be separated from 1 (very saturated) and
	 * from those with bad fM.
	 */
	float getMaxValue() {
		if (maxFmeasure.equals(Float.MIN_VALUE)) {
			return 1f;
		} else {
			return (2f + maxFmeasure) / 3f;
		}
	}

	/**
	 * Gets value to color bad candidates.
	 * 
	 * Best candidates with bad fM should be separated from 0 (grey) and from those
	 * with good fM.
	 */
	float getMinValue() {
		if (minFmeasure.equals(Float.MAX_VALUE)) {
			return 0f;
		} else {
			return minFmeasure / 2f;
		}
	}
}