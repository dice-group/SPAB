package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.candidates.six.GroupGraphPatternSub;
import org.dice_research.spab.candidates.six.Triple;
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

	@Test
	public void test() {
		Input input = new Input();
		input.addPositive(SELECT);
		// GroupGraphPatternSub expression = new GroupGraphPatternSub();
		// recursivePrint(expression, input);
		Triple triple = new Triple();
		recursivePrint(triple, input);
	}

	public void recursivePrint(Expression expression, Input input) {
		System.out.println(expression.getRegex());

		List<Expression> refinements = expression.getRefinements(input);
		for (Expression refinement : refinements) {
			System.out.println(" " + refinement.getRegex());
		}
		System.out.println();

		for (Expression refinement : refinements) {
			recursivePrint(refinement, input);
		}
	}
}