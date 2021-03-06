package org.dice_research.spab.candidates.six;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * SelectClause ::= 'SELECT' ( 'DISTINCT' | 'REDUCED' )? ( ( Var | ( '('
 * Expression 'AS' Var ')' ) )+ | '*' )
 * 
 * Generated by {@link SelectQuery}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rSelectClause
 * 
 * @author Adrian Wilke
 */
public class SelectClause extends Expression {

	enum Type {
		INITIAL, LOCKED
	};

	protected Type type = Type.LOCKED;

	public SelectClause() {
		super();
		type = Type.INITIAL;
	}

	public SelectClause(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new SelectClause(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("SELECT ");
		addWildcard(stringBuilder);
		addSequenceToRegex(stringBuilder);
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> refinements = super.getRefinements(input);
		if (type.equals(Type.INITIAL)) {
			SelectClause selectClause;

			selectClause = new SelectClause(this);
			selectClause.sequence.add(new ExpressionString("\\*"));
			selectClause.type = Type.LOCKED;
			refinements.add(selectClause);

			selectClause = new SelectClause(this);
			selectClause.sequence.add(new Var());
			selectClause.type = Type.LOCKED;
			refinements.add(selectClause);

			type = Type.LOCKED;
		}
		return refinements;
	}
}