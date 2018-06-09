package org.dice_research.spab.candidates.four;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * A feature can be represented as regular expression. It represents a SPARQL
 * part.
 * 
 * @author Adrian Wilke
 */
public abstract class Expression {

	Expression parent;

	/**
	 * Creates expression without parent expression.
	 */
	public Expression() {
		this.parent = null;
	}

	/**
	 * Creates expression with parent expression.
	 */
	public Expression(Expression parent) {
		this.parent = parent;
	}

	/**
	 * Generates list of children of this feature.
	 * 
	 * @return List of child-features.
	 */
	public abstract List<Expression> getChildren(Input input);

	/**
	 * Adds prefix of regular expression (e.g. opening brackets).
	 */
	public abstract void addPrefix(StringBuilder stringBuilder);

	/**
	 * Appends suffix of regular expression (e.g. closing brackets).
	 */
	public abstract void addSuffix(StringBuilder stringBuilder);

	/**
	 * Adds prefix of parent expression. Adds wild-card.
	 */
	protected void addParentPrefix(StringBuilder stringBuilder) {
		if (parent != null) {
			parent.addPrefix(stringBuilder);
			if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
				stringBuilder.append(".*");
			}
		}
	}

	/**
	 * Adds wild-card. Adds suffix of parent expression.
	 */
	protected void addParentSuffix(StringBuilder stringBuilder) {
		if (parent != null) {
			if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
				stringBuilder.append(".*");
			}
			parent.addSuffix(stringBuilder);
		}
	}
}