package org.dice_research.spab;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * SPAB version.
 * 
 * Should be synchronized with version-tag of pom.xml.
 * 
 * @author Adrian Wilke
 */
public abstract class Version {

	public static final String VERSION = "0.1.0-SNAPSHOT";

	public static String getVersionAndCurrentDate() {
		return VERSION + "-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	}
}