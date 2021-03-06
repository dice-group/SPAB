package org.dice_research.spab.candidates.six;

import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * Var ::= VAR1 | VAR2
 * 
 * VAR1 ::= '?' VARNAME
 * 
 * VAR2 ::= '$' VARNAME
 * 
 * Variable are formatted by Jena to use '?' in every case. This implementation
 * count the numbers of variables.
 * 
 * Generated by {@link SelectClause}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rVAR1
 * 
 * @author Adrian Wilke
 */
public class Var extends Expression {

	enum Type {
		INITIAL, REFINED
	};

	protected Type type = Type.INITIAL;

	protected int counter;

	public Var() {
		counter = 1;
	}

	public Var(Expression origin) {
		super(origin);
		counter = ((Var) origin).counter;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new Var(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
		for (int v = 0; v < counter; v++) {
			stringBuilder.append("\\?.* ");
		}
	}

	@Override
	public List<Expression> getRefinements(Input input) {
		List<Expression> refinements = super.getRefinements(input);

		if (type.equals(Type.INITIAL) && counter < input.getMaxVariables()) {
			Var var = new Var(this);
			var.counter = counter + 1;
			var.type = Type.INITIAL;
			refinements.add(var);

			type = Type.REFINED;
		}

		return refinements;
	}
}