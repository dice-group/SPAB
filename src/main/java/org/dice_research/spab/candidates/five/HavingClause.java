package org.dice_research.spab.candidates.five;

import java.util.List;

public class HavingClause extends Expression {

	public HavingClause() {
		super();
	}

	public HavingClause(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new HavingClause(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("HAVING");
		stringBuilder.append(".*");
	}
}