package org.dice_research.spab.candidates.three;

/**
 * A feature can be represented as regular expression. It represents a SPARQL
 * part.
 * 
 * @author Adrian Wilke
 */
public abstract class SubFeature {

	/**
	 * Appends regular expression of sub-feature.
	 */
	public abstract void appendRegex(StringBuilder stringBuilder);
}