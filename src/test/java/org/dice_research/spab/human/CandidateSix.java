package org.dice_research.spab.human;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.six.Expression;
import org.dice_research.spab.candidates.six.Root;
import org.dice_research.spab.input.Input;

public class CandidateSix {

	public static final String A = "SELECT ?s WHERE { ?s <A> ?o }";

	public static void main(String[] args) {
		humanCheck();
	}

	public static void humanCheck() {
		Input input = new Input();
		input.addPositive(A);

		Expression expression;

		expression = new Root();

		List<Expression> expressions = new LinkedList<Expression>();
		expressions.add(expression);

		refine(expressions, input, 5);

		for (Expression expression2 : expressions) {
			System.out.println(expression2.getRegex());
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
