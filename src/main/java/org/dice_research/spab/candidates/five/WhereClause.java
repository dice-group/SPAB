package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * WhereClause ::= 'WHERE'? GroupGraphPattern
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rWhereClause
 * 
 *      Only one instance, created by every constructor.
 * 
 *      Implemented only with 'WHERE', as this is default Jena representation.
 * 
 * @author Adrian Wilke
 */
public class WhereClause extends Expression {

	public WhereClause() {
		super();
		create();
	}

	public WhereClause(WhereClause parent) {
		super(parent);
		create();
	}

	public WhereClause(Expression parent) {
		super(parent);
		create();
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new WhereClause(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
		refineSequence(children);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("WHERE ");
		addSequenceRegex(stringBuilder);
	}

	protected void create() {
		if (sequence.isEmpty()) {
			sequence.add(new GroupGraphPattern());
		}
	}
}