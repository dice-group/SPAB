package org.dice_research.spab.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.FileWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Container for benchmark data.
 * 
 * Creation: Use constructor and add methods to set: Benchmark id and comment,
 * triple stores, queries, and results.
 * 
 * Load: Use read methods.
 * 
 * Store: Use write method or toJson.
 * 
 * @author Adrian Wilke
 */
public class Benchmark {

	public static final String JSON_BENCHMARK_ID = "benchmarkId";
	public static final String JSON_BENCHMARK_COMMENT = "comment";
	public static final String JSON_TRIPLE_STORES = "tripleStores";
	public static final String JSON_TRIPLE_STORE = "tripleStore";
	public static final String JSON_QUERIES = "queries";
	public static final String JSON_QUERY = "query";
	public static final String JSON_RESULTS = "results";
	public static final String JSON_RESULT = "result";

	private String benchmarkId;
	private String comment;
	private List<TripleStore> tripleStores = new LinkedList<TripleStore>();
	private List<Query> queries = new LinkedList<Query>();
	private List<Result> results = new LinkedList<Result>();

	private Map<TripleStore, List<Result>> triplestoreToResults;
	private Map<Query, List<Result>> queriesToResults;

	public Benchmark(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public TripleStore addTripleStore(String tripleStoreId) throws BenchmarkNullException {
		TripleStore tripleStore = new TripleStore(tripleStoreId);
		this.tripleStores.add(tripleStore);
		return tripleStore;
	}

	public Query addQuery(String queryId, String queryString) throws BenchmarkNullException {
		Query query = new Query(queryId, queryString);
		this.queries.add(query);
		return query;
	}

	public Result addResult(TripleStore tripleStore, Query query, double result) throws BenchmarkNullException {
		Result resultObj = new Result(tripleStore, query, result);
		this.results.add(resultObj);
		return resultObj;
	}

	public String getBenchmarkId() {
		return this.benchmarkId;
	}

	public String getComment() {
		if (this.comment == null) {
			return "";
		} else {
			return this.comment;
		}
	}

	public List<TripleStore> getTripleStores() {
		return tripleStores;
	}

	/**
	 * Gets list of triple store IDs.
	 */
	public List<String> getTripleStoreIds() {
		List<String> tripleStoreIds = new LinkedList<String>();
		for (TripleStore tripleStore : tripleStores) {
			tripleStoreIds.add(tripleStore.getTripleStoreId());
		}
		return tripleStoreIds;
	}

	/**
	 * Maps IDs to objects.
	 */
	public Map<String, TripleStore> getTripleStoresMap() {
		Map<String, TripleStore> map = new HashMap<String, TripleStore>();
		for (TripleStore tripleStore : tripleStores) {
			map.put(tripleStore.getTripleStoreId(), tripleStore);
		}
		return map;
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

	public List<Query> getQueries() {
		return queries;
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
	public Query getQueryById(String queryId) {
		for (Query query : queries) {
			String idInObject = query.getQueryId();
			if (idInObject != null && idInObject.equals(queryId)) {
				return query;
			}
		}
		return null;
	}

	public List<Result> getResults() {
		return results;
	}

	/**
	 * Returns result object for given objects. Returns null, if object combination
	 * is unknown.
	 */
	public Result getResult(TripleStore tripleStore, Query query) {
		for (Result result : results) {
			if (result.getTripleStore().equals(tripleStore) && result.getQuery().equals(query)) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Gets map TripleStore object to list of Result objects.
	 * 
	 * Uses cache.
	 */
	public Map<TripleStore, List<Result>> getResultsOrderedbyTripleStores() {
		if (triplestoreToResults == null) {
			triplestoreToResults = new LinkedHashMap<TripleStore, List<Result>>();
			for (TripleStore tripleStore : tripleStores) {
				triplestoreToResults.put(tripleStore, new LinkedList<Result>());
			}
			for (Result result : results) {
				triplestoreToResults.get(result.getTripleStore()).add(result);
			}
		}
		return triplestoreToResults;
	}

	/**
	 * Gets map TripleStore object to list of Result objects.
	 * 
	 * Uses cache.
	 */
	public Map<Query, List<Result>> getResultsOrderedbyQueries() {
		if (queriesToResults == null) {
			queriesToResults = new LinkedHashMap<Query, List<Result>>();
			for (Query query : queries) {
				queriesToResults.put(query, new LinkedList<Result>());
			}
			for (Result result : results) {
				queriesToResults.get(result.getQuery()).add(result);
			}
		}
		return queriesToResults;
	}

	/**
	 * JSON deserialization of benchmark.
	 */
	public static Benchmark readJson(String json) throws BenchmarkNullException {
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode;
		try {
			jsonNode = objectMapper.readTree(json);
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		}

		Benchmark benchmark = new Benchmark(jsonNode.get(JSON_BENCHMARK_ID).asText());
		benchmark.setComment(jsonNode.get(JSON_BENCHMARK_COMMENT).asText());

		Map<String, TripleStore> tripleStoreMap = new HashMap<String, TripleStore>();
		Iterator<JsonNode> it = jsonNode.get(JSON_TRIPLE_STORES).elements();
		while (it.hasNext()) {
			JsonNode tripleStoreNode = it.next();
			String id = tripleStoreNode.get(TripleStore.TRIPLE_STORE_ID).asText();
			tripleStoreMap.put(id, benchmark.addTripleStore(id));
		}

		Map<String, Query> queryMap = new HashMap<String, Query>();
		it = jsonNode.get(JSON_QUERIES).elements();
		while (it.hasNext()) {
			JsonNode queryNode = it.next();
			String id = queryNode.get(Query.QUERY_ID).asText();
			queryMap.put(id, benchmark.addQuery(id, queryNode.get(Query.QUERY_STRING).asText()));
		}

		it = jsonNode.get(JSON_RESULTS).elements();
		while (it.hasNext()) {
			JsonNode resultNode = it.next();
			TripleStore tripleStore = tripleStoreMap.get(resultNode.get(JSON_TRIPLE_STORE).asText());
			Query query = queryMap.get(resultNode.get(JSON_QUERY).asText());
			benchmark.addResult(tripleStore, query, resultNode.get(JSON_RESULT).asLong());
		}

		return benchmark;
	}

	/**
	 * JSON deserialization of benchmark.
	 */
	public static Benchmark readJsonFile(String filePath) throws BenchmarkNullException {
		return readJson(FileReader.readFileToString(filePath, StandardCharsets.UTF_8));
	}

	/**
	 * JSON serialization of benchmark.
	 * 
	 * @see https://www.baeldung.com/jackson
	 */
	public String toJson() {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			JsonGenerator jsonGenerator = new JsonFactory().createGenerator(byteArrayOutputStream);
			jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());

			jsonGenerator.writeStartObject();

			jsonGenerator.writeFieldName(JSON_BENCHMARK_ID);
			jsonGenerator.writeString(this.benchmarkId);

			jsonGenerator.writeFieldName(JSON_BENCHMARK_COMMENT);
			jsonGenerator.writeString(this.comment);

			jsonGenerator.writeFieldName(JSON_TRIPLE_STORES);
			objectMapper.writeValue(jsonGenerator, this.tripleStores);

			jsonGenerator.writeFieldName(JSON_QUERIES);
			objectMapper.writeValue(jsonGenerator, this.queries);

			jsonGenerator.writeFieldName(JSON_RESULTS);
			jsonGenerator.writeStartArray();
			for (Result result : this.results) {
				jsonGenerator.writeStartObject();

				jsonGenerator.writeFieldName(JSON_TRIPLE_STORE);
				objectMapper.writeValue(jsonGenerator, result.getTripleStore().getTripleStoreId());

				jsonGenerator.writeFieldName(JSON_QUERY);
				objectMapper.writeValue(jsonGenerator, result.getQuery().getQueryId());

				jsonGenerator.writeFieldName(JSON_RESULT);
				objectMapper.writeValue(jsonGenerator, result.getResult());

				jsonGenerator.writeEndObject();
			}
			jsonGenerator.writeEndArray();

			jsonGenerator.writeEndObject();

			jsonGenerator.flush();
			jsonGenerator.close();

			String jsonString = new String(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();
			return jsonString;
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		}
	}

	/**
	 * JSON serialization of benchmark.
	 */
	public void writeJsonFile(String filePath) {
		FileWriter.writeStringToFile(toJson(), filePath, true);
	}

	@Override
	public String toString() {
		return benchmarkId;
	}
}