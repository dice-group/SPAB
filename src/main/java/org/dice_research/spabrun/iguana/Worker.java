package org.dice_research.spabrun.iguana;

import java.util.LinkedList;
import java.util.List;

/**
 * Iguana SPARQL Worker.
 * 
 * Worker <http://iguana-benchmark.eu/properties/workerType> 'SPARQLWorker'
 * 
 * URI template: <http://iguana-benchmark.eu/recource/##/#/#/-#########>
 * 
 * @author Adrian Wilke
 */
public class Worker {

	public static final String QUERY_QPS = "SELECT DISTINCT ?qps\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/properties/qps#query> ?qps }\n" + "ORDER BY ?qps";

	public static final String WORKER_TYPE_SPARQL = "SPARQLWorker";

	protected IguanaModel model;
	protected List<QueriesPerSecond> qps;
	protected Task task;
	protected String uri;

	public Worker(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	public Worker(IguanaModel model, String uri, Task task) {
		this.model = model;
		this.uri = uri;
		this.task = task;
	}

	public List<QueriesPerSecond> getQueriesPerSecond() {
		if (qps == null) {
			qps = new LinkedList<QueriesPerSecond>();
			for (String uri : model.getUris(QUERY_QPS.replace("#URI#", "<" + uri + ">"), "qps")) {
				qps.add(new QueriesPerSecond(model, uri, this));
			}
		}
		return qps;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public String toString() {
		return uri;
	}
}