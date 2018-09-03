package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class Runtime {

	private TripleStore tripleStore;
	private Query query;
	private long runtime;

	public Runtime(TripleStore tripleStore, Query query, long runtime) {
		this.query = query;
		this.tripleStore = tripleStore;
		this.runtime = runtime;
	}

	@Override
	public String toString() {
		return String.valueOf(runtime);
	}

	public TripleStore getTripleStore() {
		return tripleStore;
	}

	public Query getQuery() {
		return query;
	}

	public long getRuntime() {
		return runtime;
	}
}