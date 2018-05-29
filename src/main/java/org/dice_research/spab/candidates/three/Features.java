package org.dice_research.spab.candidates.three;

import java.util.LinkedList;
import java.util.List;

import org.dice_research.spab.input.Input;

/**
 * Features (parts of SPARQL query) of a {@link SpabThreeCandidate}.
 * 
 * @author Adrian Wilke
 */
public class Features {

	protected TypeFeature typeFeature;
	protected WhereFeature whereFeature;

	/**
	 * Creates new, empty features.
	 */
	public Features() {
	}

	/**
	 * Creates new Features based on the provided one.
	 */
	public Features(Features features) {
		this.typeFeature = features.typeFeature;
		this.whereFeature = features.whereFeature;
	}

	/**
	 * Returns regular expression representing features.
	 */
	public String getRegex() {
		StringBuilder stringBuilder = new StringBuilder();

		// TYPE

		if (typeFeature != null) {
			typeFeature.appendRegex(stringBuilder);
			stringBuilder.append(".*");
		}

		// WHERE

		if (whereFeature != null) {
			whereFeature.appendRegex(stringBuilder);
		}

		// Build and return

		if (stringBuilder.length() > 0) {
			// Build regular expression
			return stringBuilder.toString();

		} else {
			// No features specified. Return most generic regular expression, matching
			// everything.
			return ".*";
		}
	}

	/**
	 * Generates a list of sub-features for children of current candidate.
	 */
	public List<Features> generateSubFeatures(Input input) {
		List<Features> subFeatures = new LinkedList<Features>();

		// TYPE

		if (typeFeature == null) {
			// If no type was specified yet, add all types
			for (String newType : TypeFeature.getAllTypes()) {
				Features newFeatures = new Features(this);
				newFeatures.typeFeature = new TypeFeature(newType);
				subFeatures.add(newFeatures);
			}
		}

		// WHERE

		if (whereFeature == null) {
			// No where specified yet, add empty where feature
			Features newFeatures = new Features(this);
			newFeatures.whereFeature = new WhereFeature(true);
			subFeatures.add(newFeatures);
		} else {
			// Add refinements of existing where features
			for (WhereFeature newWhereFeature : whereFeature.generateSubFeatures(input)) {
				Features newFeatures = new Features(this);
				newFeatures.whereFeature = newWhereFeature;
				subFeatures.add(newFeatures);
			}
		}

		return subFeatures;
	}
}