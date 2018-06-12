package org.dice_research.spab.candidates.five;

import java.util.List;

public class SolutionModifier extends Expression {

	public SolutionModifier() {
		super();
	}

	public SolutionModifier(Expression parent) {
		super(parent);
	}

	@Override
	protected Expression getNewInstance(Expression parent) {
		return new SolutionModifier(parent);
	}

	@Override
	protected void addChildren(List<Expression> children) {

		if (sequence.size() == 0) {
			// Add GROUP
			SolutionModifier solutionModifier = new SolutionModifier();
			solutionModifier.sequence.add(new GroupClause());
			children.add(solutionModifier);

			// Add HAVING
			solutionModifier = new SolutionModifier();
			solutionModifier.sequence.add(new HavingClause());
			children.add(solutionModifier);
		}
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceRegex(stringBuilder);
	}
}