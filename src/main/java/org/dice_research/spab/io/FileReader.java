package org.dice_research.spab.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.exceptions.IoRuntimeException;

public abstract class FileReader {

	final public static String UTF8 = StandardCharsets.UTF_8.name();

	/**
	 * Reads file to list of strings.
	 */
	public static List<String> readFileToList(String filePath, boolean ignoreEmptyLines, String charsetName) {
		File file = new File(filePath);
		if (!file.canRead()) {
			throw new IoRuntimeException("Can not read file: " + filePath);
		}

		FileInputStream fileInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		try {
			List<String> lines = new LinkedList<String>();

			fileInputStream = new FileInputStream(file);
			inputStreamReader = new InputStreamReader(fileInputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				if (!line.isEmpty() && ignoreEmptyLines) {
					lines.add(line);
				}
			}
			return lines;

		} catch (IOException ioException) {
			throw new IoRuntimeException(ioException);
		} finally {
			try {
				fileInputStream.close();
				inputStreamReader.close();
				bufferedReader.close();
			} catch (IOException closeException) {
				throw new IoRuntimeException(closeException);
			}
		}
	}
}