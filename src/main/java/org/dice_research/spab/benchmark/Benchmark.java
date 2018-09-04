package org.dice_research.spab.benchmark;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.FileWriter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Container for benchmark data.
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
	public static final String JSON_RUNTIMES = "runtimes";
	public static final String JSON_RUNTIME = "runtime";

	private String benchmarkId;
	private String comment;
	private List<TripleStore> tripleStores = new LinkedList<TripleStore>();
	private List<Query> queries = new LinkedList<Query>();
	private List<Runtime> runtimes = new LinkedList<Runtime>();

	public Benchmark(String benchmarkId) {
		this.benchmarkId = benchmarkId;
	}

	public String getBenchmarkId() {
		return this.benchmarkId;
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

	public Query addQuery(String queryId, String queryString) {
		Query query = new Query(queryId, queryString);
		this.queries.add(query);
		return query;
	}

	public Runtime addRuntime(TripleStore tripleStore, Query query, long runtime) {
		Runtime runtimeObj = new Runtime(tripleStore, query, runtime);
		this.runtimes.add(runtimeObj);
		return runtimeObj;
	}

	public List<TripleStore> getTripleStores() {
		return tripleStores;
	}

	public List<Query> getQueries() {
		return queries;
	}

	public List<Runtime> getRuntimes() {
		return runtimes;
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
	public Query getQueryById(String queryId) {
		for (Query query : queries) {
			String idInObject = query.getQueryId();
			if (idInObject != null && idInObject.equals(queryId)) {
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

			jsonGenerator.writeFieldName(JSON_RUNTIMES);
			jsonGenerator.writeStartArray();
			for (Runtime runtime : this.runtimes) {
				jsonGenerator.writeStartObject();

				jsonGenerator.writeFieldName(JSON_TRIPLE_STORE);
				objectMapper.writeValue(jsonGenerator, runtime.getTripleStore().getTripleStoreId());

				jsonGenerator.writeFieldName(JSON_QUERY);
				objectMapper.writeValue(jsonGenerator, runtime.getQuery().getQueryId());

				jsonGenerator.writeFieldName(JSON_RUNTIME);
				objectMapper.writeValue(jsonGenerator, runtime.getRuntime());

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

	/**
	 * JSON deserialization of benchmark.
	 * 
	 * @return
	 * 
	 * @throws IOException
	 * @throws JsonParseException
	 */
	public static Benchmark readJson(String json) {
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

		it = jsonNode.get(JSON_RUNTIMES).elements();
		while (it.hasNext()) {
			JsonNode runtimeNode = it.next();
			TripleStore tripleStore = tripleStoreMap.get(runtimeNode.get(JSON_TRIPLE_STORE).asText());
			Query query = queryMap.get(runtimeNode.get(JSON_QUERY).asText());
			benchmark.addRuntime(tripleStore, query, runtimeNode.get(JSON_RUNTIME).asLong());
		}

		return benchmark;
	}

	/**
	 * JSON deserialization of benchmark.
	 */
	public static Benchmark readJsonFile(String filePath) {
		return readJson(FileReader.readFileToString(filePath, StandardCharsets.UTF_8));
	}
}