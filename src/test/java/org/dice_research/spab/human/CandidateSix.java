package org.dice_research.spab.human;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.candidates.six.Root;
import org.dice_research.spab.input.Input;

public class CandidateSix {

	public static final String A = "SELECT ?s WHERE { ?s <A> ?o }";

	public static void main(String[] args) {
		// humanCheck();
		humanClassesCheck();
	}

	/**
	 * Prints regular expressions and related class structure.
	 */
	public static void humanClassesCheck() {
		Input input = new Input();
		input.addPositive(A);

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

	public static void humanCheck() {
		Input input = new Input();
		input.addPositive(A);

		// Add root node and refine
		List<Expression> expressions = new LinkedList<Expression>();
		expressions.add(new Root());
		refine(expressions, input, 5);

		// Print all generated expressions
		for (Expression expression : expressions) {
			System.out.println(expression.getRegex());
		}
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
