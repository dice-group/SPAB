package org.dice_research.spab.iguana;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dice_research.spab.exceptions.InputRuntimeException;

/**
 * Creates Iguana test queries.
 * 
 * @see https://figshare.com/s/01a0dad8427c463f2b25
 * @see https://figshare.com/s/790f9441a36f15015252
 * 
 * @author Adrian Wilke
 */
public class Iguana {

	public final static boolean DEBUG = false;

	public final static String FILENAME_DBPEDIA = "dbpedia.txt";
	public final static String FILENAME_DBPEDIA_NTRIPLES = "dbpedia_results.nt";
	public final static String FILENAME_SWDF = "swdf.txt";
	public final static String FILENAME_SWDF_NTRIPLES = "swdf_results.nt";

	protected static Set<String> tdbFilter = new HashSet<String>();

	static {
		tdbFilter.add(Task.CONNECTION_FUSEKI);
		tdbFilter.add(Task.CONNECTION_TNT);
		tdbFilter.add(Task.CONNECTION_VIRTUOSO);
	}

	/**
	 * Main entry point.
	 * 
	 * Argument 1: Directory containing query strings in files
	 * {@link #FILENAME_DBPEDIA} and {@link #FILENAME_SWDF}
	 * 
	 * Argument 2: Directory containing query strings in files
	 * {@link #FILENAME_DBPEDIA_NTRIPLES} and {@link #FILENAME_SWDF_NTRIPLES}
	 * 
	 * Argument 3 (optional): Directory for TDB files
	 * 
	 * Will write result files to directory given in args[0]
	 */
	public static void main(String[] args) {

		File spabIguanaTdb = null;
		if (args.length == 3) {
			spabIguanaTdb = new File(args[2]);
		}

		if (args.length >= 2 && args.length <= 3) {
			File ntFileDbpedia = new File(args[1], FILENAME_DBPEDIA_NTRIPLES);
			@SuppressWarnings("unused")
			File ntFileSwdf = new File(args[1], FILENAME_SWDF_NTRIPLES);

			IguanaModel iguanaModel = null;
			if (spabIguanaTdb == null) {
				// In-memory TDB
				iguanaModel = new IguanaModel(ntFileDbpedia);
			} else {
				// File-based TDB
				iguanaModel = new IguanaModel(ntFileDbpedia, spabIguanaTdb);
			}

			// Get positive and negative example out of TDB
			Iguana iguana = new Iguana(iguanaModel);
			iguana.generateQueryIndexes();

			// Get query-strings
			File queriesFolder = new File(args[0]);
			File queriesFileDbpedia = new File(queriesFolder, FILENAME_DBPEDIA);
			File queriesFileSwdf = new File(queriesFolder, FILENAME_SWDF);
			List<String> dbpediaQueries = readFile(queriesFileDbpedia.getPath(), StandardCharsets.UTF_8.name());
			@SuppressWarnings("unused")
			List<String> swdfQueries = readFile(queriesFileSwdf.getPath(), StandardCharsets.UTF_8.name());

			iguana.writeDbpediaQueryFiles(dbpediaQueries, queriesFolder);
		}
	}

	protected static List<String> readFile(String filePath, String charsetName) {
		File file = new File(filePath);
		if (!file.canRead()) {
			throw new RuntimeException("Can not read file: " + filePath);
		}

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			List<String> lines = new LinkedList<String>();

			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
			return lines;

		} catch (IOException ioException) {
			throw new InputRuntimeException(ioException);
		} finally {
			try {
				fileInputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			} catch (IOException closeException) {
				throw new InputRuntimeException(closeException);
			}
		}
	}

	protected static void writeStringToFile(String string, String filePath) {
		new File(filePath).getParentFile().mkdirs();
		try {
			PrintWriter out = new PrintWriter(filePath);
			out.println(string);
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected IguanaModel iguanaModel;

	protected Map<String, List<Integer>> negative = new HashMap<String, List<Integer>>();
	protected Map<String, List<Integer>> positive = new HashMap<String, List<Integer>>();

	public Iguana(IguanaModel iguanaModel) {
		this.iguanaModel = iguanaModel;
	}

	/**
	 * Fills {@link #positive.entrySet()} and {@link #negative} query examples for
	 * TDBs based on non-parallel executed queries.
	 */
	public void generateQueryIndexes() {
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
					if (queriesPerSecond.getQueriesPerSecondValue() > mean * 1.2) {
						positive.get(queriesPerSecond.getConnection()).add(queriesPerSecond.getIndex());
					} else if (queriesPerSecond.getQueriesPerSecondValue() < mean * 0.8) {
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

	public List<Experiment> getExperiments() {
		return iguanaModel.getAllExperiments();
	}

	public List<Worker> getQpsWorkers() {
		List<Worker> workers = new LinkedList<Worker>();
		for (Task task : getTasks()) {
			workers.addAll(task.getQpsWorkers());
		}
		return workers;
	}

	public List<Worker> getQpsWorkers(int numberOfWorkersInRelatedTask) {
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

	public List<Task> getTasks() {
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
			writeStringToFile(builderNegative.toString(), new File(directory, tdb + "-negative" + ".txt").getPath());

			for (Integer queryIndex : positive.get(tdb)) {
				builderPositive.append(dbpediaQueries.get(queryIndex));
				builderPositive.append(System.lineSeparator());
			}
			writeStringToFile(builderPositive.toString(), new File(directory, tdb + "-positive" + ".txt").getPath());
		}
	}
}