package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class Query {

	private String queryId;
	private String queryString;

	public Query(String queryId, String queryString) {
		this.queryId = queryId;
		this.queryString = queryString;
	}

	@Override
	public String toString() {
		return "Q" + queryId;
	}

	public String getQueryId() {
		return queryId;
	}

	public String getQueryString() {
		return queryString;
	}
}