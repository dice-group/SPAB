package org.dice_research.spab.candidates.five;

import java.util.List;

public class GroupClause extends Expression {

	public GroupClause() {
		super();
	}

	public GroupClause(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new GroupClause(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("GROUP BY");
		stringBuilder.append(".*");
	}
}