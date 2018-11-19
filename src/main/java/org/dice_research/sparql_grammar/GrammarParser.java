package org.dice_research.sparql_grammar;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * @see https://github.com/dice-group/SPAB/wiki/Parsers
 *
 * @author Adrian Wilke
 */
public class GrammarParser {

	public static void main(String[] args) throws IOException, URISyntaxException {

		InputStream resourceStream = GrammarParser.class.getClassLoader().getResourceAsStream("sparql_1.1_grammar.txt");

		// Get lines
		List<String> lines = IOUtils.readLines(resourceStream, Charset.defaultCharset());
		System.out.println("Number of lines: " + lines.size());

		// Lines to symbols
		List<Rule> rules = new LinkedList<Rule>();
		List<String> ruleLines = new LinkedList<String>();
		for (String line : lines) {
			if (line.startsWith("[")) {
				if (!ruleLines.isEmpty()) {
					rules.add(new Rule(ruleLines));
					ruleLines = new LinkedList<String>();
				}
			}
			ruleLines.add(line);
		}
		if (!ruleLines.isEmpty()) {
			rules.add(new Rule(ruleLines));
		}
		System.out.println("Number of symbols: " + rules.size());

		// Parse symbols
		List<String> symbols = new LinkedList<String>();
		for (Rule rule : rules) {
			rule.parse();
			symbols.add(rule.symbol);
		}

		System.out.println(symbols);

		for (Rule rule : rules) {
			System.out.println();
			System.out.print(rule.symbol);
			System.out.print(" ::= ");
			for (String expLine : rule.getExpressionLines()) {
				System.out.println(expLine);
			}
		}
	}
}