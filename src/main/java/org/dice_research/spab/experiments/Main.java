package org.dice_research.spab.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;

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

		combined.add("Dataset");

		combined.add("SPAB\nfMeasure");
		combined.add("FEAT\nfMeasure");

		combined.add("SPAB\nRuntime");
		combined.add("FEAT\nRuntime");

		combined.add("SPAB\nScore");
		combined.add("SPAB\nRegEx");
		combined.add("SPAB\nIteration");
		combined.add("SPAB\nGeneration");
		combined.add("SPAB\nNumber");
		combined.add("SPAB\nTP");
		combined.add("SPAB\nTN");
		combined.add("SPAB\nFP");
		combined.add("SPAB\nFN");

		combined.add("FEAT\nRuntime LSQ");
		combined.add("FEAT\nRuntime ARFF");
		combined.add("FEAT\nRuntime Weka");
		combined.add("FEAT\nFeatures");

		csvPrinter.printRecord(combined);
		combined.clear();

		// Canonicalize indexes and sort keys
		Map<String, String> prefixesToTitle = new HashMap<>();
		for (String key : lsqwekaResults.keySet()) {
			int firstDigitIndex = key.length() - 1;
			try {
				while (Integer.parseInt(key.substring(firstDigitIndex, firstDigitIndex + 1)) != -1) {
					// Loop will throw exception on non-integer
					firstDigitIndex--;
				}
			} catch (NumberFormatException e) {
				firstDigitIndex++;
			}
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(key.substring(0, firstDigitIndex));
			for (int i = key.substring(firstDigitIndex, key.length()).length(); i < 3; i++) {
				stringBuilder.append("0");
			}
			stringBuilder.append(key.substring(firstDigitIndex, key.length()));
			prefixesToTitle.put(key, stringBuilder.toString());
		}

		Set<String> allFeatures = new TreeSet<>();
		int processedItems = 0;
		for (String id : lsqwekaResults.keySet()) {
			if (spabResults.containsKey(id)) {
				Map<String, String> l = lsqwekaResults.get(id);
				Map<String, String> s = spabResults.get(id);

				combined.add(prefixesToTitle.get(id));

				combined.add(s.get("fMeasure"));
				if (l.get("fMeasure").equals("NaN")) {
					combined.add("0");
				} else {
					combined.add(l.get("fMeasure"));
				}

				combined.add("" + (Float.valueOf(s.get("runtimeSecs")) * 1000));
				combined.add("" + (Long.parseLong(l.get("timeLsq")) + Long.parseLong(l.get("timeArff"))
						+ Long.parseLong(l.get("timeWeka"))));

				combined.add(s.get("score"));
				combined.add(s.get("regex"));
				combined.add(s.get("iteration"));
				combined.add(s.get("generation"));
				combined.add(s.get("number"));
				combined.add(s.get("tp"));
				combined.add(s.get("tn"));
				combined.add(s.get("fp"));
				combined.add(s.get("fn"));

				combined.add(l.get("timeLsq"));
				combined.add(l.get("timeArff"));
				combined.add(l.get("timeWeka"));
				combined.add(l.get("features"));

				csvPrinter.printRecord(combined);
				combined.clear();

				allFeatures.addAll(parseListString(l.get("features")));
				processedItems++;
			}
		}
		csvPrinter.close();

		File allFeaturesFile = new File(new File(outputFile).getPath() + ".features");
		FileUtils.writeLines(allFeaturesFile, allFeatures);

		System.out.println(wekaDir);
		System.out.println(spabDir);
		System.out.println(outputFile);
		System.out.println(allFeaturesFile);

		System.out.println("LSQ/Weka files:  " + lsqwekaResults.size());
		System.out.println("SPAB files:      " + spabResults.size());
		System.out.println("Processed items: " + processedItems);
	}

	private List<String> parseListString(String string) {
		List<String> list = new LinkedList<>();
		Iterator<Object> it = new StringTokenizer(string.substring(1, string.length() - 1), ",").asIterator();
		while (it.hasNext()) {
			list.add(it.next().toString().trim());
		}
		return list;
	}
}