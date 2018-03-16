package org.dice_research.spabrun.iguana;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dice_research.spab.io.FileWriter;

/**
 * Extracts Iguana queries.
 * 
 * @see https://figshare.com/s/01a0dad8427c463f2b25
 * @see https://figshare.com/s/790f9441a36f15015252
 * 
 * @author Adrian Wilke
 */
public class IguanaExtractor {

	public final static boolean DEBUG = false;

	protected IguanaModel iguanaModel;

	protected Map<String, List<Integer>> negative = new HashMap<String, List<Integer>>();
	protected Map<String, List<Integer>> positive = new HashMap<String, List<Integer>>();

	protected Set<String> tdbFilter = new HashSet<String>();

	public IguanaExtractor(IguanaModel iguanaModel) {
		this.iguanaModel = iguanaModel;
	}

	/**
	 * Adds TDB listed in {@link Task}, e.g. {@link Task#CONNECTION_FUSEKI}
	 */
	public void addTdb(String tdb) {
		tdbFilter.add(tdb);
	}

	/**
	 * Fills {@link #positive.entrySet()} and {@link #negative} query examples for
	 * TDBs based on non-parallel executed queries.
	 * 
	 * @param range
	 *            is added to/subtracted from average queries-per-second
	 */
	public void generateQueryIndexes(double range) {

		// Get non-parallel workers
		List<Worker> workersNonParallel = getQpsWorkers(1);

		// Sort queries by index and filter by TDB
		Map<Integer, List<QueriesPerSecond>> queryMap = new HashMap<Integer, List<QueriesPerSecond>>();
		for (Worker worker : workersNonParallel) {
			for (QueriesPerSecond query : worker.getQueriesPerSecond()) {
				if (!queryMap.containsKey(query.getIndex())) {
					queryMap.put(query.getIndex(), new LinkedList<QueriesPerSecond>());
				}
				if (tdbFilter.contains(query.getConnection())) {
					queryMap.get(query.getIndex()).add(query);
				}
			}
		}

		// Filter queries by TBD
		for (int queryIndex = queryMap.size() - 1; queryIndex >= 0; queryIndex--) {
			if (queryMap.get(queryIndex).size() != tdbFilter.size()) {
				queryMap.remove(queryIndex);
			}
		}

		// Compare queries
		for (String tdb : tdbFilter) {
			positive.put(tdb, new LinkedList<Integer>());
			negative.put(tdb, new LinkedList<Integer>());
		}
		for (int queryIndex = 0; queryIndex < queryMap.size(); queryIndex++) {
			if (queryMap.containsKey(queryIndex)) {

				// Sort by queries per second
				List<QueriesPerSecond> qpsList = queryMap.get(queryIndex);
				if (qpsList.size() == tdbFilter.size()) {
					qpsList.sort(new Comparator<QueriesPerSecond>() {
						public int compare(QueriesPerSecond a, QueriesPerSecond b) {
							return a.getQueriesPerSecondValue().compareTo(b.getQueriesPerSecondValue());
						}
					});
				}

				// Arithmetical mean
				double mean = 0;
				for (QueriesPerSecond queriesPerSecond : qpsList) {
					mean += queriesPerSecond.getQueriesPerSecondValue();
				}
				mean /= qpsList.size();

				// Filter positive/negative queries
				for (QueriesPerSecond queriesPerSecond : qpsList) {
					if (queriesPerSecond.getQueriesPerSecondValue() > mean * (1 + range)) {
						positive.get(queriesPerSecond.getConnection()).add(queriesPerSecond.getIndex());
					} else if (queriesPerSecond.getQueriesPerSecondValue() < mean * (1 - range)) {
						negative.get(queriesPerSecond.getConnection()).add(queriesPerSecond.getIndex());
					}
				}
			}
		}

		if (DEBUG) {
			for (Entry<String, List<Integer>> e : positive.entrySet()) {
				System.out.println(e.getKey() + " " + e.getValue());
			}
			for (Entry<String, List<Integer>> e : negative.entrySet()) {
				System.out.println(e.getKey() + " " + e.getValue());
			}
		}
	}

	protected List<Experiment> getExperiments() {
		return iguanaModel.getAllExperiments();
	}

	public List<String> getNegatives(String tdb, List<String> queries) {
		List<String> queryList = new LinkedList<String>();
		for (Integer queryIndex : negative.get(tdb)) {
			queryList.add(queries.get(queryIndex));
		}
		return queryList;
	}

	public List<String> getPositives(String tdb, List<String> queries) {
		List<String> queryList = new LinkedList<String>();
		for (Integer queryIndex : positive.get(tdb)) {
			queryList.add(queries.get(queryIndex));
		}
		return queryList;
	}

	protected List<Worker> getQpsWorkers() {
		List<Worker> workers = new LinkedList<Worker>();
		for (Task task : getTasks()) {
			workers.addAll(task.getQpsWorkers());
		}
		return workers;
	}

	protected List<Worker> getQpsWorkers(int numberOfWorkersInRelatedTask) {
		List<Worker> returnList = new LinkedList<Worker>();
		for (Task task : getTasks()) {
			List<Worker> qpsWorkers = task.getQpsWorkers();
			if (qpsWorkers.size() == numberOfWorkersInRelatedTask) {
				for (int w = 0; w < qpsWorkers.size(); w++) {
					returnList.add(qpsWorkers.get(w));
				}
			}
		}
		return returnList;
	}

	protected List<Task> getTasks() {
		List<Task> tasks = new LinkedList<Task>();
		for (Experiment experiment : iguanaModel.getAllExperiments()) {
			tasks.addAll(experiment.getTasks());
		}
		return tasks;
	}

	public void writeDbpediaQueryFiles(List<String> dbpediaQueries, File directory) {
		StringBuilder builderNegative = new StringBuilder();
		StringBuilder builderPositive = new StringBuilder();
		for (String tdb : tdbFilter) {
			for (Integer queryIndex : negative.get(tdb)) {
				builderNegative.append(dbpediaQueries.get(queryIndex));
				builderNegative.append(System.lineSeparator());
			}
			FileWriter.writeStringToFile(builderNegative.toString(),
					new File(directory, tdb + "-negative" + ".txt").getPath(), true);

			for (Integer queryIndex : positive.get(tdb)) {
				builderPositive.append(dbpediaQueries.get(queryIndex));
				builderPositive.append(System.lineSeparator());
			}
			FileWriter.writeStringToFile(builderPositive.toString(),
					new File(directory, tdb + "-positive" + ".txt").getPath(), true);
		}
	}
}