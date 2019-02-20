package org.dice_research.spab;

import org.dice_research.spab.tests.BenchmarkTest;
import org.dice_research.spab.tests.CandidateGeneralTest;
import org.dice_research.spab.tests.CandidateGenerationTest;
import org.dice_research.spab.tests.CandidateSixTest;
import org.dice_research.spab.tests.ImportFilesTest;
import org.dice_research.spab.tests.ImportQueriesTest;
import org.dice_research.spab.tests.IncorrectInputTest;
import org.dice_research.spab.tests.QueryReplacementsTest;
import org.dice_research.spab.tests.ScoringTest;
import org.dice_research.spab.tests.SimpleScenarioBasedTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

		BenchmarkTest.class,

		CandidateGeneralTest.class,

		CandidateGenerationTest.class,

		CandidateSixTest.class,

		IncorrectInputTest.class,

		ImportFilesTest.class,

		ImportQueriesTest.class,

		QueryReplacementsTest.class,

		ScoringTest.class,

		SimpleScenarioBasedTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class TestsSuite {
}