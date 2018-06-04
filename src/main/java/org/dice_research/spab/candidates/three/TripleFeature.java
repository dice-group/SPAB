package org.dice_research.spab.candidates.three;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic representation of a triple. It can be empty (just a placeholder) or
 * contain a resource.
 * 
 * @author Adrian Wilke
 */
public class TripleFeature extends SubFeature {

	private static final Logger LOGGER = LoggerFactory.getLogger(TripleFeature.class);

	public static enum TripleType {
		EMPTY, RESOURCE, FULL
	}

	protected TripleType tripleType;
	protected String resource;
	protected String s;
	protected String p;
	protected String o;

	public TripleFeature() {
		this.tripleType = TripleType.EMPTY;
	}

	public TripleFeature(String resource) {
		this.tripleType = TripleType.RESOURCE;
		this.resource = resource;
	}

	public TripleFeature(String s, String p, String o) {
		this.tripleType = TripleType.FULL;
		this.s = s;
		this.p = p;
		this.o = o;
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

		} else if (tripleType == TripleType.RESOURCE) {
			stringBuilder.append(".*");
			stringBuilder.append(Pattern.quote(resource));
			stringBuilder.append(".*");

		} else if (tripleType == TripleType.FULL) {
			if (s != null) {
				stringBuilder.append(".*");
				stringBuilder.append(Pattern.quote(s));
				stringBuilder.append(".*");
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (p != null) {
				stringBuilder.append(".*");
				stringBuilder.append(Pattern.quote(p));
				stringBuilder.append(".*");
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (o != null) {
				stringBuilder.append(".*");
				stringBuilder.append(Pattern.quote(o));
				stringBuilder.append(".*");
			} else {
				stringBuilder.append(".*");
			}

		} else {
			LOGGER.error("Unknown triple type");
		}
	}
}