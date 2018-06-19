package org.dice_research.spab;

import org.dice_research.spab.input.SparqlQuery;
import org.dice_research.spab.structures.CandidateVertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime statistics.
 * 
 * Use system environment variable {@link Statistics#SPAB_STATISTICS_INTERVAL}
 * to define logging interval in ms. To disable use default value 0.
 * 
 * @author Adrian Wilke
 */
public abstract class Statistics {

	private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

	private static final String SPAB_STATISTICS_INTERVAL = "SPAB_STATISTICS_INTERVAL";
	private static int infoInterval = 0;

	public static int calcScoreCalls = 0;
	public static double calcScoreRuntime = 0;
	public static int matchingCalls = 0;
	public static double matchingRuntime = 0;
	public static int regexCalls = 0;
	public static double regexRuntime = 0;
	public static int queryLineCalls = 0;
	public static double queryLineRuntime = 0;

	public static long timeBegin;
	public static long timeEnd;

	/**
	 * Calculate duration until next logging interval
	 */
	public static long timeInfo = 0;

	static {
		String interval = System.getenv(SPAB_STATISTICS_INTERVAL);
		if (interval != null) {
			LOGGER.info("Setting statistics info interval to " + interval);
			infoInterval = Integer.parseInt(interval);
		}
	}

	/**
	 * {@link CandidateVertex#calculateScore()}
	 */
	public static void addCalcScoreStats(long timeBegin, long timeEnd) {
		calcScoreRuntime += (timeEnd - timeBegin) / 1000d;
		calcScoreCalls++;
	}

	/**
	 * {@link CandidateVertex#calculateScore()}
	 */
	public static void addMatchingStats(long timeBegin, long timeEnd) {
		matchingRuntime += (timeEnd - timeBegin) / 1000d;
		matchingCalls++;
	}

	/**
	 * {@link SparqlQuery#getLineRepresentation()}
	 */
	public static void addQueryLineStats(long timeBegin, long timeEnd) {
		queryLineRuntime += (timeEnd - timeBegin) / 1000d;
		queryLineCalls++;
	}

	/**
	 * {@link RegEx#generate()}
	 */
	public static void addRegExStats(long timeBegin, long timeEnd) {
		regexRuntime += (timeEnd - timeBegin) / 1000d;
		regexCalls++;
	}

	public static void info() {
		if (System.currentTimeMillis() - timeInfo > infoInterval && infoInterval != 0) {
			timeInfo = System.currentTimeMillis();
			LOGGER.info("RegEx generation: " + regexCalls + " calls, " + regexRuntime + " sec");
			LOGGER.info("Query generation: " + queryLineCalls + " calls, " + queryLineRuntime + " sec");
			LOGGER.info("Matching: " + matchingCalls + " calls, " + matchingRuntime + " sec");
			LOGGER.info("Score calculations: " + calcScoreCalls + " calls, " + calcScoreRuntime + " sec");
		}
	}

	public static double getRuntime() {
		timeEnd = System.currentTimeMillis();
		return (timeEnd - timeBegin) / 1000d;
	}
}