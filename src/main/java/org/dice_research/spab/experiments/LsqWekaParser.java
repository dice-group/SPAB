package org.dice_research.spab.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class LsqWekaParser extends CsvParser {

	public final static String EXTENSION = "-weka.csv";

	/**
	 * Reads Weka CSV files in given directory.
	 * 
	 * Returns Map<fileId, Map<CSVkey, CSVvalue>>
	 */
	public Map<String, Map<String, String>> parseDirectory(File directory) throws FileNotFoundException, IOException {
		return parseDirectory(directory, EXTENSION);
	}
}