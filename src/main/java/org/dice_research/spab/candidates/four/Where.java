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

	// TODO: Generate based on input queries
	protected final static int MAX_TRIPLES = 3;

	protected List<Triple> triples = new LinkedList<Triple>();

	public Where() {
		super();
		triples.add(new Triple());
	}

	public Where(Expression parent) {
		super(parent);
		if (parent instanceof Where) {
			Where parentWhere = (Where) parent;
			triples.addAll(parentWhere.triples);
			triples.add(new Triple());
		}
	}

	@Override
	public List<Expression> getChildren(Input input) {
		List<Expression> children = new LinkedList<Expression>();

		// Add additional triple
		if (triples.size() < MAX_TRIPLES) {
			children.add(new Where(this));
		}

		// Refine existing triples
		for (Triple triple : triples) {
			children.addAll(triple.getChildren(input));
		}
		
		return children;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {

		// Add prefix for non-where
		Expression parentExpression = parent;
		while (parentExpression instanceof Where) {
			parentExpression = parentExpression.parent;
		}
		if (parentExpression != null) {
			parentExpression.addPrefix(stringBuilder);
		}

		if (parentExpression == null) {
			stringBuilder.append(".*");
		}
		stringBuilder.append("WHERE \\{");
		for (int t = 0; t < triples.size(); t++) {
			if (t > 0) {
				stringBuilder.append(" \\. ");
			}
			triples.get(t).addPrefix(stringBuilder);
			triples.get(t).addSuffix(stringBuilder);
		}
	}

	@Override
	public void addSuffix(StringBuilder stringBuilder) {
		stringBuilder.append(" \\}");
		stringBuilder.append(".*");

		// Add suffix for non-where
		Expression parentExpression = parent;
		while (parentExpression instanceof Where) {
			parentExpression = parentExpression.parent;
		}
		if (parentExpression != null) {
			parentExpression.addSuffix(stringBuilder);
		}
	}

}
