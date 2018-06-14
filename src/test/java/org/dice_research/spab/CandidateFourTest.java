package org.dice_research.spab;

import java.util.List;

import org.dice_research.spab.candidates.four.Expression;
import org.dice_research.spab.candidates.four.SolutionModifier;
import org.dice_research.spab.candidates.four.SpabFourCandidate;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.input.Input;
import org.junit.Test;

/**
 * Tests for {@link SpabFourCandidate}.
 * 
 * @author Adrian Wilke
 */
public class CandidateFourTest extends AbstractTestCase {


	@Test
	public void testSolutionModifier() throws SpabException {
		SolutionModifier sm = new SolutionModifier(null);
		List<Expression> children = sm.getChildren(new Input());
		for (Expression child : children) {
			StringBuilder stringBuilder = new StringBuilder();
			child.addPrefix(stringBuilder);
			child.addSuffix(stringBuilder);
			System.out.println(stringBuilder.toString());
		}
	}
}