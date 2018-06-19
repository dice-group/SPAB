package org.dice_research.spab.candidates.six;

/**
 * GroupClause ::= 'GROUP' 'BY' GroupCondition+
 * 
 * Generated by {@link SolutionModifier}
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rGroupClause
 * 
 * @author Adrian Wilke
 */
public class GroupClause extends Expression {

	public GroupClause() {
		super();
	}

	public GroupClause(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new GroupClause(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addWildcard(stringBuilder);
		stringBuilder.append("GROUP BY");
		addWildcard(stringBuilder);
	}
}