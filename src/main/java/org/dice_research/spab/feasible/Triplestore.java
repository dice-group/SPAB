package org.dice_research.spab.feasible;

public enum Triplestore {

	FUSEKI, OWLIM_SE, SESAME, VITUOSO;

	public String getCsvHeader() {
		if (this.equals(FUSEKI)) {
			return "Fuseki";
		} else if (this.equals(OWLIM_SE)) {
			return "OWLIM-SE";
		} else if (this.equals(SESAME)) {
			return "Sesame";
		} else if (this.equals(VITUOSO)) {
			return "Virtuoso";
		} else {
			throw new NullPointerException("Unknown triple store.");
		}
	}
}