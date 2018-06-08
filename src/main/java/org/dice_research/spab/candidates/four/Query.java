package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * @see https://www.w3.org/TR/sparql11-query/#rQuery
 * 
 * @author Adrian Wilke
 */
public class Query extends Expression {

	public static enum Type {
		SELECT, CONSTRUCT, DESCRIBE, ASK
	}

	protected Type type;

	public Query(Type type) {
		this.type = type;
	}

	public Query(Expression parent, Type type) {
		super(parent);
		this.type = type;
	}

	@Override
	public List<Expression> getChildren(Input input) {
		// TODO Generate children
		return  new LinkedList<Expression>();
	}

	@Override
	public void addLeftHandSide(StringBuilder stringBuilder) {
		addParentLeftHandSide(stringBuilder);
		stringBuilder.append(type.toString());
		stringBuilder.append(".*");
	}

	@Override
	public void addRightHandSide(StringBuilder stringBuilder) {
		addParentRightHandSide(stringBuilder);
	}

}