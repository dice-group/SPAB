package org.dice_research.spabrun.iguana;

/**
 * Iguana QPS.
 * 
 * Task <http://iguana-benchmark.eu/recource/QPS> QPS
 * 
 * URI template:
 * <http://iguana-benchmark.eu/recource/##/#/#/-#########/sparql###>
 * 
 * @author Adrian Wilke
 */
public class QueriesPerSecond {

	public static final String QUERY_QPS_VALUE = "SELECT DISTINCT ?value\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/properties/queriesPerSecond> ?value }";

	protected IguanaModel model;
	protected Double qps;
	protected String uri;
	protected Worker worker;
	protected Integer index;

	public QueriesPerSecond(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	public QueriesPerSecond(IguanaModel model, String uri, Worker worker) {
		this.model = model;
		this.uri = uri;
		this.worker = worker;
	}

	/**
	 * Gets connection from related worker and task.
	 */
	public String getConnection() {
		return worker.getTask().getConnection();
	}

	/**
	 * Gets query index extracted from URI
	 */
	public Integer getIndex() {
		if (index == null) {
			index = Integer.parseInt(uri.substring(uri.lastIndexOf("/sparql") + "/sparql".length()));
		}
		return index;
	}

	public Double getQueriesPerSecondValue() {
		if (qps == null) {
			qps = Double.parseDouble(model.getValue(QUERY_QPS_VALUE.replace("#URI#", "<" + uri + ">"), "value"));
		}
		return qps;
	}

	@Override
	public String toString() {
		return uri;
	}
}