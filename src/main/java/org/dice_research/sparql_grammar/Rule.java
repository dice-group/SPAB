package org.dice_research.sparql_grammar;

import java.util.LinkedList;
import java.util.List;

public class Rule {

	List<String> lines;

	int number;
	String symbol;
	String firstExpressionLine;

	public Rule(List<String> lines) {
		this.lines = lines;
	}

	public void parse() {
		String line = lines.get(0);
		int beginIndex = line.indexOf('[');
		int endIndex = line.indexOf(']');
		number = Integer.parseInt(line.substring(beginIndex + 1, endIndex));

		beginIndex = endIndex + 1;
		endIndex = beginIndex + line.substring(beginIndex).indexOf("::=");
		symbol = line.substring(beginIndex + 1, endIndex).trim();

		beginIndex = endIndex + 3;
		firstExpressionLine = line.substring(beginIndex).trim();
	}

	List<String> getExpressionLines() {
		List<String> lines = new LinkedList<String>();
		lines.add(firstExpressionLine);
		for (int i = 1; i < this.lines.size(); i++) {
			lines.add(this.lines.get(i));
		}
		return lines;
	}

}
