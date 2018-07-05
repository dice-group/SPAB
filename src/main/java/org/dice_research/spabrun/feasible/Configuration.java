package org.dice_research.spabrun.feasible;

import java.io.File;

/**
 * Configuration container.
 * 
 * @author Adrian Wilke
 */
public class Configuration {

	private String directoryInputCsv;
	private String directoryInputQueries;
	private String directoryOutput;

	public void setDirectoryInputCsv(String directory) {
		File file = new File(directory);
		if (!file.canRead()) {
			throw new RuntimeException("Can not read directory: " + file);
		}
		this.directoryInputCsv = file.getPath();
	}

	public void setDirectoryInputQueries(String directory) {
		File file = new File(directory);
		if (!file.canRead()) {
			throw new RuntimeException("Can not read directory: " + file);
		}
		this.directoryInputQueries = file.getPath();
	}

	public void setDirectoryOutput(String directory) {
		File file = new File(directory);
		if (!file.canWrite()) {
			throw new RuntimeException("Can not write directory: " + file);
		}
		this.directoryOutput = file.getPath();
	}

	public String getDirectoryInputCsv() {
		return directoryInputCsv;
	}

	public String getDirectoryInputQueries() {
		return directoryInputQueries;
	}

	public String getDirectoryOutput() {
		return directoryOutput;
	}
}