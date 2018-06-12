package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * SelectQuery ::= SelectClause DatasetClause* WhereClause SolutionModifier
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSelectQuery
 * 
 * @author Adrian Wilke
 */
public class SelectQuery extends Expression {

	public SelectQuery() {
		super();
	}

	public SelectQuery(SelectQuery parent) {
		this((Expression) parent);
	}

	public SelectQuery(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new SelectQuery(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {

		if (sequence.size() == 0) {
			// Add SELECT
			SelectQuery selectQuery = new SelectQuery();
			selectQuery.sequence.add(new SelectClause());
			children.add(selectQuery);
			// TODO
			System.out.println("x");

		} else if (sequence.size() == 1) {
			// Add WHERE
			SelectQuery selectQuery = new SelectQuery(this);
			selectQuery.sequence.add(new WhereClause());
			children.add(selectQuery);

			// Add Solution Modifier
			for (Expression child : new SolutionModifier().getChildren()) {
				selectQuery = new SelectQuery(this);
				selectQuery.sequence.add(child);
				children.add(selectQuery);
			}
			// TODO
			System.out.println("y");
		}

		// TODO overflow
		refineSequence(children);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceRegex(stringBuilder);
	}

}