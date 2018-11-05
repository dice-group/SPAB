package org.dice_research.spab.webdemo;

import java.util.List;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringEscapeUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.structures.CandidateVertex;

public abstract class GraphConstructor {

	// Container for construction
	static class Vertex implements Comparable<Vertex> {
		int number;
		String regex;
		float fMeasure;

		@Override
		public int compareTo(Vertex vertex) {
			return Integer.compare(number, vertex.number);
		}
	}

	// Container for construction
	static class Edge implements Comparable<Edge> {
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

	static String construct(SpabApi spabApi, int maxBestCandidates) {

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
					vertex = new Vertex();
					vertex.number = candidate.getNumber();
					vertex.regex = StringEscapeUtils.escapeHtml4(candidate.getCandidate().getRegEx());
					vertex.fMeasure = candidate.getfMeasure();
					vertices.put(vertex.number, vertex);
				}

				// Candidate is root, there is no parent to create an edge
				if (candidateParent == null) {
					break traverseLoop;
				}

				// Add unknown parent of vertex
				Vertex vertexParent = null;
				if (!vertices.containsKey(candidateParent.getNumber())) {
					vertexParent = new Vertex();
					vertexParent.number = candidateParent.getNumber();
					vertexParent.regex = StringEscapeUtils.escapeHtml4(candidateParent.getCandidate().getRegEx());
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

	protected static String generateJs(SortedSet<Vertex> vertices, SortedSet<Edge> edges) {

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

			stringBuilder.append("{ group: \"nodes\", data: { id: \"" + vertex.number + "\", title: \"" + vertex.number
					+ "\", regex: \"" + vertex.regex +"\", fmeasure: " + vertex.fMeasure + " } }");
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

}
