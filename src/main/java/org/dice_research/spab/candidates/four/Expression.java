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
	 * If all parents are instances of the class to check, a wild-card is added.
	 */
	protected void addWildcardIfRootClass(StringBuilder stringBuilder, Class<?> classToCheck) {
		Expression parentExpression = parent;
		while (classToCheck.isInstance(parentExpression)) {
			parentExpression = parentExpression.parent;
		}
		if (parentExpression == null) {
			stringBuilder.append(".*");
		}
	}

	/**
	 * Adds prefix of parent expression. Adds wild-card.
	 */
	protected void addParentPrefix(StringBuilder stringBuilder, Class<?> parentClassToOmit, boolean addWildCard) {

		// If parent is null do nothing
		if (parent != null) {

			if (parentClassToOmit == null) {
				// If nothing to omit, add parent

				parent.addPrefix(stringBuilder);

				if (addWildCard) {
					if (stringBuilder.length() >= 2) {
						if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
							stringBuilder.append(".*");
						}
					}
				}

			} else {
				// Add first parent, which should not be omitted

				Expression parentExpression = parent;
				while (parentClassToOmit.isInstance(parentExpression)) {
					parentExpression = parentExpression.parent;
				}
				if (parentExpression != null) {
					parentExpression.addPrefix(stringBuilder);
				}

				if (addWildCard) {
					if (stringBuilder.length() >= 2) {
						if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
							stringBuilder.append(".*");
						}
					}
				}
			}
		}
	}

	/**
	 * Adds wild-card. Adds suffix of parent expression.
	 */
	protected void addParentSuffix(StringBuilder stringBuilder, Class<?> parentClassToOmit, boolean addWildCard) {

		// If parent is null do nothing
		if (parent != null) {

			if (parentClassToOmit == null) {
				// If nothing to omit, add parent

				if (addWildCard) {
					if (stringBuilder.length() >= 2) {
						if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
							stringBuilder.append(".*");
						}
					}
				}

				parent.addSuffix(stringBuilder);

			} else {
				// Add first parent, which should not be omitted

				Expression parentExpression = parent;
				while (parentClassToOmit.isInstance(parentExpression)) {
					parentExpression = parentExpression.parent;
				}
				if (parentExpression != null) {

					if (addWildCard) {
						if (stringBuilder.length() >= 2) {
							if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
								stringBuilder.append(".*");
							}
						}
					}

					parentExpression.addSuffix(stringBuilder);
				}
			}
		}
	}
}