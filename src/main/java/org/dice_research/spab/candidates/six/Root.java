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
		INITIAL, REFINED
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
		addSequenceToRegex(stringBuilder);
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> refinements = super.getRefinements(input);

		if (type.equals(Type.INITIAL)) {
			Root refinement;

			refinement = new Root();
			refinement.type = Type.REFINED;
			Query query = new Query();
			refinement.sequence.add(query);
			refinements.add(refinement);

			refinement = new Root();
			refinement.type = Type.REFINED;
			WhereClause where = new WhereClause();
			refinement.sequence.add(where);
			refinements.add(refinement);

			refinement = new Root();
			refinement.type = Type.REFINED;
			GraphPatternNotTriples graphPatternNotTriples = new GraphPatternNotTriples();
			refinement.sequence.add(graphPatternNotTriples);
			refinements.add(refinement);

			type = Type.REFINED;
		}

		return refinements;
	}
}