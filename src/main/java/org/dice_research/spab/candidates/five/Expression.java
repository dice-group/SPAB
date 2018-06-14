package org.dice_research.spab.candidates.five;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;

/**
 * A feature can be represented as regular expression. It represents a SPARQL
 * part.
 * 
 * @author Adrian Wilke
 */
public abstract class Expression {

	/**
	 * Sequence of sub-expressions.
	 */
	protected List<Expression> sequence;

	/**
	 * Creates new expression with empty sequence.
	 */
	public Expression() {
		this.sequence = new LinkedList<Expression>();
	}

	/**
	 * Creates new expression and copies elements of parents sequence.
	 */
	public Expression(Expression parent) {
		this.sequence = Lists.newLinkedList(parent.sequence);
	}

	/**
	 * Adds children of the current expression. Typically, these are of the same
	 * class (e.g. WHERE objects produce WHERE objects).
	 */
	protected abstract void addChildren(List<Expression> children);

	/**
	 * Adds current regular expression part.
	 */
	protected abstract void addRegex(StringBuilder stringBuilder);

	/**
	 * Creates new object.
	 */
	protected abstract Expression getNewInstance(Expression parent);

	/**
	 * Adds wild-card '.*', if it is currently not at and of given StringBuilder.
	 */
	protected void addWildcard(StringBuilder stringBuilder) {
		if (stringBuilder != null && stringBuilder.length() >= 2) {
			if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
				stringBuilder.append(".*");
			}
		}
	}

	/**
	 * Walks through sequence and builds regular expression by calling
	 * {@link #addRegex(StringBuilder)}.
	 */
	protected void addSequenceToRegex(StringBuilder stringBuilder) {
		for (Expression expression : sequence) {
			expression.addRegex(stringBuilder);
		}
	}

	/**
	 * Returns result of {@link #addChildren(List)}.
	 */
	protected List<Expression> getChildren() {
		List<Expression> children = new LinkedList<Expression>();
		addChildren(children);
		return children;
	}

	/***
	 * Walks through current sequence of expressions, checks each entry for possible
	 * refinements, and adds them to children.
	 */
	protected void addRefinedSequenceTo(List<Expression> children) {
		// Get through sequence of expressions
		for (int i = 0; i < sequence.size(); i++) {
			Expression expression = sequence.get(i);

			// Create new sequence containing child and add instance to list of generated
			// children
			for (Expression child : expression.getChildren()) {

				// TODO
				if (!expression.getClass().isInstance(child)) {
					System.err.println("Expression addRefinedSequenceTo");
				}

				Expression newExpression = getNewInstance(this);
				newExpression.sequence.set(i, child);
				children.add(newExpression);
			}
		}
	}

}