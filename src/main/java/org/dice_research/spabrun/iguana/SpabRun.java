package org.dice_research.spabrun.iguana;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.dice_research.spab.io.FileReader;

public class SpabRun {

	public final static String FILENAME_DBPEDIA = "dbpedia.txt";
	public final static String FILENAME_DBPEDIA_NTRIPLES = "dbpedia_results.nt";
	public final static String FILENAME_SWDF = "swdf.txt";
	public final static String FILENAME_SWDF_NTRIPLES = "swdf_results.nt";

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
			IguanaExtractor iguana = new IguanaExtractor(iguanaModel);
			iguana.addTdb(Task.CONNECTION_FUSEKI);
			iguana.addTdb(Task.CONNECTION_TNT);
			iguana.addTdb(Task.CONNECTION_VIRTUOSO);
			iguana.generateQueryIndexes(0.5);

			// Get query-strings
			File queriesFolder = new File(args[0]);
			File queriesFileDbpedia = new File(queriesFolder, FILENAME_DBPEDIA);
			File queriesFileSwdf = new File(queriesFolder, FILENAME_SWDF);
			List<String> dbpediaQueries = FileReader.readFileToList(queriesFileDbpedia.getPath(), true,
					StandardCharsets.UTF_8.name());
			@SuppressWarnings("unused")
			List<String> swdfQueries = FileReader.readFileToList(queriesFileSwdf.getPath(), true,
					StandardCharsets.UTF_8.name());

			// TODO
			// iguana.getPositives(Task.CONNECTION_FUSEKI, dbpediaQueries);
			// iguana.getNegatives(Task.CONNECTION_FUSEKI, dbpediaQueries);

			System.out.println(iguana.getPositives(Task.CONNECTION_FUSEKI, dbpediaQueries).size());
			System.out.println(iguana.getNegatives(Task.CONNECTION_FUSEKI, dbpediaQueries).size());

			System.out.println(iguana.getPositives(Task.CONNECTION_VIRTUOSO, dbpediaQueries).size());
			System.out.println(iguana.getNegatives(Task.CONNECTION_VIRTUOSO, dbpediaQueries).size());

			System.out.println(iguana.getPositives(Task.CONNECTION_TNT, dbpediaQueries).size());
			System.out.println(iguana.getNegatives(Task.CONNECTION_TNT, dbpediaQueries).size());

			// iguana.writeDbpediaQueryFiles(dbpediaQueries, queriesFolder);
		}
	}

}