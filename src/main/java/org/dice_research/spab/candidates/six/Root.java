package org.dice_research.spab.candidates.six;


/**
 * Most general expression.
 * 
 * @author Adrian Wilke
 */
public class Root extends Expression {

	public Root() {
		Query query = new Query();
		sequence.add(query);
	}

	public Root(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new Root(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
	}
}