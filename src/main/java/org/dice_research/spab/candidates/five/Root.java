package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * Most general expression.
 * 
 * @author Adrian Wilke
 */
public class Root extends Expression {

	public Root() {
		super();
	}

	public Root(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new Root(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
		// All children are more specific
		new Query().addChildren(children);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		// Most general expression.
		stringBuilder.append(".*");
	}
}