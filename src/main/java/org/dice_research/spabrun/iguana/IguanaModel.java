package org.dice_research.spabrun.iguana;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.tdb.TDBFactory;

/**
 * Model for Iguana data.
 * 
 * @see https://figshare.com/s/01a0dad8427c463f2b25
 * @see https://figshare.com/s/790f9441a36f15015252
 * 
 * @author Adrian Wilke
 */
public class IguanaModel {

	public static final String PREFIX_RESOURCE = "http://iguana-benchmark.eu/recource/";

	public static final String QUERY_ALL_EXPERIMENTS = "SELECT DISTINCT ?experiment\n"
			+ "WHERE { ?s <http://iguana-benchmark.eu/properties/experiment> ?experiment }\n" + "ORDER BY ?experiment";

	/**
	 * Jena RDF model.
	 */
	Model model;

	/**
	 * Jena TDB.
	 */
	Dataset dataset;

	/**
	 * Creates new in-memory model.
	 * 
	 * @param file
	 *            see {@link IguanaModel#read(String)}
	 */
	public IguanaModel(File file) {
		if (!file.canRead()) {
			throw new RuntimeException("Can not read file: " + file.getPath());
		}
		model = ModelFactory.createDefaultModel();
		model.read(file.getPath());
	}

	/**
	 * Creates new TDB and creates model based on file contents.
	 * 
	 * @param file
	 *            see {@link IguanaModel#read(String)}
	 */
	public IguanaModel(File file, File tdb) {
		if (!file.canRead()) {
			throw new RuntimeException("Can not read file: " + file.getPath());
		}
		Model localModel = ModelFactory.createDefaultModel();
		localModel.read(file.getPath());

		if (tdb.exists()) {
			if (!tdb.isDirectory()) {
				throw new RuntimeException("TDB is not a directory: " + tdb.getPath());
			} else if (!tdb.canRead()) {
				throw new RuntimeException("Can not read TDB directory: " + tdb.getPath());
			}
			dataset = TDBFactory.createDataset(tdb.getPath());
			model = dataset.getDefaultModel();
		} else {
			if (!tdb.mkdirs()) {
				throw new RuntimeException("Can not create TDB directory: " + tdb.getPath());
			}
			dataset = TDBFactory.createDataset(tdb.getPath());
			model = dataset.getDefaultModel();
			model.add(localModel);
		}
	}

	/**
	 * Gets all experiments in model.
	 */
	public List<Experiment> getAllExperiments() {
		List<Experiment> experiments = new LinkedList<Experiment>();
		for (String uri : getUris(QUERY_ALL_EXPERIMENTS, "experiment")) {
			experiments.add(new Experiment(this, uri));
		}
		return experiments;
	}

	/**
	 * Gets URIs of resources using the given query-string and return-variable.
	 */
	protected List<String> getUris(String queryString, String returnVariable) {
		List<String> uris = new LinkedList<String>();
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, this.model);
		ResultSet results = queryExecution.execSelect();
		while (results.hasNext()) {
			QuerySolution solution = results.next();
			Resource resource = solution.getResource(returnVariable);
			uris.add(resource.getURI());
		}
		return uris;
	}

	/**
	 * Gets URI of resources using the given query-string and return-variable.
	 */
	protected String getValue(String queryString, String returnVariable) {
		String value = null;
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.create(query, this.model);
		ResultSet results = queryExecution.execSelect();
		if (results.hasNext()) {
			QuerySolution solution = results.next();
			Literal resource = solution.getLiteral(returnVariable);
			value = resource.getString();
		} else {
			throw new RuntimeException("No result for: " + queryString + System.lineSeparator() + returnVariable);
		}
		if (results.hasNext()) {
			throw new RuntimeException(
					"Result is not unique: " + queryString + System.lineSeparator() + returnVariable);
		}
		return value;
	}

	/**
	 * Removes length of {@link IguanaModel#PREFIX_RESOURCE}.
	 */
	public String removeResourcePrefix(String uri) {
		if (uri.startsWith(PREFIX_RESOURCE)) {
			return uri.substring(PREFIX_RESOURCE.length());
		} else {
			throw new RuntimeException("Prefix not found in URI: " + uri);
		}
	}
}