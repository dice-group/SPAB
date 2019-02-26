package org.dice_research.spab.feasible.enumerations;

public enum QueryType {
	ASK, CONSTRUCT, DESCRIBE, SELECT, MIX;

	public String getShortHand() {
		if (this.equals(ASK)) {
			return "ASK";
		} else if (this.equals(CONSTRUCT)) {
			return "CON";
		} else if (this.equals(DESCRIBE)) {
			return "DES";
		} else if (this.equals(SELECT)) {
			return "SEL";
		} else if (this.equals(MIX)) {
			return "MIX";
		} else {
			return "???";
		}
	}
}
