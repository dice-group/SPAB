package org.dice_research.spabrun.iguana;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.dice_research.spab.exceptions.IoRuntimeException;

/**
 * Iguana configuration
 * 
 * @author Adrian Wilke
 */
public class Configuration {

	public final static String DBPEDIA_QUERIES_2018_01_20 = "dbpedia_queries_2018-01-20";
	public final static String DBPEDIA_RESULTS_2018_01_20 = "dbpedia_results_2018-01-20";
	public final static String PROPERTIES = "iguana-2018-01-20/file-locations.properties";
	public final static String SWDF_QUERIES_2018_01_20 = "swdf_queries_2018-01-20";
	public final static String SWDF_RESULTS_2018_01_20 = "swdf_results_2018-01-20";
	public final static String TDB = "tdb";

	protected Properties properties;

	public Configuration() {
		loadProperties();
	}

	public String get(String key) {
		return properties.getProperty(key);
	}

	public void loadProperties() {
		ClassLoader classLoader = Configuration.class.getClassLoader();
		File propertiesFile = new File(classLoader.getResource(PROPERTIES).getFile());
		properties = new Properties();
		try {
			properties.load(new FileInputStream(propertiesFile));
		} catch (IOException e) {
			throw new IoRuntimeException(e);
		}
	}
}