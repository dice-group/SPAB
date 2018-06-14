package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.dice_research.spab.input.Input;

/**
 * Triple refining itself based on input resources.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rTriplesBlock
 * 
 * @author Adrian Wilke
 */
public class Triple extends Expression {

	/**
	 * EMPTY: Only empty placeholder.
	 * 
	 * GENERIC: One resource inside triple.
	 * 
	 * FULL: S, P, O can contain resources.
	 */
	public static enum Type {
		EMPTY, GENERIC, FULL
	}

	protected Type type;
	protected String resource;
	protected String subject;
	protected String predicate;
	protected String object;

	/**
	 * Empty triple.
	 */
	public Triple() {
		super();
		this.type = Type.EMPTY;
	}

	/**
	 * Empty triple.
	 */
	public Triple(Expression parent) {
		super(parent);
		this.type = Type.EMPTY;
	}

	/**
	 * Generic triple.
	 */
	public Triple(Expression parent, String resource) {
		super(parent);
		this.type = Type.GENERIC;
		this.resource = resource;
	}

	/**
	 * Full triple.
	 */
	public Triple(Expression parent, String subject, String predicate, String object) {
		super(parent);
		this.type = Type.FULL;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public List<Expression> getChildren(Input input) {
		List<Expression> children = new LinkedList<Expression>();

		if (type.equals(Type.EMPTY)) {
			// Empty triples have to be replaced by resources
			for (String resource : input.getResources()) {
				children.add(new Triple(this, resource));
			}

		} else if (type.equals(Type.GENERIC)) {
			// Resources are moved to S, P, O
			children.add(new Triple(this, resource, null, null));
			children.add(new Triple(this, null, resource, null));
			children.add(new Triple(this, null, null, resource));

		} else if (type.equals(Type.FULL)) {
			// Adding resources at empty positions

			for (String resource : input.getResources()) {
				if (subject == null) {
					children.add(new Triple(this, resource, predicate, object));
				}
				if (predicate == null) {
					children.add(new Triple(this, subject, resource, object));
				}
				if (object == null) {
					children.add(new Triple(this, subject, predicate, resource));
				}
			}

		}
		return children;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {
	}

	@Override
	public void addString(StringBuilder stringBuilder) {

		if (type.equals(Type.EMPTY)) {
			// Empty triple represented by wild-card
			if (stringBuilder.length() >= 2 && !stringBuilder.substring(stringBuilder.length() - 2).equals(".*")) {
				stringBuilder.append(".*");
			}

		} else if (type.equals(Type.GENERIC)) {
			// Generic triple represented by resource
			stringBuilder.append(".*");
			stringBuilder.append(Pattern.quote("<" + resource + ">"));
			stringBuilder.append(".*");

		} else if (type.equals(Type.FULL)) {
			// Fill triple represented by resources and wild-cards
			if (subject != null) {
				stringBuilder.append(Pattern.quote("<" + subject + ">"));
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (predicate != null) {
				stringBuilder.append(Pattern.quote("<" + predicate + ">"));
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (object != null) {
				stringBuilder.append(Pattern.quote("<" + object + ">"));
			} else {
				stringBuilder.append(".*");
			}
		}
	}

	@Override
	public void addSuffix(StringBuilder stringBuilder) {
	}
}
