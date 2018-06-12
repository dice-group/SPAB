package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * 
 * TriplesBlock ::= TriplesSameSubjectPath ( '.' TriplesBlock? )?
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rTriplesBlock
 * 
 *      Simplified version of triple
 * 
 * @author Adrian Wilke
 */
public class SimpleTriple extends Expression {

	public SimpleTriple() {
		super();
	}

	public SimpleTriple(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new SimpleTriple(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		// TODO
		stringBuilder.append("TRIPLE");
	}
}