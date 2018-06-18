package org.dice_research.spab.candidates.six;

/**
 * SelectQuery ::= SelectClause DatasetClause* WhereClause SolutionModifier
 * 
 * Generated by {@link Query}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSelectQuery
 * 
 * @author Adrian Wilke
 */
public class SelectQuery extends Expression {

	public SelectQuery() {
		WhereClause whereClause = new WhereClause();
		sequence.add(whereClause);
	}

	public SelectQuery(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new SelectQuery(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}
}