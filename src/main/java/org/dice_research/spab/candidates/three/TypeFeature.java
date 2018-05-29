package org.dice_research.spab.candidates.three;

/**
 * TYPE part of SPARQL. E.g. SELECT or CREATE.
 * 
 * @author Adrian Wilke
 */
public class TypeFeature extends SubFeature {

	/**
	 * SPARQL unit: Query
	 */
	public static final String[] QUERIES = { "SELECT", "CONSTRUCT", "DESCRIBE", "ASK" };

	/**
	 * SPARQL unit: Update
	 */
	public static final String[] UPDATES = { "LOAD", "CLEAR", "DROP", "ADD", "MOVE", "COPY", "CREATE", "INSERT DATA",
			"DELETE DATA", "DELETE WHERE" };

	/**
	 * Returns types of SPARQL queries and SPARQL updates.
	 */
	public static String[] getAllTypes() {
		String[] types = new String[QUERIES.length + UPDATES.length];
		System.arraycopy(QUERIES, 0, types, 0, QUERIES.length);
		System.arraycopy(UPDATES, 0, types, QUERIES.length, UPDATES.length);
		return types;
	}

	protected String type;

	public TypeFeature(String type) {
		this.type = type;
	}

	@Override
	public void appendRegex(StringBuilder stringBuilder) {
		stringBuilder.append(type);
	}
}