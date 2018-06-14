package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
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

		// Add empty triple
		triples.add(new Triple(this));
	}

	public Where(Expression parent) {
		super(parent);

		// TODO: Check if necessary
		if (parent instanceof Where) {
			triples.addAll(((Where) parent).triples);
		}

		// Add empty triple
		triples.add(new Triple(this));
	}

	public Where(Expression parent, List<Triple> triples) {
		super(parent);
		this.triples = triples;
	}

	@Override
	public List<Expression> getChildren(Input input) {
		List<Expression> children = new LinkedList<Expression>();

		// Add additional triple
		if (triples.size() < MAX_TRIPLES) {
			children.add(new Where(this));
		}

		// Refine existing triples
		for (int t = 0; t < triples.size(); t++) {
			for (Expression expressionToInsert : triples.get(t).getChildren(input)) {
				LinkedList<Triple> newTriples = Lists.newLinkedList(triples);
				newTriples.set(t, (Triple) expressionToInsert);
				children.add(new Where(this, newTriples));
			}
		}

		return children;
	}

	@Override
	public void addPrefix(StringBuilder stringBuilder) {

		addWildcardIfRootClass(stringBuilder, Where.class);

		addParentPrefix(stringBuilder, Where.class, true);

		stringBuilder.append("WHERE \\{ ");
	}

	@Override
	public void addString(StringBuilder stringBuilder) {
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

		addParentSuffix(stringBuilder, Where.class, true);
	}

}
