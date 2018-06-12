package org.dice_research.spab.candidates.five;

import java.util.List;

public class SelectClause extends Expression {

	public SelectClause() {
		super();
	}

	public SelectClause(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new SelectClause(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("SELECT");
		addWildcard(stringBuilder);
	}

}