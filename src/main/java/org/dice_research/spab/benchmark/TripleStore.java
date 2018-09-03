package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class TripleStore {

	private String tripleStoreId;

	public TripleStore(String tripleStoreId) {
		this.tripleStoreId = tripleStoreId;
	}

	@Override
	public String toString() {
		return tripleStoreId;
	}

	public String getTripleStoreId() {
		return tripleStoreId;
	}
}