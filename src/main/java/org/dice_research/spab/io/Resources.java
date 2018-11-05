package org.dice_research.spab.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * Handles resources.
 * 
 * @author Adrian Wilke
 */
public class Resources {

	/**
	 * Gets resource as InputStream and reads it as UTF-8 string.
	 * 
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	public static String getResourceAsString(String resourceName) throws IOException {
		ClassLoader classLoader = Resources.class.getClassLoader();
		return IOUtils.toString(classLoader.getResourceAsStream(resourceName), "UTF-8");
	}

	/**
	 * @deprecated File access can not be used in Jar.
	 */
	public static File getResource(String resourceName) {
		ClassLoader classLoader = Resources.class.getClassLoader();
		return new File(classLoader.getResource(resourceName).getFile());
	}
}