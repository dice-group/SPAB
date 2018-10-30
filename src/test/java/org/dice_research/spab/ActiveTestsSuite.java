package org.dice_research.spab;

import org.dice_research.spab.active.CandidateGeneralTest;
import org.dice_research.spab.active.CandidateGenerationTest;
import org.dice_research.spab.active.CandidateSixTest;
import org.dice_research.spab.active.ImportFilesTest;
import org.dice_research.spab.active.ImportQueriesTest;
import org.dice_research.spab.active.IncorrectInputTest;
import org.dice_research.spab.active.QueryReplacementsTest;
import org.dice_research.spab.active.ScoringTest;
import org.dice_research.spab.active.SimpleScenarioBasedTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({

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
public class ActiveTestsSuite {
}