package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * @see https://www.w3.org/TR/sparql11-query/#rWhereClause
 * 
 * @author Adrian Wilke
 */
public class Where extends Expression {

	@Override
	public List<Expression> getChildren(Input input) {
		List<Expression> children = new LinkedList<Expression>();
		children.add(new Triple(this));
		return children;
	}

	@Override
	public void addLeftHandSide(StringBuilder stringBuilder) {
		addParentLeftHandSide(stringBuilder);
		if (parent == null) {
			stringBuilder.append(".*");
		}
		stringBuilder.append("WHERE");
		stringBuilder.append("\\{");
	}

	@Override
	public void addRightHandSide(StringBuilder stringBuilder) {
		stringBuilder.append("\\}");
		stringBuilder.append(".*");
		addParentRightHandSide(stringBuilder);
	}

}
