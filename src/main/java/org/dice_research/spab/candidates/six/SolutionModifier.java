package org.dice_research.spab.candidates.six;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * SolutionModifier ::= GroupClause? HavingClause? OrderClause?
 * LimitOffsetClauses?
 * 
 * Generated by {@link SelectQuery}
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSolutionModifier
 * 
 * @author Adrian Wilke
 */
public class SolutionModifier extends Expression {

	enum Type {
		INITIAL, REFINED
	};

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
		List<Expression> refinements = super.getRefinements(input);
		if (type.equals(Type.INITIAL)) {
			SolutionModifier refinement;

			refinement = new SolutionModifier(this);
			refinement.type = Type.REFINED;
			refinement.sequence.add(new GroupClause());
			refinements.add(refinement);

			refinement = new SolutionModifier(this);
			refinement.type = Type.REFINED;
			refinement.sequence.add(new HavingClause());
			refinements.add(refinement);

			refinement = new SolutionModifier(this);
			refinement.type = Type.REFINED;
			refinement.sequence.add(new OrderClause());
			refinements.add(refinement);

			for (Expression expression : new LimitOffsetClauses().getInitialInstances()) {
				refinement = new SolutionModifier(this);
				refinement.type = Type.REFINED;
				refinement.sequence.add(expression);
				refinements.add(refinement);
			}

			type = Type.REFINED;
		}
		return refinements;
	}
}