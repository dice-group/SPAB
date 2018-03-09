package org.dice_research.spab;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ IncorrectInputTest.class, MissingPrefixTest.class, QueryFormatTest.class, DummyTest.class,
		SpabOneCandidateTest.class })

/**
 * Suite for all tests.
 * 
 * @author Adrian Wilke
 */
public class AllTestsSuite {
}