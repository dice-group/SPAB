package org.dice_research.spab.candidates.six;

/**
 * GroupGraphPatternSub ::= TriplesBlock? ( GraphPatternNotTriples '.'?
 * TriplesBlock? )*
 * 
 * Generated by {@link GroupGraphPattern}.
 * 
 * Contains one {@link TriplesBlock}.
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rGroupGraphPattern
 * 
 * @author Adrian Wilke
 */
public class GroupGraphPatternSub extends Expression {

	public GroupGraphPatternSub() {
		TriplesBlock triplesBlock = new TriplesBlock();
		sequence.add(triplesBlock);
	}

	public GroupGraphPatternSub(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new GroupGraphPatternSub(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}
}