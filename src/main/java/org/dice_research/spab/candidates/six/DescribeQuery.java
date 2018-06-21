package org.dice_research.spab.candidates.six;

/**
 * DescribeQuery ::= 'DESCRIBE' ( VarOrIri+ | '*' ) DatasetClause* WhereClause?
 * SolutionModifier
 * 
 * Generated by {@link Query}.
 * 
 * In this implementation, the WHERE clause is mandatory. In the grammar, it can
 * be omitted.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rDescribeQuery
 * 
 * @author Adrian Wilke
 */
public class DescribeQuery extends Expression {

	public DescribeQuery() {
		super();
		sequence.add(new WhereClause());
		sequence.add(new SolutionModifier());
	}

	public DescribeQuery(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new DescribeQuery(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("DESCRIBE ");
		addWildcard(stringBuilder);
		addSequenceToRegex(stringBuilder);
	}
}