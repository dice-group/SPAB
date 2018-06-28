package org.dice_research.spab.candidates.six;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * GroupOrUnionGraphPattern ::= GroupGraphPattern ( 'UNION' GroupGraphPattern )*
 * 
 * Generated by {@link GraphPatternNotTriples}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rGroupOrUnionGraphPattern
 * 
 * @author Adrian Wilke
 */
public class GroupOrUnionGraphPattern extends Expression {

	enum Type {
		INITIAL, REFINED
	};

	protected Type type = Type.INITIAL;

	// TODO Handle by input
	protected final static int MAX_UNIONS = 2;
	protected int counter;

	public GroupOrUnionGraphPattern() {
		sequence.add(new GroupGraphPattern());
		sequence.add(new ExpressionString(" UNION "));
		sequence.add(new GroupGraphPattern());
		counter = MAX_UNIONS;
	}

	public GroupOrUnionGraphPattern(Expression origin) {
		super(origin);
		counter = ((GroupOrUnionGraphPattern) origin).counter;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new GroupOrUnionGraphPattern(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> refinements = super.getRefinements(input);

		if (type.equals(Type.INITIAL) && counter > 1) {
			GroupOrUnionGraphPattern groupOrUnionGraphPattern = new GroupOrUnionGraphPattern(this);
			groupOrUnionGraphPattern.counter = counter - 1;
			groupOrUnionGraphPattern.type = Type.INITIAL;

			groupOrUnionGraphPattern.sequence.add(new ExpressionString(" UNION "));
			groupOrUnionGraphPattern.sequence.add(new GroupGraphPattern());

			refinements.add(groupOrUnionGraphPattern);

			type = Type.REFINED;
		}

		return refinements;
	}
}