package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * Query ::= Prologue ( SelectQuery | ConstructQuery | DescribeQuery | AskQuery
 * ) ValuesClause
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rQuery
 * 
 * Prologue is not implemented, as PREFIXes are removed.
 * 
 * @author Adrian Wilke
 */
public class Query extends Expression {

	public Query() {
		super();
	}

	public Query(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new Query(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
		new SelectQuery().addChildren(children);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
	}
}