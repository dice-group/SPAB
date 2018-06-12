package org.dice_research.spab.candidates.five;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;

public class SpabFiveCandidate implements Candidate {

	protected CandidateVertex candidateVertex;
	protected Expression expression;

	public SpabFiveCandidate() {
		this.expression = new Root();
	}

	public SpabFiveCandidate(Expression expression) {
		this.expression = expression;
	}

	@Override
	public List<Candidate> getChildren(Input input) throws CandidateRuntimeException {
		List<Candidate> children = new LinkedList<Candidate>();
		List<Expression> expressions = new LinkedList<Expression>();
		expression.addChildren(expressions);
		for (Expression expression : expressions) {
			children.add(new SpabFiveCandidate(expression));
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

	// TODO
	public static void main(String[] args) {
		SpabFiveCandidate candidate = new SpabFiveCandidate();
		Input input = new Input();
		recursive(candidate, input);
	}

	// TODO
	public static void recursive(Candidate candidate, Input input) {
		System.out.println();
		System.out.println("> " + candidate.getRegEx());
		List<Candidate> children = candidate.getChildren(input);
		for (Candidate child : children) {
			System.out.println("| " + child.getRegEx());
		}
		for (Candidate child : children) {
			recursive(child, input);
		}
	}
}