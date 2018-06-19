package org.dice_research.spab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IncorrectInputTest.class, QueryReplacementsTest.class, CandidateGeneralTest.class,
		CandidateSpabTwoTest.class, ImportFilesTest.class, QueryReplacementsTest.class, RegExTest.class,
		ScoringTest.class, CandidateGenerationTest.class, SimpleScenarioBasedTest.class, CandidateSpabSixTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class AllTestsSuite {
}