package org.dice_research.spab.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public abstract class FileWriter {
	
	public static void writeStringToFile(String string, String filePath, boolean createDirectories) {

		if (createDirectories) {
			new File(filePath).getParentFile().mkdirs();
		}

		try {
			PrintWriter out = new PrintWriter(filePath);
			out.println(string);
			out.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}