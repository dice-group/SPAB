package org.dice_research.spab.candidates.four;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.candidates.four.Query.Type;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Candidate representing a set of SPARQL queries represented by a regular
 * expression.
 * 
 * @author Adrian Wilke
 */
public class SpabFourCandidate implements Candidate {

	protected CandidateVertex candidateVertex;
	protected Expression expression;

	/**
	 * Creates initial candidate.
	 */
	public SpabFourCandidate() {
		this.expression = new Expression() {

			/**
			 * Adds all initial variations.
			 */
			@Override
			public List<Expression> getChildren(Input input) {
				List<Expression> children = new LinkedList<Expression>();

				// TODO
				// children.add(new Triple(null));

				children.add(new Where());

				for (Type type : Query.Type.values()) {
					children.add(new Query(type));
				}

				return children;
			}

			@Override
			public void addPrefix(StringBuilder stringBuilder) {
				stringBuilder.append(".*");
			}

			@Override
			public void addSuffix(StringBuilder stringBuilder) {
			}
		};
	}

	/**
	 * Creates candidate using an expression.
	 */
	public SpabFourCandidate(Expression expression) {
		if (expression == null) {
			throw new RuntimeException("Candidate has to be instantiated with expression.");
		}
		this.expression = expression;
	}

	@Override
	public List<Candidate> getChildren(Input input) throws CandidateRuntimeException {
		List<Candidate> candidateChildren = new LinkedList<Candidate>();
		List<Expression> expressionChildren = expression.getChildren(input);
		if (expressionChildren != null) {
			for (Expression expressionChild : expression.getChildren(input)) {
				candidateChildren.add(new SpabFourCandidate(expressionChild));
			}
		}
		return candidateChildren;
	}

	@Override
	public String getRegEx() throws CandidateRuntimeException {
		StringBuilder stringBuilder = new StringBuilder();
		expression.addPrefix(stringBuilder);
		expression.addSuffix(stringBuilder);
		return stringBuilder.toString();
	}

	@Override
	public void setVertex(CandidateVertex candidateVertex) {
		this.candidateVertex = candidateVertex;
	}

}