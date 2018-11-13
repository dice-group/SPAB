package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class Result {

	private TripleStore tripleStore;
	private Query query;
	private double result;

	public Result(TripleStore tripleStore, Query query, Double result) throws BenchmarkNullException {
		if (tripleStore == null) {
			throw new BenchmarkNullException("tripleStore is null");
		}
		if (query == null) {
			throw new BenchmarkNullException("query is null");
		}
		if (result == null) {
			throw new BenchmarkNullException("result is null");
		}
		this.query = query;
		this.tripleStore = tripleStore;
		this.result = result;
	}

	@Override
	public String toString() {
		return String.valueOf(result);
	}

	public TripleStore getTripleStore() {
		return tripleStore;
	}

	public Query getQuery() {
		return query;
	}

	public double getResult() {
		return result;
	}
}