package org.dice_research.spab.candidates.three;

import java.util.regex.Pattern;

/**
 * Generic representation of a triple. It can be empty (just a placeholder) or
 * contain a resource.
 * 
 * @author Adrian Wilke
 */
public class TripleFeature extends SubFeature {

	public static enum TripleType {
		EMPTY, RESOURCE
	}

	protected TripleType tripleType;
	protected String resource;

	public TripleFeature() {
		this.tripleType = TripleType.EMPTY;
	}

	public TripleFeature(String resource) {
		this.tripleType = TripleType.RESOURCE;
		this.resource = resource;
	}

	public TripleType getTripleType() {
		return tripleType;
	}

	public String getResource() {
		return resource;
	}

	/**
	 * Appends regular expression of sub-feature.
	 */
	@Override
	public void appendRegex(StringBuilder stringBuilder) {
		if (tripleType == TripleType.EMPTY) {
			stringBuilder.append(".*");
		} else {
			stringBuilder.append(".*");
			stringBuilder.append(Pattern.quote(resource));
			stringBuilder.append(".*");
		}
	}
}