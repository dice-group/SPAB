package org.aksw.spab.input;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.aksw.spab.exceptions.ParseException;
import org.apache.jena.query.Query;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileUtils;
import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;

/**
 * Representations for a single SPARQL query.
 * 
 * @author Adrian Wilke
 */
public class InputQuery {

	final static private boolean DEBUG_STATEMENTS = false;
	final static private boolean DEBUG_TRIPLES = false;
	final static private String LINE_SEPARATOR = System.getProperty("line.separator");

	// The original input
	final private String originalQuery;

	// The set this query belongs to
	final private Input inputSet;

	// The model of the query
	private Model model;

	// The parsed query
	private Query query;

	// Hierarchical representation of the query
	private InputGraph graph;

	private String queryCache = null;

	public InputQuery(String sparqlQuery, Input inputSet) {
		this.originalQuery = sparqlQuery;
		this.inputSet = inputSet;
	}

	/**
	 * Creates models/representations for the query.
	 * 
	 * @throws ParseException
	 *             if root element in the statement-representation of the query is
	 *             not found or if non-expected triples are found
	 */
	public void createModel() throws ParseException {

		// Create model
		model = ModelFactory.createDefaultModel();
		Iterator<Entry<String, String>> i = inputSet.getNamespaces().entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, String> nameToUri = i.next();
			model.setNsPrefix(nameToUri.getKey(), nameToUri.getValue());
		}

		// Create SPIN query
		// Changes model, which is important for building the graph
		query = ARQFactory.get().createQuery(model, originalQuery);
		ARQ2SPIN arq2spin = new ARQ2SPIN(model);
		arq2spin.createQuery(query, null);

		// Build up graph from statements in model
		createGraph();
	}

	/**
	 * Builds up graph based on statements in model.
	 * 
	 * @throws ParseException
	 *             if root element in the statement-representation of the query is
	 *             not found
	 */
	private void createGraph() throws ParseException {

		graph = new InputGraph();

		// Container for subject-predicate-object-triples
		class Triple {
			public Resource subject;
			public Property predicate;
			public RDFNode object;

			public Triple(Resource subject, Property predicate, RDFNode object) {
				this.subject = subject;
				this.predicate = predicate;
				this.object = object;
			}
		}

		// Map resource IDs to related triples
		Map<String, Set<Triple>> resourceIdToTriples = new HashMap<String, Set<Triple>>();

		// Container for identifiers of resources, which are anonymous
		Set<String> anonymousResources = new HashSet<String>();

		if (DEBUG_STATEMENTS) {
			System.out.println();
			for (Statement statement : model.listStatements().toSet()) {
				System.out.println(statement);
			}
			System.out.println();
		}

		// Iterate through statements and get needed data
		StmtIterator iterator = model.listStatements();
		while (iterator.hasNext()) {
			Statement statement = iterator.next();

			// Remember triples
			String subjectId = statement.getSubject().toString();
			if (!resourceIdToTriples.containsKey(subjectId)) {
				resourceIdToTriples.put(subjectId, new HashSet<Triple>());
			}
			resourceIdToTriples.get(subjectId)
					.add(new Triple(statement.getSubject(), statement.getPredicate(), statement.getObject()));

			// Remember IDs of anonymous resources
			if (statement.getObject().isAnon()) {
				anonymousResources.add(statement.getObject().toString());
			}
		}

		// Determine root
		// This is contained in statements, but never on object-side of triples
		Set<String> resourceIds = new HashSet<String>(resourceIdToTriples.keySet());
		resourceIds.removeAll(anonymousResources);
		if (resourceIds.size() != 1) {
			throw new ParseException("Root not found.");
		}
		String rootId = resourceIds.toArray(new String[0])[0];

		if (DEBUG_TRIPLES) {
			System.out.println();
			Set<Entry<String, Set<Triple>>> entries = resourceIdToTriples.entrySet();
			for (Entry<String, Set<Triple>> entry : entries) {
				System.out.println(entry.getKey());
				for (Triple triple : entry.getValue()) {
					System.out.print("  " + triple.predicate);
					System.out.println("  " + triple.object);
				}
			}
			System.out.println();
		}

		// Add triples to graph
		Map<String, InputVertex> resourceIdToVertex = new HashMap<String, InputVertex>();
		Queue<Triple> tripleQueue = new LinkedList<Triple>();
		resourceIdToVertex.put(rootId, graph.getRoot());
		tripleQueue.addAll(resourceIdToTriples.get(rootId));
		while (!tripleQueue.isEmpty()) {
			Triple triple = tripleQueue.remove();
			String subjectId = triple.subject.toString();
			if (triple.object.isAnon()) {
				InputVertex newVertex = graph.createTriple(resourceIdToVertex.get(subjectId), triple.predicate, null);
				resourceIdToVertex.put(triple.object.toString(), newVertex);
				tripleQueue.addAll(resourceIdToTriples.get(triple.object.toString()));
			} else {
				graph.createTriple(resourceIdToVertex.get(subjectId), triple.predicate, triple.object);
				if (resourceIdToTriples.get(triple.object.toString()) != null) {
					throw new ParseException("Found triple(s) in non-anonymous resource.");
				}
			}
		}
	}

	/**
	 * Gets SPIN representation in Turtle syntax.
	 */
	public String getSpin() {
		Writer writer = new StringWriter();
		model.write(writer, FileUtils.langTurtle);
		return writer.toString();
	}

	/**
	 * Gets the SPARQL query.
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Gets the hierarchical representation of the query.
	 */
	public InputGraph getGraph() {
		return graph;
	}

	/**
	 * Removes prefixes in SPARQL query and returns resulting string.
	 */
	public String getQueryWithoutPrefixes() {
		if (queryCache == null) {
			String lines[] = getQuery().toString().split("\\r?\\n");
			boolean firstLine = true;
			StringBuffer sb = new StringBuffer();
			for (String line : lines) {
				if (!line.startsWith("PREFIX") && !line.isEmpty()) {
					if (firstLine) {
						firstLine = false;
					} else {
						sb.append(LINE_SEPARATOR);
					}
					sb.append(line);
				}
			}
			queryCache = sb.toString();
		}
		return queryCache;
	}
}