package org.dice_research.spab.feasible.enumerations;

public enum Dataset {
	DBPEDIA, SWDF;

	public String getShortHand() {
		if (this.equals(DBPEDIA)) {
			return "DBPE";
		} else if (this.equals(SWDF)) {
			return "SWDF";
		} else {
			return "????";
		}
	}
}