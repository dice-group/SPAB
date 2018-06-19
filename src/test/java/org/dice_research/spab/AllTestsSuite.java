package org.dice_research.spab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IncorrectInputTest.class, QueryReplacementsTest.class, CandidateGeneralTest.class,
		ImportFilesTest.class, QueryReplacementsTest.class, ScoringTest.class, CandidateGenerationTest.class,
		SimpleScenarioBasedTest.class, CandidateSixTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class AllTestsSuite {
}