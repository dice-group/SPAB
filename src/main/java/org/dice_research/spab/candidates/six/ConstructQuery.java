package org.dice_research.spab.candidates.six;

/**
 * ConstructQuery ::= 'CONSTRUCT' ( ConstructTemplate DatasetClause* WhereClause
 * SolutionModifier | DatasetClause* 'WHERE' '{' TriplesTemplate? '}'
 * SolutionModifier )
 * 
 * Generated by {@link Query}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rConstructQuery
 * 
 * @author Adrian Wilke
 */
public class ConstructQuery extends Expression {

	public ConstructQuery() {
		super();
		sequence.add(new WhereClause());
		sequence.add(new SolutionModifier());
	}

	public ConstructQuery(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new ConstructQuery(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("CONSTRUCT ");
		addWildcard(stringBuilder);
		addSequenceToRegex(stringBuilder);
	}
}