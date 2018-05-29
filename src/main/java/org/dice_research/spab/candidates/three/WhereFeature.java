package org.dice_research.spab.candidates.three;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.candidates.three.TripleFeature.TripleType;
import org.dice_research.spab.input.Input;

/**
 * WHERE part of SPARQL.
 * 
 * @author Adrian Wilke
 */
public class WhereFeature extends SubFeature {

	protected List<TripleFeature> triples = new LinkedList<TripleFeature>();

	/**
	 * Create new empty WHERE feature.
	 */
	public WhereFeature() {
	}

	/**
	 * Create new WHERE feature.
	 */
	public WhereFeature(boolean addEmptyTriple) {
		if (addEmptyTriple) {
			triples.add(new TripleFeature());
		}
	}

	/**
	 * Creates new WHERE feature based on the provided one. All triples will be
	 * exchanged
	 */
	public WhereFeature(WhereFeature whereFeature) {
		this.triples = new LinkedList<TripleFeature>(whereFeature.triples);
	}

	/**
	 * Generates a list of sub-features of the current WHERE feature.
	 */
	public List<WhereFeature> generateSubFeatures(Input input) {
		List<WhereFeature> whereSubFeatures = new LinkedList<WhereFeature>();
		boolean allTriplesEmpty = true;

		// Refine every triple
		for (int t = 0; t < triples.size(); t++) {
			TripleFeature triple = triples.get(t);

			if (triple.getTripleType() == TripleType.EMPTY) {
				// Empty triples have to be replaced by resources

				for (String resource : input.getResources()) {
					whereSubFeatures.add(createNewWhereFeatureExchangeTriple(new TripleFeature(resource), t));
				}

			} else {
				allTriplesEmpty = false;

			}
		}

		// All triples empty: Add another empty triple
		if (allTriplesEmpty) {
			whereSubFeatures.add(createNewWhereFeatureAddTriple(new TripleFeature(), true));
		}

		return whereSubFeatures;
	}

	/**
	 * Appends regular expression of sub-feature.
	 */
	@Override
	public void appendRegex(StringBuilder stringBuilder) {
		stringBuilder.append(".*WHERE.*");
		stringBuilder.append("\\{");

		// Add triples in WHERE

		for (int i = 0; i < triples.size(); i++) {

			// Triple divider
			if (i != 0) {
				stringBuilder.append("\\ \\.\\ ");
				// whereBuilder.append(".*");
			}

			// Triple regex representation
			triples.get(i).appendRegex(stringBuilder);
		}

		stringBuilder.append("\\}");
		stringBuilder.append(".*");
	}

	/**
	 * Creates new WHERE feature and adds triple.
	 * 
	 * @param tripleFeature
	 *            Triple to add
	 * @param addCurrentTriples
	 *            If true, all triples of this object are added
	 * @return New WHERE feature
	 */
	protected WhereFeature createNewWhereFeatureAddTriple(TripleFeature tripleFeature, boolean addCurrentTriples) {
		WhereFeature newWhereFeature = addCurrentTriples ? new WhereFeature(this) : new WhereFeature();
		newWhereFeature.triples.add(tripleFeature);
		return newWhereFeature;
	}

	/**
	 * Creates new WHERE feature and adds all triples of this object. The triple at
	 * index will be exchanged.
	 */
	protected WhereFeature createNewWhereFeatureExchangeTriple(TripleFeature tripleFeature, int index) {
		WhereFeature newWhereFeature = new WhereFeature(this);
		newWhereFeature.triples.set(index, tripleFeature);
		return newWhereFeature;
	}
}