package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

/**
 * Query ::= Prologue ( SelectQuery | ConstructQuery | DescribeQuery | AskQuery
 * ) ValuesClause
 * 
 * Generated by {@link Root}.
 * 
 * Prologue is not implemented, as PREFIXes are removed.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rQuery
 * 
 * @author Adrian Wilke
 */
public class Query extends Expression {

	public static List<Expression> getInitialInstances() {
		List<Expression> instances = new LinkedList<Expression>();
		Query instance;

		instance = new Query();
		instance.sequence.add(new SelectQuery());
		instances.add(instance);

		instance = new Query();
		instance.sequence.add(new ConstructQuery());
		instances.add(instance);

		instance = new Query();
		instance.sequence.add(new DescribeQuery());
		instances.add(instance);

		instance = new Query();
		instance.sequence.add(new AskQuery());
		instances.add(instance);

		return instances;
	}

	public Query() {
		super();
	}

	public Query(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new Query(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}
}