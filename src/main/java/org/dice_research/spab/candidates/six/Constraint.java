package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

/**
 * Constraint ::= BrackettedExpression | BuiltInCall | FunctionCall
 * 
 * Generated by {@link Filter}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rConstraint
 * 
 * @author Adrian Wilke
 */
public class Constraint extends Expression {

	/**
	 * Creates constraints containing various built-in-calls..
	 */
	public static List<Expression> getInitialInstances() {
		List<Expression> instances = new LinkedList<Expression>();
		for (Expression builtInCall : BuiltInCall.getInitialInstances()) {
			Constraint constraint = new Constraint();
			constraint.sequence.add(builtInCall);
			instances.add(constraint);
		}
		return instances;
	}

	public Constraint() {
		super();
	}

	public Constraint(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new Constraint(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}
}