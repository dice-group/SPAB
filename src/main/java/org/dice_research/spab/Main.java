package org.dice_research.spab;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Main entry point for CLI.
 * 
 * For Java usage use {@link SpabApi}.
 * 
 * @author Adrian Wilke
 */
public class Main {

	/**
	 * @param args [0] File with positive queries
	 * 
	 *             [1] File with negative queries
	 * 
	 *             [2] Output directory
	 * 
	 *             [3] SPAB maximum number of iterations
	 * 
	 *             [4] SPAB lambda
	 */
	public static void main(String[] args) throws NumberFormatException, IOException, SpabException {
		Main main = new Main();
		main.checkArgs(args);
		main.run(args[0], args[1], args[2], Integer.valueOf(args[3]), Float.valueOf(args[4]));
	}

	private void checkArgs(String[] args) {

		// Check required arguments

		if (args.length < 5) {
			System.err.println("Please provide:");
			System.err.println("- SPARQL file with positive set of queries");
			System.err.println("- SPARQL file with negative set of queries");
			System.err.println("- Output directory");
			System.err.println("- Maximum number of iterations");
			System.err.println("- Lambda");
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
		if (!new File(args[2]).canWrite()) {
			System.err.println("Can not write output directory: " + args[2]);
			System.exit(1);
		}

		// Check numbers

		try {
			Integer.valueOf(args[3]);
		} catch (NumberFormatException e) {
			System.err.println("Can not read number of iterations (integer): " + args[3]);
			System.exit(1);
		}

		try {
			Float.valueOf(args[4]);
		} catch (NumberFormatException e) {
			System.err.println("Can not read lambda (float): " + args[4]);
			System.exit(1);
		}
	}

	private void run(String inputQueriesPosFile, String inputQueriesNegFile, String outputDirectory, int maxIterations,
			float lambda) throws IOException, SpabException {

		// Set SPAB parameters and run

		SpabApi spabApi = new SpabApi();
		spabApi.setMaxIterations(maxIterations);
		spabApi.setLambda(lambda);
		for (String line : FileUtils.readLines(new File(inputQueriesPosFile), StandardCharsets.UTF_8)) {
			spabApi.addPositive(line);
		}
		for (String line : FileUtils.readLines(new File(inputQueriesNegFile), StandardCharsets.UTF_8)) {
			spabApi.addNegative(line);
		}
		spabApi.run();

		// Write results to file

		CandidateVertex bestCandidate = spabApi.getBestCandidates().get(0);

		String prefix = StringUtils.getCommonPrefix(
				new String[] { new File(inputQueriesPosFile).getName(), new File(inputQueriesNegFile).getName() });
		File outputFile = new File(outputDirectory, prefix + "spab.csv");

		CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputFile), CSVFormat.DEFAULT);
		csvPrinter.printRecord(new String[] { "regex", "" + bestCandidate.getCandidate().getRegEx() });
		csvPrinter.printRecord(new String[] { "fMeasure", "" + bestCandidate.getfMeasure() });
		csvPrinter.printRecord(new String[] { "score", "" + bestCandidate.getScore() });
		csvPrinter.printRecord(new String[] { "tp", "" + bestCandidate.getNumberOfTruePositives() });
		csvPrinter.printRecord(new String[] { "tn", "" + bestCandidate.getNumberOfTrueNegatives() });
		csvPrinter.printRecord(new String[] { "fp", "" + bestCandidate.getNumberOfFalsePositives() });
		csvPrinter.printRecord(new String[] { "fn", "" + bestCandidate.getNumberOfFalseNegatives() });
		csvPrinter.printRecord(new String[] { "generation", "" + bestCandidate.getGeneration() });
		csvPrinter.printRecord(new String[] { "number", "" + bestCandidate.getNumber() });
		csvPrinter.printRecord(new String[] { "lambda", "" + spabApi.getConfiguration().getLambda() });
		csvPrinter.printRecord(new String[] { "maxIterations", "" + spabApi.getConfiguration().getMaxIterations() });
		csvPrinter.printRecord(new String[] { "runtimeSecs", "" + spabApi.getRuntime() });
		csvPrinter.flush();
		csvPrinter.close();

		System.out.println(outputFile.getPath());
	}

}