package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.dice_research.spab.io.Resources;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Processes HTTP exchanges. Behavior is specified by implementing
 * {@link #handle}.
 *
 * @author Adrian Wilke
 */
public abstract class AbstractHandler implements HttpHandler {

	public static final String TITLE = "SPAB web demo";

	public static final String CONTENT_TYPE_HTML = "text/html";
	public static final String CONTENT_TYPE_TEXT = "text/plain";

	public static final int STATUS_OK = 200;
	public static final int STATUS_NOT_FOUND = 404;
	public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

	private com.sun.net.httpserver.HttpExchange exchange;

	private String contentType;
	private String data;
	private Integer statusCode;

	private Map<String, String> responseHeaders = new HashMap<String, String>();

	/**
	 * Handle the given request and generate an appropriate response. See
	 * {@link HttpExchange} for a description of the steps involved in handling an
	 * exchange.
	 * 
	 * @see #handle(HttpExchange)
	 */
	abstract public void handle() throws WebserverIoException;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handle(HttpExchange exchange) throws IOException {

		// Exchange object used by various methods
		this.exchange = exchange;

		// Main behavior. Configuration variables are set.
		// WebserverIoException will be thrown as IOException.
		// WebserverIoException is no default Java exception, developers are aware of
		// throwing it.
		handle();

		// Content type (Usually HTML)
		if (contentType == null) {
			// Unknown. Plain text will be handled by browsers as HTML.
			exchange.getResponseHeaders().add("Content-type", CONTENT_TYPE_HTML);
		} else {
			exchange.getResponseHeaders().add("Content-type", contentType);
		}

		// Add all additional headers
		for (Entry<String, String> responseHeader : responseHeaders.entrySet()) {
			exchange.getResponseHeaders().add(responseHeader.getKey(), responseHeader.getValue());
		}

		// Set response code for use afterwards
		int responseCode;
		if (this.statusCode == null) {
			// No error set. Assume everything is okay.
			responseCode = STATUS_OK;
		} else {
			responseCode = this.statusCode;
		}

		// Send data
		// WebserverIoException will be thrown as IOException.
		if (data == null) {
			// sendResponseHeaders(...): If rCode <= -1, then no response body length is
			// specified and no response body may be written.
			exchange.sendResponseHeaders(responseCode, -1);
		} else {
			exchange.sendResponseHeaders(responseCode, data.length());
			OutputStream outputStream = exchange.getResponseBody();
			outputStream.write(data.getBytes());
			outputStream.close();
		}
	}

	/**
	 * Gets the exchange containing the request from the client and used to send the
	 * response.
	 */
	protected HttpExchange getHttpExchange() {
		return exchange;
	}

	/**
	 * Loads from webdemo resource directory.
	 * 
	 * @throws IOException
	 *             If resource could not be read.
	 */
	protected String getResource(String resource) throws IOException {
		String webdemoResourcePath = "org/dice_research/spab/webdemo/";
		return Resources.getResourceAsString(webdemoResourcePath + resource);
	}

	/**
	 * Parses query string.
	 * 
	 * @param parameters
	 *            map has to be constructed before, as it the needed HTTP request
	 *            body can only be read once.
	 * 
	 * @see https://www.codeproject.com/Tips/1040097/Create-simple-http-server-in-Java
	 * 
	 * @throws WebserverIoException
	 *             on errors reading request body or decoding parameters.
	 */
	protected void fillParameters(Map<String, String> parameters) throws WebserverIoException {
		String pairs[];
		try {
			pairs = IOUtils.toString(getHttpExchange().getRequestBody(), "UTF-8").split("[&]");
		} catch (IOException e) {
			throw new WebserverIoException("Can not read request body.", e);
		}
		for (String pair : pairs) {
			String param[] = pair.split("[=]");
			String key = null;
			String value = null;
			try {
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}
				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}
			} catch (UnsupportedEncodingException e) {
				throw new WebserverIoException("Can not decode parameters.", e);
			}
			parameters.put(key, value);
		}
	}

	/**
	 * Prepares form template
	 */
	String getForm(boolean isStatic, Map<String, String> parameters) throws IOException {
		String form = new String();
		form = getResource(Templates.FORM);
		if (isStatic) {
			String pos = getResource("data/positive.txt");
			String neg = getResource("data/negative.txt");
			form = form.replaceFirst(Templates.FORM_MARKER_POSITIVES, pos);
			form = form.replaceFirst(Templates.FORM_MARKER_NEGATIVES, neg);
			form = form.replaceFirst(Templates.FORM_MARKER_LAMBDA, "0.1");
			form = form.replaceFirst(Templates.FORM_MARKER_ITERATIONNS, "100");
		} else {
			String parameter = parameters.get(Templates.FORM_ID_POSITIVES);
			form = form.replaceFirst(Templates.FORM_MARKER_POSITIVES, parameter == null ? "" : parameter);

			parameter = parameters.get(Templates.FORM_ID_NEGATIVES);
			form = form.replaceFirst(Templates.FORM_MARKER_NEGATIVES, parameter == null ? "" : parameter);

			parameter = parameters.get(Templates.FORM_ID_LAMBDA);
			form = form.replaceFirst(Templates.FORM_MARKER_LAMBDA, parameter == null ? "" : parameter);

			parameter = parameters.get(Templates.FORM_ID_ITERATIONNS);
			form = form.replaceFirst(Templates.FORM_MARKER_ITERATIONNS, parameter == null ? "" : parameter);
		}

		return form;
	}

	/**
	 * Sets HTTP 500.
	 * 
	 * @param text
	 *            Error information
	 */
	protected void setInternalServerError(String text) {
		statusCode = STATUS_INTERNAL_SERVER_ERROR;
		contentType = CONTENT_TYPE_TEXT;
		data = "Internal Server Error: " + text;
	}

	/**
	 * Sets HTTP 404
	 * 
	 * @param html
	 *            HTML to send
	 */
	protected void setNotFound(String text) {
		statusCode = STATUS_NOT_FOUND;
		contentType = CONTENT_TYPE_TEXT;
		data = text;
	}

	/**
	 * Sets HTTP 200
	 * 
	 * @param html
	 *            HTML to send
	 */
	protected void setOk(String html) {
		statusCode = STATUS_OK;
		contentType = CONTENT_TYPE_HTML;
		data = html;

		// Avoid Chrome ERR_BLOCKED_BY_XSS_AUDITOR
		responseHeaders.put("X-XSS-Protection", "0");
	}

	protected void setOkWithBody(String body) {
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append("<h1>SPAB: SPARQL Benchmark Query Generalization</h1>");
		bodyBuilder.append(System.lineSeparator());
		bodyBuilder.append(body);
		bodyBuilder.append(System.lineSeparator());
		bodyBuilder.append("<p class=\"footer\">");
		bodyBuilder.append("<a target=\"_blank\" href=\"https://github.com/dice-group/SPAB\">SPAB on GitHub</a>");
		bodyBuilder.append(" - ");
		bodyBuilder.append(
				"<a target=\"_blank\" href=\"https://dice.cs.uni-paderborn.de/about/\">Data Science Group (DICE) at Paderborn University</a>");
		bodyBuilder.append("</p>");
		try {
			setOk(getResource(Templates.HTML)

					.replaceFirst(Templates.HTML_MARKER_TITLE, TITLE)

					.replaceFirst(Templates.HTML_MARKER_HEAD, "")

					.replaceFirst(Templates.HTML_MARKER_BODY, bodyBuilder.toString()));
		} catch (Exception e) {
			setInternalServerError(e.getMessage());
			return;
		}
	}

}