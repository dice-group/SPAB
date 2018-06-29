package org.dice_research.spab.human;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.candidates.six.GroupOrUnionGraphPattern;
import org.dice_research.spab.candidates.six.Root;
import org.dice_research.spab.input.Input;

public class CandidateSix {

	public static final String RES = "SELECT ?s WHERE { ?s <RES> ?o }";

	public static void main(String[] args) {

		humanCheck();
		// humanClassesCheck();
	}

	public static void humanCheck() {
		List<Expression> expressions = new LinkedList<Expression>();

		int refinements = 4;
		expressions.add(new GroupOrUnionGraphPattern());
		// expressions.addAll(.getInitialInstances());

		Input input = new Input();
		input.addPositive(RES);
		refine(expressions, input, refinements);

		// Print all generated expressions
		for (Expression expression : expressions) {
			System.out.println(expression.getRegex());
		}
	}

	/**
	 * Prints regular expressions and related class structure.
	 */
	public static void humanClassesCheck() {
		Input input = new Input();
		input.addPositive(RES);

		// Add root node and refine
		List<Expression> expressions = new LinkedList<Expression>();
		expressions.add(new Root());
		refine(expressions, input, 5);

		// Print all generated expressions
		StringBuilder stringBuilder = new StringBuilder();
		for (Expression expression : expressions) {
			expression.getClasses(stringBuilder);
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(expression.getRegex());
			stringBuilder.append(System.lineSeparator());
			stringBuilder.append(System.lineSeparator());
		}
		System.out.println(stringBuilder.toString());

	}

	public static List<Expression> refine(List<Expression> expressions, Input input, int steps) {
		for (int i = 0; i < steps; i++) {
			expressions.addAll(refine(expressions, input));
		}
		return expressions;
	}

	public static List<Expression> refine(List<Expression> expressions, Input input) {
		List<Expression> refinements = new LinkedList<Expression>();
		for (Expression expression : expressions) {
			refinements.addAll(expression.getRefinements(input));
		}
		return refinements;
	}
}
