package org.dice_research.spab.iguana;

/**
 * Iguana QMPH.
 * 
 * @author Adrian Wilke
 */
public class NumberOfQueriesPerHour {

	protected IguanaModel model;
	protected String uri;

	public NumberOfQueriesPerHour(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	@Override
	public String toString() {
		return uri;
	}
}