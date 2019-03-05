package org.dice_research.spab.candidates.six;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.sparql.sse.Tags;

/**
 * BuiltInCall ::= Aggregate | 'STR' '(' Expression ')' | 'LANG' '(' Expression
 * ')' | 'LANGMATCHES' '(' Expression ',' Expression ')' | 'DATATYPE' '('
 * Expression ')' | 'BOUND' '(' Var ')' | 'IRI' '(' Expression ')' | 'URI' '('
 * Expression ')' | 'BNODE' ( '(' Expression ')' | NIL ) | 'RAND' NIL | 'ABS'
 * '(' Expression ')' | 'CEIL' '(' Expression ')' | 'FLOOR' '(' Expression ')' |
 * 'ROUND' '(' Expression ')' | 'CONCAT' ExpressionList | SubstringExpression |
 * 'STRLEN' '(' Expression ')' | StrReplaceExpression | 'UCASE' '(' Expression
 * ')' | 'LCASE' '(' Expression ')' | 'ENCODE_FOR_URI' '(' Expression ')' |
 * 'CONTAINS' '(' Expression ',' Expression ')' | 'STRSTARTS' '(' Expression ','
 * Expression ')' | 'STRENDS' '(' Expression ',' Expression ')' | 'STRBEFORE'
 * '(' Expression ',' Expression ')' | 'STRAFTER' '(' Expression ',' Expression
 * ')' | 'YEAR' '(' Expression ')' | 'MONTH' '(' Expression ')' | 'DAY' '('
 * Expression ')' | 'HOURS' '(' Expression ')' | 'MINUTES' '(' Expression ')' |
 * 'SECONDS' '(' Expression ')' | 'TIMEZONE' '(' Expression ')' | 'TZ' '('
 * Expression ')' | 'NOW' NIL | 'UUID' NIL | 'STRUUID' NIL | 'MD5' '('
 * Expression ')' | 'SHA1' '(' Expression ')' | 'SHA256' '(' Expression ')' |
 * 'SHA384' '(' Expression ')' | 'SHA512' '(' Expression ')' | 'COALESCE'
 * ExpressionList | 'IF' '(' Expression ',' Expression ',' Expression ')' |
 * 'STRLANG' '(' Expression ',' Expression ')' | 'STRDT' '(' Expression ','
 * Expression ')' | 'sameTerm' '(' Expression ',' Expression ')' | 'isIRI' '('
 * Expression ')' | 'isURI' '(' Expression ')' | 'isBLANK' '(' Expression ')' |
 * 'isLITERAL' '(' Expression ')' | 'isNUMERIC' '(' Expression ')' |
 * RegexExpression | ExistsFunc | NotExistsFunc
 * 
 * Generated by {@link Constraint}.
 * 
 * Spelling based on {@link Tags}
 * 
 * @see https://www.w3.org/TR/sparql11-query/#rBuiltInCall
 * 
 * @author Adrian Wilke
 */
public class BuiltInCall extends Expression {

	protected final static String[] CALLS_SINGLE = { Tags.tagStr, Tags.tagLang, Tags.tagDatatype, Tags.tagIri,
			Tags.tagUri, Tags.tagNumAbs, Tags.tagNumCeiling, Tags.tagNumFloor, Tags.tagNumRound, Tags.tagStrlen,
			Tags.tagStrUppercase, Tags.tagStrLowercase, Tags.tagStrEncodeForURI, Tags.tagYear, Tags.tagMonth,
			Tags.tagDay, Tags.tagHours, Tags.tagMinutes, Tags.tagSeconds, Tags.tagTimezone, Tags.tagTZ, Tags.tagMD5,
			Tags.tagSHA1, Tags.tagSHA256, Tags.tagSHA384, Tags.tagSHA512, Tags.tagIsIRI, Tags.tagIsURI, Tags.tagIsBlank,
			Tags.tagIsLiteral, Tags.tagIsNumeric };

	protected final static String[] CALLS_DOUBLE = { Tags.tagLangMatches, Tags.tagStrContains, Tags.tagStrStarts,
			Tags.tagStrEnds, Tags.tagStrBefore, Tags.tagStrAfter, Tags.tagLang, Tags.tagStrDatatype, Tags.tagSameTerm

	};

	protected final static String[] CALLS_TRIPLE = { Tags.tagIf };

	protected final static String[] CALLS_VAR = { Tags.tagBound };

	protected final static String[] CALLS_SINGLE_NIL = { Tags.tagBNode };

	protected final static String[] CALLS_NIL = { Tags.tagRand, Tags.tagNow, Tags.tagUUID, Tags.tagStrUUID };

	protected final static String[] CALLS_EXPRESSIONLIST = { Tags.tagConcat, Tags.tagCoalesce };

	protected final static String[] CALLS_EXPRESSIONS = { Tags.tagSubstr, Tags.tagReplace, Tags.tagRegex };

	protected final static String[] CALLS_FUNC = { Tags.tagExists, Tags.tagNotExists };

	/**
	 * Creates list of call with reserved words.
	 */
	public static List<Expression> getInitialInstances() {
		List<Expression> instances = new LinkedList<Expression>();
		for (String call : CALLS_SINGLE) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_DOUBLE) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_TRIPLE) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_VAR) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_SINGLE_NIL) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_NIL) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_EXPRESSIONLIST) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_EXPRESSIONS) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		for (String call : CALLS_FUNC) {
			BuiltInCall builtInCall = new BuiltInCall();
			builtInCall.sequence.add(new ExpressionString(call));
			instances.add(builtInCall);
		}
		return instances;
	}

	public BuiltInCall() {
		super();
	}

	public BuiltInCall(Expression origin) {
		super(origin);
	}

	@Override
	protected Expression createInstance(Expression origin) {
		return new BuiltInCall(origin);
	}

	@Override
	protected void addRegex(StringBuilder stringBuilder) {
		addSequenceToRegex(stringBuilder);
		addWildcard(stringBuilder);
	}
}