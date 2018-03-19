package org.dice_research.spab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runtime statistics.
 * 
 * @author Adrian Wilke
 */
public abstract class Statistics {

	private static final int infoInterval = 5000;
	private static final Logger LOGGER = LoggerFactory.getLogger(Statistics.class);

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
	public static long timeInfo = 0;

	public static void addCalcScoreStats(long timeBegin, long timeEnd) {
		calcScoreRuntime += (timeEnd - timeBegin) / 1000d;
		calcScoreCalls++;
	}

	public static void addMatchingStats(long timeBegin, long timeEnd) {
		matchingRuntime += (timeEnd - timeBegin) / 1000d;
		matchingCalls++;
	}

	public static void addQueryLineStats(long timeBegin, long timeEnd) {
		queryLineRuntime += (timeEnd - timeBegin) / 1000d;
		queryLineCalls++;
	}

	public static void addRegExStats(long timeBegin, long timeEnd) {
		regexRuntime += (timeEnd - timeBegin) / 1000d;
		regexCalls++;
	}

	public static void info() {
		if (System.currentTimeMillis() - timeInfo > infoInterval) {
			timeInfo = System.currentTimeMillis();
			LOGGER.info("RegEx generation: " + regexCalls + " calls, " + regexRuntime + " sec");
			LOGGER.info("Query generation: " + queryLineCalls + " calls, " + queryLineRuntime + " sec");
			LOGGER.info("Matching: " + matchingCalls + " calls, " + matchingRuntime + " sec");
			LOGGER.info("Score calculations: " + calcScoreCalls + " calls, " + calcScoreRuntime + " sec");
		}

	}
}