package org.dice_research.spabrun.iguana;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.IoRuntimeException;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Generates Iguana queries of interest, imports query strings, and runs SPAB
 * variations.
 * 
 * @author Adrian Wilke
 */
public class SpabRun {

	public static void main(String[] args) throws SpabException {

		SpabRun spabRun = new SpabRun();

		// Load DBpedia query strings
		List<String> dbpediaQueries = spabRun.getQueries(Configuration.DBPEDIA_QUERIES_2018_01_20);

		// Set TDBs to use
		List<String> tdbs = new LinkedList<String>();
		tdbs.add(Task.CONNECTION_FUSEKI);
		tdbs.add(Task.CONNECTION_TNT);
		tdbs.add(Task.CONNECTION_VIRTUOSO);

		// Extract query indexes
		spabRun.createIguanaExtractor(tdbs, 0.5);

		// SPAB runs
		spabRun.run(Task.CONNECTION_FUSEKI, dbpediaQueries, false);
		spabRun.run(Task.CONNECTION_TNT, dbpediaQueries, false);
		spabRun.run(Task.CONNECTION_VIRTUOSO, dbpediaQueries, false);

		// SPAB runs with inverted positives and negatives
		spabRun.run(Task.CONNECTION_FUSEKI, dbpediaQueries, true);
		spabRun.run(Task.CONNECTION_TNT, dbpediaQueries, true);
		spabRun.run(Task.CONNECTION_VIRTUOSO, dbpediaQueries, true);
	}

	protected Configuration configuration;
	protected IguanaExtractor iguana;
	protected IguanaModel iguanaModel;

	public SpabRun() {
		this.configuration = new Configuration();
		createIguanaModel();
	}

	public IguanaExtractor createIguanaExtractor(List<String> tdbs, double range) {
		iguana = new IguanaExtractor(iguanaModel);
		for (String tdb : tdbs) {
			iguana.addTdb(tdb);
		}
		iguana.generateQueryIndexes(range);
		return iguana;
	}

	public void createIguanaModel() {
		File dbpediaResults = getFile(Configuration.DBPEDIA_RESULTS_2018_01_20);
		if (configuration.get(Configuration.TDB) != null) {
			// In-memory TDB
			iguanaModel = new IguanaModel(dbpediaResults);
		} else {
			// File-based TDB
			iguanaModel = new IguanaModel(dbpediaResults, getDirectory(Configuration.TDB, true));
		}
	}

	protected File getDirectory(String key, boolean createIfNotExists) {
		String value = configuration.get(key);
		if (value == null) {
			throw new IoRuntimeException("Can not find key: " + key);
		}

		File dir = new File(value);
		if (!dir.exists()) {
			if (createIfNotExists) {
				dir.mkdirs();
			} else {
				throw new IoRuntimeException("Directory not found: " + dir.getPath());
			}
		}
		if (!dir.canRead()) {
			throw new IoRuntimeException("Can not read directory: " + dir.getPath());
		} else if (!dir.isDirectory()) {
			throw new IoRuntimeException("Directory not found: " + dir.getPath());
		}
		return dir;
	}

	protected File getFile(String key) {
		String value = configuration.get(key);
		if (value == null) {
			throw new IoRuntimeException("Can not find key: " + key);
		}
		File file = new File(value);
		if (!file.canRead()) {
			throw new IoRuntimeException("Can not read file: " + file.getPath());
		}
		return file;
	}

	public List<String> getQueries(String key) {
		return FileReader.readFileToList(getFile(key).getPath(), true, StandardCharsets.UTF_8.name());
	}

	public void run(String tdb, List<String> queries, boolean invert) throws SpabException {
		SpabApi spabApi = new SpabApi();
		if (invert) {
			for (String query : iguana.getPositives(tdb, queries)) {
				spabApi.addNegative(query);
			}
			for (String query : iguana.getNegatives(tdb, queries)) {
				spabApi.addPositive(query);
			}
		} else {
			for (String query : iguana.getPositives(tdb, queries)) {
				spabApi.addPositive(query);
			}
			for (String query : iguana.getNegatives(tdb, queries)) {
				spabApi.addNegative(query);
			}
		}

		spabApi.setLambda(0.1f);
		CandidateVertex bestCandidate;

		bestCandidate = spabApi.run();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());

		bestCandidate = spabApi.getQueue().pollBestCandidate();
		System.out.println(bestCandidate.getCandidate().getRegEx());
		System.out.println(bestCandidate.getScore());
	}
}