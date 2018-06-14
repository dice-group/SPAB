package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * @see https://www.w3.org/TR/sparql11-query/#rSolutionModifier
 * 
 * @author Adrian Wilke
 */
public class SolutionModifier extends Expression {
	
	public SolutionModifier(Expression parent) {
		super(parent);
	}
	
	@Override
	public List<Expression> getChildren(Input input) {
		List<Expression> children = new LinkedList<Expression>();
		children.add(new Group(this));
		return children;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {
		addWildcardIfRootClass(stringBuilder, SolutionModifier.class);
		addParentPrefix(stringBuilder, null, false);
	}

	@Override
	public void addString(StringBuilder stringBuilder) {
	}

	@Override
	public void addSuffix(StringBuilder stringBuilder) {
		addParentSuffix(stringBuilder, null, false);
	}

}