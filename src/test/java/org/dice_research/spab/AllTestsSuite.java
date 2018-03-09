package org.dice_research.spab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IncorrectInputTest.class, MissingPrefixTest.class, QueryFormatTest.class, UpdateRequestTest.class,DummyTest.class,
		SpabOneCandidateTest.class, ImportExamplesTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class AllTestsSuite {
}