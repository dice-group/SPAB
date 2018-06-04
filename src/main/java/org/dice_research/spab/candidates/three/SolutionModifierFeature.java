package org.dice_research.spab.candidates.three;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SPARQL parts: GroupClause, HavingClause, OrderClause, LimitOffsetClauses.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSolutionModifier
 * 
 * @author Adrian Wilke
 */
public class SolutionModifierFeature extends SubFeature {

	private static final Logger LOGGER = LoggerFactory.getLogger(SolutionModifierFeature.class);

	enum SolutionModifiers {
		GROUP, HAVING, ORDER, LIMIT, OFFSET
	}

	protected String solutionModifier;

	public SolutionModifierFeature(String solutionModifier) {
		this.solutionModifier = solutionModifier;
	}

	public static String[] getAllTypes() {
		return Arrays.stream(SolutionModifiers.class.getEnumConstants()).map(Enum::name).toArray(String[]::new);
	}

	@Override
	public void appendRegex(StringBuilder stringBuilder) {

		stringBuilder.append(".*");

		if (solutionModifier.equals(SolutionModifiers.GROUP.toString())) {
			stringBuilder.append("GROUP BY");

		} else if (solutionModifier.equals(SolutionModifiers.HAVING.toString())) {
			stringBuilder.append("HAVING");

		} else if (solutionModifier.equals(SolutionModifiers.ORDER.toString())) {
			stringBuilder.append("ORDER BY");

		} else if (solutionModifier.equals(SolutionModifiers.LIMIT.toString())) {
			stringBuilder.append("LIMIT");

		} else if (solutionModifier.equals(SolutionModifiers.OFFSET.toString())) {
			stringBuilder.append("OFFSET");

		} else {
			LOGGER.error("Unknown solution modifier " + solutionModifier);

		}

		stringBuilder.append(".*");
	}
}