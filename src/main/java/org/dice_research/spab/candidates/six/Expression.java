package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.dice_research.spab.input.Input;

/**
 * A superclass for SPARQL features/properties/parts which are represented as
 * part of a regular expression. The SPARQL features are implemented by
 * subclasses.
 * 
 * @author Adrian Wilke
 */
public abstract class Expression {

	/**
	 * Sequence of expression parts. The whole sequence represents a complete
	 * regular expression.
	 */
	protected List<Expression> sequence = new LinkedList<Expression>();

	/**
	 * Creates new type of expression. Subclasses use this constructor to set class
	 * variables.
	 */
	public Expression() {
	}

	/**
	 * Creates an expression based on the origin expression. By default, subclasses
	 * just call 'super(origin)' to duplicate the sequence of the origin expression.
	 * 
	 * If subclasses define additional class variables, these should be duplicated
	 * in this constructor.
	 * 
	 * Implementation note: Do not add functionality in the constructor, as it will
	 * be used by {@link #getRefinementsOfSequence(Input)} to create a duplicate of
	 * the origin object and the related properties.
	 * 
	 * Used in {@link #createInstance(Expression)}.
	 */
	public Expression(Expression origin) {
		this.sequence = Lists.newLinkedList(origin.sequence);
	}

	/**
	 * Used to create instances of subclasses.
	 * 
	 * Subclasses should return their implementation of
	 * {@link #Expression(Expression)}.
	 * 
	 * Used to creates new instances for refinement in
	 * {@link #getRefinementsOfSequence(Input)}.
	 */
	protected abstract Expression createInstance(Expression origin);

	/**
	 * Subclasses should add their regular expression part.
	 */
	protected abstract void addRegex(StringBuilder stringBuilder);

	/**
	 * Builds regular expression by calling subclass implementations of
	 * {@link #addRegex(StringBuilder)}.
	 */
	public String getRegex() {
		StringBuilder stringBuilder = new StringBuilder();
		addRegex(stringBuilder);
		return stringBuilder.toString();
	}

	/**
	 * Gets refinements of same type of expression.
	 * 
	 * By default the sequence of expression parts is refined by
	 * {@link #getRefinementsOfSequence(Input)}.
	 * 
	 * Subclasses may add additional refinements.
	 */
	public List<Expression> getRefinements(Input input) {
		return getRefinementsOfSequence(input);
	}

	/**
	 * Gets sequence of current candidate.
	 */
	public List<Expression> getSequence() {
		return this.sequence;
	}

	/**
	 * Returns list of expressions of the same type of this element. The elements in
	 * the returned list are refinements of expression sequences.
	 * 
	 * This is the default refinement used in {@link #getRefinements(Input)}.
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
	 * Adds wild-card '.*', if it is currently not at end of given StringBuilder.
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

	/**
	 * Adds names of classes to stringBuilder. Used in tests to keep
	 * {@link Expression#sequence} protected.
	 * 
	 * @deprecated Sequence can be accessed now.
	 */
	public void addClassNames(StringBuilder stringBuilder) {
		stringBuilder.append(getClass().getSimpleName());
		if (!sequence.isEmpty()) {
			stringBuilder.append("[ ");
			for (Expression expression : sequence) {
				expression.addClassNames(stringBuilder);
			}
			stringBuilder.append(" ]");
		}
	}

	/**
	 * Gets hierarchy of expressions as several lines containing the related
	 * expression class name and the respective regular expression.
	 */
	public void getHierarchy(StringBuilder stringBuilder) {
		getHierarchy(stringBuilder, 0, 0);
	}

	/**
	 * Gets hierarchy of expressions as several lines containing the related
	 * expression class name and the respective regular expression.
	 */
	protected void getHierarchy(StringBuilder stringBuilder, int indent, int separatorLength) {

		for (int i = 0; i < indent; i++) {
			stringBuilder.append(" ");
		}

		stringBuilder.append(this.getClass().getSimpleName());

		if (separatorLength == 0) {
			separatorLength = getHierarchyLength(indent);
		}
		for (int i = 0; i < separatorLength - this.getClass().getSimpleName().length() - indent; i++) {
			stringBuilder.append(" ");
		}

		stringBuilder.append(" ");
		stringBuilder.append(getRegex());

		for (Expression expression : sequence) {
			stringBuilder.append(System.lineSeparator());
			expression.getHierarchy(stringBuilder, indent + 1, separatorLength);
		}
	}

	/**
	 * Gets maximum length of class name and indent of current class and all
	 * children.
	 */
	protected int getHierarchyLength(int indent) {
		int max = this.getClass().getSimpleName().length() + indent;
		for (Expression expression : sequence) {
			max = Math.max(max, expression.getHierarchyLength(indent + 1));
		}
		return max;
	}
}