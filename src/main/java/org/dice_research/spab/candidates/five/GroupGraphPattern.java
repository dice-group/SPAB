package org.dice_research.spab.candidates.five;

import java.util.List;

/**
 * GroupGraphPattern ::= '{' ( SubSelect | GroupGraphPatternSub ) '}'
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rGroupGraphPattern
 * 
 * @author Adrian Wilke
 */
public class GroupGraphPattern extends Expression {

	public GroupGraphPattern() {
		super();
	}

	public GroupGraphPattern(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new GroupGraphPattern(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {
		if (sequence.isEmpty()) {
			GroupGraphPattern groupGraphPattern = new GroupGraphPattern(this);
			groupGraphPattern.sequence.add(new GroupGraphPatternSub());
			children.add(groupGraphPattern);
		}
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		stringBuilder.append("\\{");
		if (sequence.isEmpty()) {
			stringBuilder.append(".*");
		} else {
			addSequenceRegex(stringBuilder);
		}
		stringBuilder.append("\\}");
		addWildcard(stringBuilder);
	}
}