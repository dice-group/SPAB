package org.dice_research.spab.candidates.four;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * @see https://www.w3.org/TR/sparql11-query/#rGroupClause
 * @see https://www.w3.org/TR/sparql11-query/#rGroupCondition
 * 
 * @author Adrian Wilke
 */
public class Group extends Expression {

	public Group(Expression parent) {
		super(parent);
	}

	@Override
	public List<Expression> getChildren(Input input) {
		return null;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {
		addParentPrefix(stringBuilder, null, false);
		stringBuilder.append(" GROUP BY ");
		stringBuilder.append(".*");
	}

	@Override
	public void addString(StringBuilder stringBuilder) {
	}

	@Override
	public void addSuffix(StringBuilder stringBuilder) {
		addParentSuffix(stringBuilder, null, false);
	}

}