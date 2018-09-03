package org.dice_research.spab.benchmark;

import java.util.LinkedList;
import java.util.List;

/**
 * Container for benchmark data.
 * 
 * @author Adrian Wilke
 */
public class Benchmark {

	private String benchmarkId;
	private String comment;
	private List<TripleStore> tripleStores = new LinkedList<TripleStore>();
	private List<Query> queries = new LinkedList<Query>();
	private List<Runtime> runtimes = new LinkedList<Runtime>();

	public Benchmark(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		if (this.comment == null) {
			return "";
		} else {
			return this.comment;
		}
	}

	@Override
	public String toString() {
		return benchmarkId;
	}

	public TripleStore addTripleStore(String tripleStoreId) {
		TripleStore tripleStore = new TripleStore(tripleStoreId);
		this.tripleStores.add(tripleStore);
		return tripleStore;
	}

	public Query addQuery(String queryString) {
		Query query = new Query(queryString);
		this.queries.add(query);
		return query;
	}

	public Query addQuery(String queryString, String queryOptionalId) {
		Query query = new Query(queryString, queryOptionalId);
		this.queries.add(query);
		return query;
	}

	public Runtime addRuntime(TripleStore tripleStore, Query query, long runtime) {
		Runtime runtimeObj = new Runtime(tripleStore, query, runtime);
		this.runtimes.add(runtimeObj);
		return runtimeObj;
	}

	/**
	 * Returns object for given ID. Returns null, if ID is unknown.
	 */
	public TripleStore getTripleStore(String tripleStoreId) {
		for (TripleStore tripleStore : tripleStores) {
			if (tripleStore.getTripleStoreId().equals(tripleStoreId)) {
				return tripleStore;
			}
		}
		return null;
	}

	/**
	 * Returns object for given query string. Returns null, if string is unknown.
	 */
	public Query getQuery(String queryString) {
		for (Query query : queries) {
			if (query.getQueryString().equals(queryString)) {
				return query;
			}
		}
		return null;
	}

	/**
	 * Returns object for given optional ID. Returns null, if ID is unknown.
	 */
	public Query getQueryById(String queryOptionalId) {
		for (Query query : queries) {
			String idInObject = query.getQueryOptionalId();
			if (idInObject != null && idInObject.equals(queryOptionalId)) {
				return query;
			}
		}
		return null;
	}

	/**
	 * Returns runtime object for given objects. Returns null, if object combination
	 * is unknown.
	 */
	public Runtime getRuntime(TripleStore tripleStore, Query query) {
		for (Runtime runtime : runtimes) {
			if (runtime.getTripleStore().equals(tripleStore) && runtime.getQuery().equals(query)) {
				return runtime;
			}
		}
		return null;
	}
}