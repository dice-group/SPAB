package org.dice_research.spab.candidates.six;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * Most general expression.
 * 
 * @author Adrian Wilke
 */
public class Root extends Expression {

	enum Type {
		INITIAL, INITIAL_REFINED, CHILD
	}

	protected Type type;

	public Root() {
		type = Type.INITIAL;
	}

	public Root(Expression origin) {
		super(origin);
		type = ((Root) origin).type;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new Root(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		if (type.equals(Type.CHILD)) {
			addSequenceToRegex(stringBuilder);
		} else {
			stringBuilder.append(".*");
		}
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> refinements = super.getRefinements(input);

		if (type.equals(Type.INITIAL)) {
			Root refinement;

			for (Expression expression : Query.getInitialInstances()) {
				refinement = new Root();
				refinement.type = Type.CHILD;
				refinement.sequence.add(expression);
				refinements.add(refinement);
			}

			refinement = new Root();
			refinement.type = Type.CHILD;
			WhereClause where = new WhereClause();
			refinement.sequence.add(where);
			refinements.add(refinement);

			refinement = new Root();
			refinement.type = Type.CHILD;
			GraphPatternNotTriples graphPatternNotTriples = new GraphPatternNotTriples();
			refinement.sequence.add(graphPatternNotTriples);
			refinements.add(refinement);

			for (Expression expression : new SolutionModifier().getRefinements(input)) {
				refinement = new Root();
				refinement.type = Type.CHILD;
				refinement.sequence.add(expression);
				refinements.add(refinement);
			}

			type = Type.INITIAL_REFINED;
		}

		return refinements;
	}
}