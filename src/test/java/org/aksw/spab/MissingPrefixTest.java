package org.aksw.spab;

import org.aksw.spab.exceptions.InputRuntimeException;
import org.junit.Test;

import junit.framework.TestCase;
/**
 * Tests for missing prefixes in SPARQL queries.
 * 
 * @author Adrian Wilke
 */
public class MissingPrefixTest extends TestCase {

	public static String query = "SELECT ?x ?name\n" + "WHERE  { ?x foaf:name ?name }";

	@Test
	public void testMissingPrefixInput() {
		try {
			new SpabApi().addPositive(query);
			fail("Missing namespace should throw Exception.");
		} catch (InputRuntimeException e) {
		}
	}

	@Test
	public void testGivenPrefixInput() {
		try {
			SpabApi spab = new SpabApi();
			spab.addNamespacePrefix("foaf", "<http://xmlns.com/foaf/0.1/>");
			spab.addPositive(query);
		} catch (InputRuntimeException e) {
			fail("Missing namespace should not throw Exception.");
		}
	}
}