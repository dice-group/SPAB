package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * LimitOffsetClauses ::= LimitClause OffsetClause? | OffsetClause LimitClause?
 * 
 * LimitClause ::= 'LIMIT' INTEGER
 * 
 * OffsetClause ::= 'OFFSET' INTEGER
 * 
 * Generated by {@link SolutionModifier}.
 * 
 * Typically, {@link #getAllInstances()} will be called to get all initial
 * combinations of LIMIT and OFFSET.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rLimitOffsetClauses
 * 
 * @author Adrian Wilke
 */
public class LimitOffsetClauses extends Expression {

	public static List<Expression> getInitialInstances() {
		List<Expression> instances = new LinkedList<Expression>();
		LimitOffsetClauses instance;

		instance = new LimitOffsetClauses();
		instance.type = Type.LIMIT;
		instance.sequence.add(new LimitClause());
		instances.add(instance);

		instance = new LimitOffsetClauses();
		instance.type = Type.OFFSET;
		instance.sequence.add(new OffsetClause());
		instances.add(instance);

		return instances;
	}

	enum Type {
		LIMIT, OFFSET, REFINED
	};

	protected Type type;

	public LimitOffsetClauses() {
		super();
	}

	public LimitOffsetClauses(Expression origin) {
		super(origin);
		type = ((LimitOffsetClauses) origin).type;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new LimitOffsetClauses(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}

	@Override
	public List<Expression> getRefinements(Input input) {

		List<Expression> refinements = super.getRefinements(input);
		if (type.equals(Type.LIMIT)) {
			LimitOffsetClauses refinement;

			refinement = new LimitOffsetClauses(this);
			refinement.type = Type.REFINED;
			refinement.sequence.add(new OffsetClause());
			refinements.add(refinement);

			type = Type.REFINED;
		} else if (type.equals(Type.OFFSET)) {
			LimitOffsetClauses refinement;

			refinement = new LimitOffsetClauses(this);
			refinement.type = Type.REFINED;
			refinement.sequence.add(new LimitClause());
			refinements.add(refinement);

			type = Type.REFINED;
		}
		return refinements;
	}

	/**
	 * Creates initial instances and all refinements.
	 */
	public List<Expression> getAllInstances() {
		// Get initial instances
		List<Expression> instances = getInitialInstances();

		// Refine until no additional instances are created
		List<Expression> newInstances = null;
		while (newInstances == null || !newInstances.isEmpty()) {
			newInstances = new LinkedList<Expression>();
			for (Expression instance : instances) {
				newInstances.addAll(instance.getRefinements(null));
			}
			instances.addAll(newInstances);
		}

		return instances;
	}
}