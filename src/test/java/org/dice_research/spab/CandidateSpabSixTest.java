package org.dice_research.spab;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.candidates.six.Root;
import org.dice_research.spab.candidates.six.Triple;
import org.dice_research.spab.candidates.six.TriplesBlock;
import org.dice_research.spab.input.Input;
import org.junit.Test;

/**
 * Tests for candidate generation.
 * 
 * @author Adrian Wilke
 */
public class CandidateSpabSixTest extends AbstractTestCase {

	public static final String SELECT = "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> "
			+ "SELECT ?s ?o WHERE { ?s dbpedia-owl:pubchem ?o }";

	public static final String A = "SELECT ?s WHERE { ?s <A> ?o }";
	public static final String B = "SELECT ?s WHERE { ?s <B> ?o }";

	@Test
	public void test() {
//		Input input = new Input();
//		input.addPositive(A);
//		input.addPositive(B);
//
//		Expression expression;
//
//		expression = new Root();
//
//		List<Expression> expressions = new LinkedList<Expression>();
//		recursiveGeneration(expression, input, expressions);
//		System.out.println(expressions.size());
//
//		for (Expression exp : expressions) {
//			System.out.println(exp.getRegex() + "  (" + exp.getClass().getSimpleName() + ")");
//		}

	}

	@Test
	public void testTriplesBlockCreation() {
		Triple.generateFullTriples = false;

		Input input = new Input();
		input.addPositive(A);

		List<Expression> expressions = new LinkedList<Expression>();

		TriplesBlock triplesBlock = new TriplesBlock();
		triplesBlock.createTripleBlock=true;
		expressions.add(triplesBlock);
		for (Expression expression : expressions) {
			System.out.println(expression.getRegex());
		}

		
		expressions = refine(expressions, input);
		System.out.println(expressions.size());
		for (Expression expression : expressions) {
			System.out.println(expression.getRegex());
		}
		
		expressions = refine(expressions, input);
		System.out.println(expressions.size());
		for (Expression expression : expressions) {
			System.out.println(expression.getRegex());
		}

		
		Triple.generateFullTriples = true;
	}

	@Test
	public void testTripleCreation() {
		// Generated triples for one resource
		// 1x resource, 3x single, 3x double, 1x all
		// (generic triple not included in refinements)

		Input input = new Input();
		input.addPositive(A);
		Triple triple = new Triple();
		List<Expression> expressions = new LinkedList<Expression>();
		recursiveGeneration(triple, input, expressions);
		assertEquals(1 + 3 + 3 + 1, expressions.size());

		// Two resources
		// 2x resource
		// 6x = (2x3)x single
		// 12x double
		// 8x = (2x2x2)x triple
		// Additionally: Check for duplicates

		input = new Input();
		input.addPositive(A);
		input.addPositive(B);
		triple = new Triple();
		expressions = new LinkedList<Expression>();
		recursiveGeneration(triple, input, expressions);
		assertEquals(2 + 6 + 12 + 8, expressions.size());

		assertEquals(expressions.size(), new HashSet<Expression>(expressions).size());
	}

	public List<Expression> refine(List<Expression> expressions, Input input) {
		List<Expression> refinements = new LinkedList<Expression>();
		for (Expression expression : expressions) {
			refinements.addAll(expression.getRefinements(input));
		}
		return refinements;
	}

	public void recursiveGeneration(Expression expression, Input input, List<Expression> expressions) {
		List<Expression> refinements = expression.getRefinements(input);
		expressions.addAll(refinements);
		for (Expression refinement : refinements) {
			recursiveGeneration(refinement, input, expressions);
		}
	}

	public void recursivePrint(Expression expression, Input input) {
		System.out.println(expression.getRegex());

		List<Expression> refinements = expression.getRefinements(input);
		for (Expression refinement : refinements) {
			System.out.println(" " + refinement.getRegex());
		}
		System.out.println();
		System.out.println();

		for (Expression refinement : refinements) {
			recursivePrint(refinement, input);
		}
	}
}