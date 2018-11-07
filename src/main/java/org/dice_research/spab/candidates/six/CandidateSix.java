package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.Candidate;
import org.dice_research.spab.exceptions.CandidateRuntimeException;
import org.dice_research.spab.input.Input;
import org.dice_research.spab.structures.CandidateVertex;

/**
 * Candidate implementation which uses {@link Expression} and its subclasses to
 * generate regular expressions.
 * 
 * The initial expression is {@link Root}, next generation children are
 * generated by {@link Root#getRefinements(Input)}.
 *
 * @author Adrian Wilke
 */
public class CandidateSix<InternalRepresentation> implements Candidate<InternalRepresentation> {

	protected CandidateVertex candidateVertex;
	protected Expression expression;

	/**
	 * Creates root candidate.
	 */
	public CandidateSix() {
		this.expression = new Root();
	}

	/**
	 * Creates candidate based on given expression. Used in
	 * {@link CandidateSix#getChildren(Input)}.
	 */
	public CandidateSix(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Uses {@link CandidateSix#expression} object to refine regular expression and
	 * to generate children.
	 */
	@Override
	public List<Candidate<InternalRepresentation>> getChildren(Input input) throws CandidateRuntimeException {
		List<Candidate<InternalRepresentation>> children = new LinkedList<Candidate<InternalRepresentation>>();
		for (Expression expression : expression.getRefinements(input)) {
			children.add(new CandidateSix<InternalRepresentation>(expression));
		}
		return children;
	}

	/**
	 * Uses {@link CandidateSix#expression} object to create regular expression.
	 */
	@Override
	public String getRegEx() throws CandidateRuntimeException {
		StringBuilder stringBuilder = new StringBuilder();
		expression.addRegex(stringBuilder);
		return stringBuilder.toString();
	}

	/**
	 * Returns {@link Expression} instance.
	 * 
	 * @throws CandidateRuntimeException on casting errors.
	 */
	@Override
	public InternalRepresentation getInternalRepresentation(Class<InternalRepresentation> internalRepresentationClass)
			throws CandidateRuntimeException {
		if (!internalRepresentationClass.equals(Expression.class)) {
			throw new CandidateRuntimeException("The internal representation is " + Expression.class.getName());
		}
		return internalRepresentationClass.cast(expression);
	}
}