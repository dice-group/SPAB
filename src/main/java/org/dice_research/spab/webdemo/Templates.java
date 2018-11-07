package org.dice_research.spab.webdemo;

/**
 * Constants for processing templates.
 *
 * @author Adrian Wilke
 */
public abstract class Templates {

	public final static String HTML = "templates/html.html";
	public final static String HTML_MARKER_TITLE = "<!--TITLE-->";
	public final static String HTML_MARKER_HEAD = "<!--HEAD-->";
	public final static String HTML_MARKER_BODY = "<!--BODY-->";

	public final static String FORM = "templates/form.html";
	public final static String FORM_MARKER_POSITIVES = "<!--POSITIVES-->";
	public final static String FORM_MARKER_NEGATIVES = "<!--NEGATIVES-->";
	public final static String FORM_MARKER_LAMBDA = "<!--LAMBDA-->";
	public final static String FORM_MARKER_ITERATIONNS = "<!--ITERATIONNS-->";
	public final static String FORM_ID_POSITIVES = "positives";
	public final static String FORM_ID_NEGATIVES = "negatives";
	public final static String FORM_ID_LAMBDA = "lambda";
	public final static String FORM_ID_ITERATIONNS = "iterations";

	public final static String GRAPH = "templates/graph.js";
	public final static String GRAPH_MARKER_ELEMENTS = "// ELEMENTS";
	public final static String GRAPH_MARKER_MIN = "/\\*MIN\\*/";
	public final static String GRAPH_MARKER_MAX = "/\\*MAX\\*/";
}