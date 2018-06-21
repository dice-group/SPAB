package org.dice_research.spab.candidates.six;

/**
 * SelectClause ::= 'SELECT' ( 'DISTINCT' | 'REDUCED' )? ( ( Var | ( '('
 * Expression 'AS' Var ')' ) )+ | '*' )
 * 
 * Generated by {@link SelectQuery}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSelectClause
 * 
 * @author Adrian Wilke
 */
public class SelectClause extends Expression {

	public SelectClause() {
		super();
	}

	public SelectClause(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new SelectClause(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("SELECT ");
		addWildcard(stringBuilder);
	}
}