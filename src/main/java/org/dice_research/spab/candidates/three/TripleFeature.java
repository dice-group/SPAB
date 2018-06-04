package org.dice_research.spab.candidates.three;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic representation of a triple.
 * 
 * @author Adrian Wilke
 */
public class TripleFeature extends SubFeature {

	private static final Logger LOGGER = LoggerFactory.getLogger(TripleFeature.class);

	/**
	 * EMPTY: Only empty placeholder.
	 * 
	 * GENERIC: One resource inside triple.
	 * 
	 * FULL: S, p, o can contain resources.
	 */
	public static enum TripleType {
		EMPTY, GENERIC, FULL
	}

	protected TripleType tripleType;
	protected String resource;
	protected String subject;
	protected String predicate;
	protected String object;

	/**
	 * Creates new FULL triple type.
	 * 
	 * @param tripleFeature
	 *            Another FULL triple type
	 * 
	 * @throws RuntimeException
	 *             if triple type is not FULL
	 */
	TripleFeature(TripleFeature tripleFeature) {
		if (tripleFeature.getTripleType() == TripleType.FULL) {
			this.tripleType = TripleType.FULL;
			this.subject = tripleFeature.subject;
			this.predicate = tripleFeature.predicate;
			this.object = tripleFeature.object;
		} else {
			throw new RuntimeException("Trying to create triple feature with incorrect type.");
		}
	}

	/**
	 * Empty triple.
	 */
	public TripleFeature() {
		this.tripleType = TripleType.EMPTY;
	}

	/**
	 * Generic triple.
	 */
	public TripleFeature(String resource) {
		this.tripleType = TripleType.GENERIC;
		this.resource = resource;
	}

	/**
	 * Full triple.
	 */
	public TripleFeature(String subject, String predicate, String object) {
		this.tripleType = TripleType.FULL;
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/**
	 * Returns triple type.
	 */
	public TripleType getTripleType() {
		return tripleType;
	}

	/**
	 * Used in GENERIC type.
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Used in FULL type.
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * Used in FULL type.
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * Used in FULL type.
	 */
	public String getObject() {
		return object;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public void setObject(String object) {
		this.object = object;
	}

	/**
	 * Appends regular expression of sub-feature.
	 */
	@Override
	public void appendRegex(StringBuilder stringBuilder) {
		if (tripleType == TripleType.EMPTY) {
			stringBuilder.append(".*");

		} else if (tripleType == TripleType.GENERIC) {
			stringBuilder.append(".*");
			stringBuilder.append(Pattern.quote(resource));
			stringBuilder.append(".*");

		} else if (tripleType == TripleType.FULL) {
			if (subject != null) {
				stringBuilder.append(Pattern.quote(subject));
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (predicate != null) {
				stringBuilder.append(Pattern.quote(predicate));
			} else {
				stringBuilder.append(".*");
			}
			stringBuilder.append(" ");
			if (object != null) {
				stringBuilder.append(Pattern.quote(object));
			} else {
				stringBuilder.append(".*");
			}

		} else {
			LOGGER.error("Unknown triple type");
		}
	}
}