package org.dice_research.spab;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.three.SolutionModifierFeature;
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
		assertTrue(tripleFeature.getTripleType().equals(TripleFeature.TripleType.GENERIC));
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
		// 1x empty replaced with resource and 3x resource at triple positions s,p,o
		assertEquals(subWhereFeatures.size(), numberOfResources * 4);
	}

	@Test
	public void testModifierFeature() {
		// Get all keys of solution modifiers
		List<String> allSolutionModifiers = Arrays.asList(SolutionModifierFeature.getAllTypes());

		//
		// SolutionModifierFeature

		// Generate regex for all s.m.
		List<String> returnedRegexs = new LinkedList<String>();
		for (String solutionnModifier : allSolutionModifiers) {
			SolutionModifierFeature feature = new SolutionModifierFeature(solutionnModifier);
			returnedRegexs.add(subFeatureRegexToStringBuilder(feature).toString());
		}

		// Test, if asserted regex are generated by features
		String[] expectedSubRegexs = new String[] { ".*GROUP BY.*", ".*HAVING.*", ".*ORDER BY.*", ".*LIMIT.*",
				".*OFFSET.*" };
		for (String expectedSubRegex : expectedSubRegexs) {
			assertTrue(returnedRegexs.contains(expectedSubRegex));
		}

		//
		// Candidates

		// Generate cildren
		Input input = new Input();
		SpabThreeCandidate rootCandidate = new SpabThreeCandidate(null);
		List<Candidate> children = rootCandidate.getChildren(input);

		// Get regexs form children
		returnedRegexs = new LinkedList<String>();
		for (Candidate candidate : children) {
			returnedRegexs.add(candidate.getRegEx());
		}

		// Test, if asserted regex are generated by candidates
		for (String expectedSubRegex : expectedSubRegexs) {
			assertTrue(returnedRegexs.contains(expectedSubRegex));
		}

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
		int assumedNumberOfModifiers = 5;
		assertEquals(assumedNumberOfTypes, TypeFeature.getAllTypes().length);
		int assumedNumberOfWhere = 1;
		assertEquals(rootChildren.size(), assumedNumberOfTypes + assumedNumberOfWhere + assumedNumberOfModifiers);
		if (PRINT) {
			for (Candidate child : rootChildren) {
				System.out.println(child.getRegEx());
			}
			System.out.println(rootChildren.size() + "children of root");
			System.out.println();
		}

		// Human test
		if (PRINT) {
			String resource = "http://dbpedia.org/ontology/pubchem";
			Candidate candidatewithResource = null;

			// Print all children of root node
			for (Candidate rootChild : rootChildren) {
				System.out.println(">" + rootChild.getRegEx());
				List<Candidate> children = rootChild.getChildren(input);
				for (Candidate child : children) {
					System.out.println(child.getRegEx());
					if (child.getRegEx().contains(resource)) {
						candidatewithResource = child;
					}
				}
				System.out.println();
			}

			// Get candidate, which contains the resource and print its children
			List<Candidate> sparseCandidates = new LinkedList<Candidate>();
			if (candidatewithResource != null) {
				System.out.println(">>" + candidatewithResource.getRegEx());
				List<Candidate> children = candidatewithResource.getChildren(input);
				for (Candidate child : children) {
					System.out.println(child.getRegEx());
					if (child.getRegEx().contains(resource) && child.getRegEx().contains(" .*")) {
						sparseCandidates.add(child);
					}
				}
			}

			// Get candidates, which contains the resource in S or P or O and print their
			// children
			Candidate lastGeneratedChild = null;
			for (Candidate sparseCandidate : sparseCandidates) {
				System.out.println();
				System.out.println(">>>" + sparseCandidate.getRegEx());
				List<Candidate> children = sparseCandidate.getChildren(input);
				for (Candidate child : children) {
					System.out.println(child.getRegEx());
					lastGeneratedChild = child;
				}
			}

			// Print children of last generated child
			if (lastGeneratedChild != null) {
				System.out.println();
				System.out.println(">>>>" + lastGeneratedChild.getRegEx());
				List<Candidate> children = lastGeneratedChild.getChildren(input);
				for (Candidate child : children) {
					System.out.println(child.getRegEx());
				}
			}
		}

	}

	protected StringBuilder subFeatureRegexToStringBuilder(SubFeature subFeature) {
		StringBuilder stringBuilder = new StringBuilder();
		subFeature.appendRegex(stringBuilder);
		return stringBuilder;
	}

}
