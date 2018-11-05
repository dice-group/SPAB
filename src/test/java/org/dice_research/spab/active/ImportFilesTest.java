package org.dice_research.spab.active;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.AbstractTestCase;
import org.dice_research.spab.exceptions.UnitTestRuntimeException;

/**
 * Tests import of example files.
 * 
 * @author Adrian Wilke
 */
public class ImportFilesTest extends AbstractTestCase {

	protected static List<String> dbpediaAskQueries;
	protected static List<String> dbpediaConstructQueries;
	protected static List<String> dbpediaDescribeQueries;
	protected static List<String> dbpediaSelectQueries;
	protected final static String QUERIES_ASK = "dbpedia-ask-100.txt";

	protected final static String QUERIES_CONSTRUCT = "dbpedia-construct-100.txt";
	protected final static String QUERIES_DESCRIBE = "dbpedia-describe-25.txt";
	protected final static String QUERIES_SELECT = "dbpedia-select-100.txt";
	protected final static String SEPARATOR = "#";

	public static List<String> getDbpediaAskQueries() {
		if (dbpediaAskQueries == null) {
			File fileSelect = getResource(QUERIES_ASK);
			dbpediaAskQueries = readFile(fileSelect.getPath(), StandardCharsets.UTF_8.name());
		}
		return dbpediaAskQueries;
	}

	public static List<String> getDbpediaConstructQueries() {
		if (dbpediaConstructQueries == null) {
			File fileSelect = getResource(QUERIES_CONSTRUCT);
			dbpediaConstructQueries = readFile(fileSelect.getPath(), StandardCharsets.UTF_8.name());
		}
		return dbpediaConstructQueries;
	}

	public static List<String> getDbpediaDescribeQueries() {
		if (dbpediaDescribeQueries == null) {
			File fileSelect = getResource(QUERIES_DESCRIBE);
			dbpediaDescribeQueries = readFile(fileSelect.getPath(), StandardCharsets.UTF_8.name());
		}
		return dbpediaDescribeQueries;
	}

	public static List<String> getDbpediaSelectQueries() {
		if (dbpediaSelectQueries == null) {
			File fileSelect = getResource(QUERIES_SELECT);
			dbpediaSelectQueries = readFile(fileSelect.getPath(), StandardCharsets.UTF_8.name());
		}
		return dbpediaSelectQueries;
	}

	protected static File getResource(String resourceName) {
		ClassLoader classLoader = ImportFilesTest.class.getClassLoader();
		return new File(classLoader.getResource(resourceName).getFile());
	}

	protected static List<String> readFile(String filePath, String charsetName) {
		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {

			List<String> lines = new LinkedList<String>();
			StringBuilder sb = new StringBuilder();

			File file = new File(filePath);
			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.startsWith(SEPARATOR)) {

					// Add non-empty query
					if (sb.length() > 0) {
						lines.add(sb.toString());
						sb = new StringBuilder();
					}

				} else {

					// Add line
					sb.append(line);
					sb.append(System.lineSeparator());
				}
			}

			// Add last query
			if (sb.length() > 0) {
				lines.add(sb.toString());
			}

			return lines;

		} catch (IOException ioException) {
			throw new UnitTestRuntimeException(ioException);
		} finally {
			try {
				fileInputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			} catch (IOException closeException) {
				throw new UnitTestRuntimeException(closeException);
			}
		}
	}

	public void test() {
		int commentedOut = 6;
		assertTrue(getDbpediaAskQueries().size() == 100 - commentedOut);
		assertTrue(getDbpediaConstructQueries().size() == 100);
		assertTrue(getDbpediaDescribeQueries().size() == 25);
		assertTrue(getDbpediaSelectQueries().size() == 100);
	}
}