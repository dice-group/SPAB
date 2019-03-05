package org.dice_research.spab.candidates.six;

/**
 * OrderClause ::= 'ORDER' 'BY' OrderCondition+
 * 
 * Generated by {@link SolutionModifier}
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rOrderClause
 * 
 * @author Adrian Wilke
 */
public class OrderClause extends Expression {

	public OrderClause() {
		super();
	}

	public OrderClause(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new OrderClause(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addWildcard(stringBuilder);
		stringBuilder.append("ORDER BY");
		addWildcard(stringBuilder);
	}
}