package org.dice_research.spab.iguana;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Iguana Task.
 * 
 * Experiment <http://iguana-benchmark.eu/properties/task> Task
 * 
 * URI template: <http://iguana-benchmark.eu/recource/##/#/#>
 * 
 * @author Adrian Wilke
 */
public class Task {

	public static final String CONNECTION_FUSEKI = "Fuseki";
	public static final String CONNECTION_N_GRAPHSTORE = "N-graphStore";
	public static final String CONNECTION_NGRAPHSTORE = "NgraphStore";
	public static final String CONNECTION_TNT = "TNT";
	public static final String CONNECTION_VIRTUOSO = "Virtuoso";

	protected static Set<String> connections = new HashSet<String>();

	public static final String QUERY_CONNECTION = "SELECT DISTINCT ?connection\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/properties/connection> ?connection }";
	public static final String QUERY_QPS = "SELECT DISTINCT ?qps\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/recource/QPS> ?qps\n"
			+ ". ?qps <http://iguana-benchmark.eu/properties/queriesPerSecond> ?value }\n" + "ORDER BY ?qps";

	public static final String QUERY_QPS_WORKERS = "SELECT DISTINCT ?worker\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/recource/QPS> ?worker\n"
			+ ". ?worker <http://iguana-benchmark.eu/properties/workerType> '" + Worker.WORKER_TYPE_SPARQL + "' }\n"
			+ "ORDER BY ?worker";

	/**
	 * Returns all connections found so far.
	 */
	public static Set<String> getAllConnections() {
		return connections;
	}

	protected String connection;
	protected Experiment experiment;
	protected IguanaModel model;

	protected String uri;

	public Task(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	public Task(IguanaModel model, String uri, Experiment experiment) {
		this.model = model;
		this.uri = uri;
		this.experiment = experiment;
	}

	public String getConnection() {
		if (connection == null) {
			List<String> connections = model.getUris(QUERY_CONNECTION.replace("#URI#", "<" + uri + ">"), "connection");
			if (connections.size() != 1) {
				throw new RuntimeException("No unique connection for " + this + ": " + connections);
			} else {
				connection = model.removeResourcePrefix(connections.get(0));
				Task.connections.add(connection);
			}
		}
		return connection;
	}

	public Experiment getExperiment() {
		return experiment;
	}

	public List<Worker> getQpsWorkers() {
		List<Worker> workers = new LinkedList<Worker>();
		for (String uri : model.getUris(QUERY_QPS_WORKERS.replace("#URI#", "<" + uri + ">"), "worker")) {
			workers.add(new Worker(model, uri, this));
		}
		return workers;
	}

	public List<QueriesPerSecond> getQueriesPerSecond() {
		List<QueriesPerSecond> qps = new LinkedList<QueriesPerSecond>();
		for (String uri : model.getUris(QUERY_QPS.replace("#URI#", "<" + uri + ">"), "qps")) {
			qps.add(new QueriesPerSecond(model, uri));
		}
		return qps;
	}

	@Override
	public String toString() {
		return uri;
	}
}