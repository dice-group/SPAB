package org.dice_research.spab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IncorrectInputTest.class, QueryHandlingTest.class, CandidateGeneralTest.class,
		CandidateSpabOneTest.class, ImportFilesTest.class, QueryHandlingTest.class, RegExTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class AllTestsSuite {
}