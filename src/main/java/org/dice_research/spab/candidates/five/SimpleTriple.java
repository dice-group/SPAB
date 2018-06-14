package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * Triple refining itself based on input resources.
 * 
 * Generated by TriplesBlock.
 * 
 * @author Adrian Wilke
 */
public class SimpleTriple extends Expression {

	protected boolean appendDivider = false;

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
		if (appendDivider) {
			stringBuilder.append(" \\. ");
		}
		stringBuilder.append(".*");
	}
}