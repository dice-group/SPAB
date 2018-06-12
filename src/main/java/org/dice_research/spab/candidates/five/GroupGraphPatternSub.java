package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * GroupGraphPatternSub ::= TriplesBlock? ( GraphPatternNotTriples '.'?
 * TriplesBlock? )*
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rGroupGraphPattern
 * 
 * @author Adrian Wilke
 */
public class GroupGraphPatternSub extends Expression {

	public GroupGraphPatternSub() {
		super();
	}

	public GroupGraphPatternSub(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new GroupGraphPatternSub(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		// TODO
		stringBuilder.append("TEST");
	}
}