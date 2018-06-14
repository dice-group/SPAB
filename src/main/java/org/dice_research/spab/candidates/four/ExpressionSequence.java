package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * Sequence of expressions.
 * 
 * @author Adrian Wilke
 */
public class ExpressionSequence extends Expression {

	List<Expression> expressionSequence = new LinkedList<Expression>();

	protected boolean addWildcardAtPrefix = false;
	protected boolean addWildcardAtSuffix = false;
	protected Class<?> classToOmitAtPrefix = null;
	protected Class<?> classToOmitAtSuffix = null;

	@Override
	public List<Expression> getChildren(Input input) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {
		addParentPrefix(stringBuilder, classToOmitAtPrefix, addWildcardAtPrefix);
	}

	@Override
	public void addString(StringBuilder stringBuilder) {
		for (Expression expression : expressionSequence) {
			expression.addString(stringBuilder);
		}
	}

	@Override
	public void addSuffix(StringBuilder stringBuilder) {
		addParentSuffix(stringBuilder, classToOmitAtSuffix, addWildcardAtSuffix);
	}

}