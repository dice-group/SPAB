package org.dice_research.spab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Tests import of example queries
 * 
 * @author Adrian Wilke
 */
public class ImportExamplesTest extends TestCase {

	protected final static String QUERIES_SELECT = "dbpedia-select-100.txt";
	protected final static String SEPARATOR = "#---";

	public List<String> getDbpediaSelectQueries() throws IOException {
		File fileSelect = getResource(QUERIES_SELECT);
		return readFile(fileSelect.getPath(), StandardCharsets.UTF_8.name());
	}

	protected File getResource(String resourceName) {
		ClassLoader classLoader = this.getClass().getClassLoader();
		return new File(classLoader.getResource(resourceName).getFile());
	}

	protected List<String> readFile(String filePath, String charsetName) throws IOException {
		List<String> lines = new LinkedList<String>();
		StringBuilder sb = new StringBuilder();

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;

		try {
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

		} finally {
			fileInputStream.close();
			inputStreamReader.close();
			bufferedReader.close();
		}
		return lines;
	}

	public void test() throws IOException {
		assertTrue(getDbpediaSelectQueries().size() == 100);
	}
}