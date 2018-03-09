package org.dice_research.spab;

import org.apache.jena.vocabulary.DC;
import org.dice_research.spab.exceptions.InputRuntimeException;
import org.junit.Test;

/**
 * Tests for SPARQL update requests
 * 
 * @author Adrian Wilke
 */
public class UpdateRequestTest extends AbstractTestCase {

	public static String insert = "INSERT DATA { <http://example/book1> " + DC.title
			+ " \"A new book\" ; dc:creator \"A.N.Other\" .}";
	public static String insertWithPrefix = "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
			+ "INSERT DATA { <http://example/book1> dc:title \"A new book\" ; dc:creator \"A.N.Other\" .}";

	@Test
	public void testInsertWithoutPrefixRequest() {
		try {
			// In current implementation, namespaces are required in Jena update requests
			new SpabApi().addPositive(insert);
			fail("Expected InputRuntimeException");
		} catch (InputRuntimeException e) {
		}
	}

	@Test
	public void testInsertWithPrefixRequest() {
		SpabApi spab = new SpabApi();
		spab.addPositive(insertWithPrefix);

		if (PRINT) {
			System.out.println(spab.getInput().getPositives().get(0).getOriginalString());
			System.out.println();
			System.out.println(spab.getInput().getPositives().get(0).getLineRepresentation());
			System.out.println();
			System.out.println(spab.getInput().getPositives().get(0).getStringRepresentation());
		}
	}
}