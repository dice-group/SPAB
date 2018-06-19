package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_research.spab.input.Input;

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
	protected List<Expression> sequence = new LinkedList<Expression>();

	/**
	 * Creates new type of expression.
	 */
	Expression() {
	}

	/**
	 * Creates expression based on origin. By default, the sequence of origin is
	 * duplicated. Used in {@link #createInstance(Expression)}.
	 */
	Expression(Expression origin) {
		this.sequence = Lists.newLinkedList(origin.sequence);
	}

	/**
	 * Creates new instance for refinement in
	 * {@link #getRefinementsOfSequence(Input)}.
	 */
	protected abstract Expression createInstance(Expression origin);

	/**
	 * Creates an initial list of instances, if more than one initial instance
	 * (a.k.a. the default constructor without parameters) is required.
	 */
	protected List<Expression> getInitialInstances() {
		return new LinkedList<Expression>();
	}

	/**
	 * Adds current regular expression part.
	 */
	protected abstract void addRegex(StringBuilder stringBuilder);

	/**
	 * Builds regular expression based on {@link #addRegex(StringBuilder)}.
	 */
	public String getRegex() {
		StringBuilder stringBuilder = new StringBuilder();
		addRegex(stringBuilder);
		return stringBuilder.toString();
	}

	/**
	 * Gets refinements of same type of expression. By default,
	 * {@link #getRefinementsOfSequence(Input)} is called.
	 */
	public List<Expression> getRefinements(Input input) {
		return getRefinementsOfSequence(input);
	}

	/**
	 * Returns list of expressions of the same type of this element. The elements in
	 * the list are refinements of expression sequences. By default, this is used in
	 * {@link #getRefinements(Input)}.
	 */
	protected List<Expression> getRefinementsOfSequence(Input input) {
		List<Expression> refinements = new LinkedList<Expression>();

		// Go through sequence
		for (int i = 0; i < sequence.size(); i++) {

			// Check sequence element for refinements
			Expression sequenceElement = sequence.get(i);

			for (Expression sequenceElementRefinement : sequenceElement.getRefinements(input)) {

				// For each refinement of sequence element: Create new sequence (inside element
				// of same type as this object) and replace element by refinement.
				Expression newInstance = createInstance(this);
				newInstance.sequence.set(i, sequenceElementRefinement);

				// Add new sequence (inside element of same type as this object) to refinements
				refinements.add(newInstance);
			}
		}
		return refinements;
	}

	/**
	 * Walks through sequence and builds regular expression by calling
	 * {@link #addRegex(StringBuilder)} for each element of sequence.
	 */
	protected void addSequenceToRegex(StringBuilder stringBuilder) {
		for (Expression expression : sequence) {
			expression.addRegex(stringBuilder);
		}
	}

	/**
	 * Walks through sequence and builds regular expression by calling
	 * {@link #addRegex(StringBuilder)} for each element of sequence.
	 * 
	 * Adds {@code separator} between elements.
	 */
	protected void addSequenceToRegex(StringBuilder stringBuilder, String separator) {
		for (int i = 0; i < sequence.size(); i++) {
			if (i != 0) {
				stringBuilder.append(separator);
			}
			sequence.get(i).addRegex(stringBuilder);
		}
	}

	/**
	 * Adds wild-card '.*', if it is currently not at and of given StringBuilder.
	 */
	protected void addWildcard(StringBuilder stringBuilder) {
		if (stringBuilder != null) {
			if (stringBuilder.length() >= 2) {
				if (!stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
					stringBuilder.append(".*");
				}
			} else {
				stringBuilder.append(".*");
			}
		}
	}

	/**
	 * Adds wild-card '.*' at begin and end.
	 */
	protected void encloseWithWildcards(StringBuilder stringBuilder) {
		// Begin
		if (stringBuilder.length() >= 2) {
			if (!stringBuilder.substring(0, 2).equals(".*")) {
				stringBuilder.insert(0, ".*");
			}
		} else {
			stringBuilder.append(".*");
		}

		// End
		addWildcard(stringBuilder);
	}
}