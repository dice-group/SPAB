package org.dice_research.spab.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

/**
 * Main entry point.
 * 
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * @param args See: {@link #checkArgs(String[])}
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Main main = new Main();
		main.checkArgs(args);
		main.run(args[0], args[1], args[2]);
	}

	private void checkArgs(String[] args) {

		// Check required arguments

		if (args.length < 3) {
			System.err.println("Please provide:");
			System.err.println("- LSQ/Weka CSV results directory");
			System.err.println("- SPAB CSV results directory");
			System.err.println("- CSV output file");
			System.exit(1);
		}

		// Check filesystem

		if (!new File(args[0]).canRead()) {
			System.err.println("Can not read " + args[0]);
			System.exit(1);
		}
		if (!new File(args[1]).canRead()) {
			System.err.println("Can not read " + args[1]);
			System.exit(1);
		}
	}

	private void run(String wekaDir, String spabDir, String outputFile) throws FileNotFoundException, IOException {
		Map<String, Map<String, String>> lsqwekaResults = new LsqWekaParser().parseDirectory(new File(wekaDir));
		Map<String, Map<String, String>> spabResults = new SpabParser().parseDirectory(new File(spabDir));
		CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT);

		// Create new dataset for IDs in all data sources

		List<String> combined = new LinkedList<>();

		combined.add("Data");

		combined.add("fMeasure LSQ/Weka");
		combined.add("fMeasure SPAB");

		combined.add("Runtime LSQ/Weka");
		combined.add("Runtime SPAB");

		combined.add("Runtime LSQ");
		combined.add("Runtime ARFF");
		combined.add("Runtime Weka");

		combined.add("SPAB regex");
		combined.add("SPAB Generation");
		combined.add("SPAB Number");

		csvPrinter.printRecord(combined);
		combined.clear();

		int processedItems = 0;
		for (String id : lsqwekaResults.keySet()) {
			if (spabResults.containsKey(id)) {
				Map<String, String> l = lsqwekaResults.get(id);
				Map<String, String> s = spabResults.get(id);

				combined.add(id);

				combined.add(l.get("fMeasure"));
				combined.add(s.get("fMeasure"));

				combined.add("" + (Long.parseLong(l.get("timeLsq")) + Long.parseLong(l.get("timeArff"))
						+ Long.parseLong(l.get("timeWeka"))));
				combined.add("" + (Float.valueOf(s.get("runtimeSecs")) * 1000));

				combined.add(l.get("timeLsq"));
				combined.add(l.get("timeArff"));
				combined.add(l.get("timeWeka"));

				combined.add(s.get("regex"));
				combined.add(s.get("generation"));
				combined.add(s.get("number"));

				csvPrinter.printRecord(combined);
				combined.clear();
				processedItems++;
			}
		}
		csvPrinter.close();

		System.out.println(wekaDir);
		System.out.println(spabDir);
		System.out.println(outputFile);

		System.out.println("LSQ/Weka files:  " + lsqwekaResults.size());
		System.out.println("SPAB files:      " + spabResults.size());
		System.out.println("Processed items: " + processedItems);
	}
}