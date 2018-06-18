package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;

public class CandidateSix implements Candidate {

	protected CandidateVertex candidateVertex;
	protected Expression expression;

	public CandidateSix() {
		this.expression = new Root();
	}

	public CandidateSix(Expression expression) {
		this.expression = expression;
	}

	@Override
	public List<Candidate> getChildren(Input input) throws CandidateRuntimeException {
		List<Candidate> children = new LinkedList<Candidate>();
		for (Expression expression : expression.getRefinements(input)) {
			children.add(new CandidateSix(expression));
		}
		return children;
	}

	@Override
	public String getRegEx() throws CandidateRuntimeException {
		StringBuilder stringBuilder = new StringBuilder();
		expression.addRegex(stringBuilder);
		return stringBuilder.toString();
	}

	@Override
	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}

}