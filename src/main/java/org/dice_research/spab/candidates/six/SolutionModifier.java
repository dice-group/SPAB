package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * SolutionModifier ::= GroupClause? HavingClause? OrderClause?
 * LimitOffsetClauses?
 * 
 * Generated by {@link SelectQuery}.
 * 
 * First instance will be expression without additions to regular expression.
 * This meets the "all optional" case. The first call of
 * {@link #getRefinements(Input)} will return all initial combinations of
 * solution modifiers.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSolutionModifier
 * 
 * @author Adrian Wilke
 */
public class SolutionModifier extends Expression {

	enum Type {
		INITIAL, REFINED
	}

	protected Type type;

	public SolutionModifier() {
		super();
		type = Type.INITIAL;
	}

	public SolutionModifier(Expression origin) {
		super(origin);
		type = ((SolutionModifier) origin).type;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new SolutionModifier(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		boolean empty = false;
		if (stringBuilder.length() == 0) {
			empty = true;
		}

		addSequenceToRegex(stringBuilder);

		if (empty && !sequence.isEmpty()) {
			encloseWithWildcards(stringBuilder);
		}
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> expressions = super.getRefinements(input);

		if (type.equals(Type.INITIAL)) {

			// This type will also be used for new instances
			type = Type.REFINED;

			expressions.addAll(createInstances(true, false, false, false));
			expressions.addAll(createInstances(false, true, false, false));
			expressions.addAll(createInstances(false, false, true, false));
			expressions.addAll(createInstances(false, false, false, true));

			expressions.addAll(createInstances(true, true, false, false));
			expressions.addAll(createInstances(true, false, true, false));
			expressions.addAll(createInstances(true, false, false, true));
			expressions.addAll(createInstances(false, true, true, false));
			expressions.addAll(createInstances(false, true, false, true));
			expressions.addAll(createInstances(false, false, true, true));

			expressions.addAll(createInstances(true, true, true, false));
			expressions.addAll(createInstances(true, true, false, true));
			expressions.addAll(createInstances(true, false, true, true));
			expressions.addAll(createInstances(false, true, true, true));

			expressions.addAll(createInstances(true, true, true, true));
		}

		return expressions;
	}

	/**
	 * Returns all initial combinations of different solution modifiers.
	 */
	protected List<SolutionModifier> createInstances(boolean addGroup, boolean addHaving, boolean addOrder,
			boolean addLimitOffset) {
		List<SolutionModifier> instances = new LinkedList<SolutionModifier>();

		SolutionModifier solutionModifier = new SolutionModifier(this);
		if (addGroup) {
			solutionModifier.sequence.add(new GroupClause());
		}
		if (addHaving) {
			solutionModifier.sequence.add(new HavingClause());
		}
		if (addOrder) {
			solutionModifier.sequence.add(new OrderClause());
		}

		// Add several LIMIT OFFSET combinations
		if (addLimitOffset) {
			for (Expression expression : new LimitOffsetClauses().getAllInstances()) {
				// Copy current version and add LimitOffset variations
				SolutionModifier solutionModifierRefinement = new SolutionModifier(solutionModifier);
				solutionModifierRefinement.sequence.add(expression);
				instances.add(solutionModifierRefinement);
			}
		} else {
			// Just return one instance
			instances.add(solutionModifier);
		}
		return instances;
	}
}