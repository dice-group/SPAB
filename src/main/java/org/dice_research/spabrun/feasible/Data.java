package org.dice_research.spabrun.feasible;

import java.io.File;

/**
 * Data container.
 * 
 * @author Adrian Wilke
 */
public class Data {

	protected String[] queries;
	protected float[][] values;
	protected File sourceOfQueries;
	protected File sourceOfValues;

	public void setQueries(String[] queries) {
		this.queries = queries;
	}

	public void setValues(float[][] values) {
		this.values = values;
	}

	public void setSourceOfQueries(File sourceOfQueries) {
		this.sourceOfQueries = sourceOfQueries;
	}

	public void setSourceOfValues(File sourceOfValues) {
		this.sourceOfValues = sourceOfValues;
	}

	public File getSourceOfQueries() {
		return sourceOfQueries;
	}

	public File getSourceOfValues() {
		return sourceOfValues;
	}
}