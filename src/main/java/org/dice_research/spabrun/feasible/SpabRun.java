package org.dice_research.spabrun.feasible;

public class SpabRun {

	/**
	 * Main entry point.
	 * 
	 * @param args
	 *            [0] input directory with CSV files containing benchmark times.
	 *            Should contain folders 'individual-benchmarks' and
	 *            'mix-benchmarks'.
	 * 
	 *            [1] input directory with query files. Should contain folders
	 *            'individual-benchmarks' and 'mix-benchmarks'.
	 * 
	 *            [2] output directory
	 */
	public static void main(String[] args) {

		Configuration configuration = new Configuration();
		configuration.setDirectoryInputCsv(args[0]);
		configuration.setDirectoryInputQueries(args[1]);
		configuration.setDirectoryOutput(args[2]);

		System.out.println("Input directory CSV:     " + configuration.getDirectoryInputCsv());
		System.out.println("Input directory queries: " + configuration.getDirectoryInputQueries());
		System.out.println("Output directory:        " + configuration.getDirectoryOutput());

		DataAccessor benchmarkResults = new DataAccessor(configuration);

		Data swdfSel = benchmarkResults.getData(DataAccessor.BENCHMARK_SWDF, DataAccessor.QUERYTYPE_SELECT);

		// TODO: Use the data to produce sets of good/bad query sets
		// Different metrics for creating sets should be provided.

		System.out.println(swdfSel.getSourceOfQueries().getPath());
		System.out.println(swdfSel.getSourceOfValues().getPath());
		for (String string : swdfSel.queries) {
			System.out.println(string);
		}
		for (float[] val : swdfSel.values) {
			for (float f : val) {
				System.out.print(f + " ");
			}
			System.out.println();
		}
	}

}