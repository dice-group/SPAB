package org.dice_research.spab.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public abstract class CsvParser {

	/**
	 * Reads CSV files in given directory.
	 * 
	 * Returns Map<fileId, Map<CSVkey, CSVvalue>>
	 */
	public Map<String, Map<String, String>> parseDirectory(File directory, String fileExtension)
			throws FileNotFoundException, IOException {

		File[] files = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(fileExtension)) {
					return true;
				} else {
					return false;
				}
			}
		});

		Map<String, Map<String, String>> data = new HashMap<>();
		for (File file : files) {
			String fileId = file.getName().substring(0, file.getName().length() - fileExtension.length());
			data.put(fileId, parseFile(file));
		}

		return data;
	}

	/**
	 * Reads CSV-file in key-value format.
	 */
	protected Map<String, String> parseFile(File file) throws FileNotFoundException, IOException {
		CSVParser csvParser = CSVParser.parse(new FileReader(file), CSVFormat.DEFAULT);
		Map<String, String> data = new HashMap<>();
		for (CSVRecord csvRecord : csvParser) {
			if (csvRecord.size() != 2) {
				throw new IOException("Unknown CSV format. Record size: " + csvRecord.size());
			}
			data.put(csvRecord.get(0), csvRecord.get(1));
		}
		return data;
	}
}
