package org.dice_research.spab.candidates.six;

/**
 * Expression to add static Strings into sequence.
 * 
 * @author Adrian Wilke
 */
public class ExpressionString extends Expression {

	protected String string;

	public ExpressionString(String string) {
		super();
		this.string = string;
	}

	public ExpressionString(Expression origin) {
		super(origin);
		string = ((ExpressionString) origin).string;
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new ExpressionString(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append(string);
	}

}