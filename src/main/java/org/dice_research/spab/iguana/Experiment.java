package org.dice_research.spab.iguana;

import java.util.LinkedList;
import java.util.List;

/**
 * Iguana Experiment.
 * 
 * Resource <http://iguana-benchmark.eu/properties/experiment> Experiment
 * 
 * URI template: <http://iguana-benchmark.eu/recource/##/#>
 * 
 * @author Adrian Wilke
 */
public class Experiment {

	public static final String QUERY_DATASET = "SELECT DISTINCT ?dataset\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/properties/dataset> ?dataset }\n" + "ORDER BY ?dataset";

	public static final String QUERY_TASKS = "SELECT DISTINCT ?task\n"
			+ "WHERE { #URI# <http://iguana-benchmark.eu/properties/task> ?task }\n"
			+ "ORDER BY ?task";

	protected IguanaModel model;
	protected String uri;

	public Experiment(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	public String getDataset() {
		List<String> datasets = model.getUris(QUERY_DATASET.replace("#URI#", "<" + uri + ">"), "dataset");
		if (datasets.size() != 1) {
			throw new RuntimeException("No unique dataset for " + this + ": " + datasets);
		} else {
			return model.removeResourcePrefix(datasets.get(0));
		}
	}

	public List<Task> getTasks() {
		List<Task> tasks = new LinkedList<Task>();
		for (String uri : model.getUris(QUERY_TASKS.replace("#URI#", "<" + uri + ">"), "task")) {
			tasks.add(new Task(model, uri, this));
		}
		return tasks;
	}

	@Override
	public String toString() {
		return uri;
	}
}