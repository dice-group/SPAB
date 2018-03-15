package org.dice_research.spab.iguana;

/**
 * Iguana NoQPH.
 * 
 * @author Adrian Wilke
 */
public class QueryMixesPerHour {

	protected IguanaModel model;
	protected String uri;

	public QueryMixesPerHour(IguanaModel model, String uri) {
		this.model = model;
		this.uri = uri;
	}

	@Override
	public String toString() {
		return uri;
	}
}