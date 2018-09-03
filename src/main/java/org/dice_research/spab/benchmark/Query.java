package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class Query {

	private String queryString;
	private String queryOptionalId;

	public Query(String queryString) {
		this.queryString = queryString;
	}

	public Query(String queryString, String queryOptionialId) {
		this.queryString = queryString;
		this.queryOptionalId = queryOptionialId;
	}

	@Override
	public String toString() {
		if (queryOptionalId == null) {
			return this.queryString;
		} else {
			return queryOptionalId;
		}
	}

	public String getQueryString() {
		return queryString;
	}

	/**
	 * Returns optional ID or null.
	 */
	public String getQueryOptionalId() {
		return queryOptionalId;
	}
}