package org.dice_research.spab.benchmark;

/**
 * Container class maintained by {@link Benchmark}.
 * 
 * @author Adrian Wilke
 */
public class TripleStore {

	public static final String TRIPLE_STORE_ID = "tripleStoreId";

	private String tripleStoreId;

	public TripleStore(String tripleStoreId) throws BenchmarkNullException {
		if(tripleStoreId == null) {
			throw new BenchmarkNullException("tripleStoreId is null");
		}
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