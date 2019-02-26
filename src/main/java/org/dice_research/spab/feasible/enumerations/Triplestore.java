package org.dice_research.spab.feasible.enumerations;

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

	public String getShortHand() {
		if (this.equals(FUSEKI)) {
			return "Fuseki";
		} else if (this.equals(OWLIM_SE)) {
			return "OWLIMS";
		} else if (this.equals(SESAME)) {
			return "Sesame";
		} else if (this.equals(VITUOSO)) {
			return "Virtuo";
		} else {
			return "??????";
		}

	}
}