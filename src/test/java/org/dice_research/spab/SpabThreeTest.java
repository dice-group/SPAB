package org.dice_research.spab;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.three.SpabThreeCandidate;
import org.dice_research.spab.candidates.three.SubFeature;
import org.dice_research.spab.candidates.three.TripleFeature;
import org.dice_research.spab.candidates.three.TypeFeature;
import org.dice_research.spab.candidates.three.WhereFeature;
import org.dice_research.spab.input.Input;
import org.junit.Test;

/**
 * Tests candidate implementation.
 * 
 * @author Adrian Wilke
 */
public class SpabThreeTest extends AbstractTestCase {

	public static final String SELECT = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o }";

	@Test
	public void testTypeFeature() {

		Set<String> types = new HashSet<>();
		for (String type : TypeFeature.getAllTypes()) {
			TypeFeature typeFeature = new TypeFeature(type);
			types.add(subFeatureRegexToStringBuilder(typeFeature).toString());
		}

		int numberOfTypes = 14;
		assertEquals(types.size(), numberOfTypes);
	}

	@Test
	public void testTripleFeature() {

		// Empty triple can contain everything
		String emptyTripleRegEx = ".*";
		TripleFeature tripleFeature = new TripleFeature();
		StringBuilder stringBuilder = new StringBuilder();
		tripleFeature.appendRegex(stringBuilder);
		assertEquals(stringBuilder.toString(), emptyTripleRegEx);
		assertTrue(tripleFeature.getTripleType().equals(TripleFeature.TripleType.EMPTY));

		// Resource triple should contain resource
		String resource = "http://dbpedia.org/ontology/pubchem";
		String resourceTripleRegEx = ".*" + "\\Q" + resource + "\\E" + ".*";
		tripleFeature = new TripleFeature(resource);
		stringBuilder = new StringBuilder();
		tripleFeature.appendRegex(stringBuilder);
		assertEquals(stringBuilder.toString(), resourceTripleRegEx);
		assertTrue(tripleFeature.getTripleType().equals(TripleFeature.TripleType.RESOURCE));
	}

	@Test
	public void testWhereFeature() {

		Input input = new Input();
		int numberOfResources = 1;
		input.addPositive(SELECT);
		assertEquals(input.getResources().size(), numberOfResources);

		//
		// Root (empty WHERE)

		// Where has to contain term itself and brackets
		String emptyWhereRegEx = ".*WHERE.*\\{.*\\}.*";
		WhereFeature emptyWhereFeature = new WhereFeature(true);
		StringBuilder regexBuilder = subFeatureRegexToStringBuilder(emptyWhereFeature);
		if (PRINT) {
			System.out.println(regexBuilder);
		}
		assertEquals(regexBuilder.toString(), emptyWhereRegEx);

		//
		// Root children

		String resource = "http://dbpedia.org/ontology/pubchem";
		String resourceWhereRegEx = ".*WHERE.*\\{" + ".*" + "\\Q" + resource + "\\E" + ".*" + "\\}.*";
		String twoTriplesWhereRegEx = ".*WHERE.*\\{.*" + "\\ \\.\\ .*" + "\\}.*";
		List<WhereFeature> subWhereFeatures = emptyWhereFeature.generateSubFeatures(input);
		// (a) resource (b) two empty triples
		assertEquals(subWhereFeatures.size(), numberOfResources + 1);

		// Empty triple is replaced with resource
		WhereFeature resourceWhereFeature = subWhereFeatures.get(0);
		regexBuilder = subFeatureRegexToStringBuilder(resourceWhereFeature);
		if (PRINT) {
			System.out.println(regexBuilder.toString());
			System.out.println(resourceWhereRegEx);
		}
		assertEquals(regexBuilder.toString(), resourceWhereRegEx);

		// Additional empty triple
		WhereFeature twoTriplesWhereFeature = subWhereFeatures.get(subWhereFeatures.size() - 1);
		regexBuilder = subFeatureRegexToStringBuilder(twoTriplesWhereFeature);
		assertEquals(regexBuilder.toString(), twoTriplesWhereRegEx);

		//
		// Start with two empty triples

		subWhereFeatures = twoTriplesWhereFeature.generateSubFeatures(input);
		// (a) 2x resource (b) three empty triples
		assertEquals(subWhereFeatures.size(), numberOfResources + 2);

		// Three empty triples
		String threeTriplesWhereRegEx = ".*WHERE.*\\{.*" + "\\ \\.\\ .*" + "\\ \\.\\ .*" + "\\}.*";
		WhereFeature threeTriplesWhereFeature = subWhereFeatures.get(subWhereFeatures.size() - 1);
		regexBuilder = subFeatureRegexToStringBuilder(threeTriplesWhereFeature);
		if (PRINT) {
			System.out.println(regexBuilder.toString());
			System.out.println(threeTriplesWhereRegEx);
		}
		assertEquals(regexBuilder.toString(), threeTriplesWhereRegEx);

		// Human check
		if (PRINT) {
			System.out.println(">>>" + subFeatureRegexToStringBuilder(twoTriplesWhereFeature));
			for (WhereFeature whereFeature : subWhereFeatures) {
				System.out.println(subFeatureRegexToStringBuilder(whereFeature));
			}
		}

		// Start: Resource and empty triple
		WhereFeature resEmptyWhereFeature = subWhereFeatures.get(0);
		regexBuilder = subFeatureRegexToStringBuilder(resEmptyWhereFeature);
		subWhereFeatures = resEmptyWhereFeature.generateSubFeatures(input);

		if (PRINT) {
			System.out.println(">>>" + subFeatureRegexToStringBuilder(resEmptyWhereFeature));
			for (WhereFeature whereFeature : subWhereFeatures) {
				System.out.println(subFeatureRegexToStringBuilder(whereFeature));
			}
		}
		// Current implementation: Only one additional feature: empty replaced with res
		assertEquals(subWhereFeatures.size(), numberOfResources);
	}

	@Test
	public void testCandidateGeneration() {

		// Create test input
		Input input = new Input();
		input.addPositive(SELECT);

		// Root candidate should match every query
		SpabThreeCandidate rootCandidate = new SpabThreeCandidate(null);
		assertEquals(rootCandidate.getRegEx(), ".*");

		// First generation of children
		List<Candidate> rootChildren = rootCandidate.getChildren(input);
		int assumedNumberOfTypes = 14;
		assertEquals(assumedNumberOfTypes, TypeFeature.getAllTypes().length);
		int assumedNumberOfWhere = 1;
		assertEquals(rootChildren.size(), assumedNumberOfTypes + assumedNumberOfWhere);
		if (PRINT) {
			for (Candidate child : rootChildren) {
				System.out.println(child.getRegEx());
			}
			System.out.println(rootChildren.size() + "children of root");
			System.out.println();
		}

		// Human test
		if (PRINT) {
			for (Candidate rootChild : rootChildren) {
				System.out.println(rootChild.getRegEx());
				List<Candidate> children = rootChild.getChildren(input);
				for (Candidate child : children) {
					System.out.println(child.getRegEx());
				}
				System.out.println();
			}
		}

	}

	protected StringBuilder subFeatureRegexToStringBuilder(SubFeature subFeature) {
		StringBuilder stringBuilder = new StringBuilder();
		subFeature.appendRegex(stringBuilder);
		return stringBuilder;
	}

}
