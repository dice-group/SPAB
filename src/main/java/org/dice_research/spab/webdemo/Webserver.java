package org.dice_research.spab.webdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.dice_research.spab.SpabApi;
import org.dice_research.spab.exceptions.SpabException;
import org.dice_research.spab.io.FileReader;
import org.dice_research.spab.io.Resources;
import org.dice_research.spab.structures.CandidateVertex;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * SPAB web server.
 * 
 * Dev note: On Eclipse com.sun.net.httpserver problems
 * https://stackoverflow.com/a/25945740
 *
 * @author Adrian Wilke
 */
public class Webserver implements HttpHandler {

	public static final String TITLE = "SPAB web demo";

	/**
	 * Main entry point
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// TODO
		int port = 8080;

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/", new Webserver());
		server.start();

	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		{
			if (exchange.getRequestURI().toString().equals("/")) {
				Map<String, String> parameters = parseQuery(IOUtils.toString(exchange.getRequestBody(), "UTF-8"));

				StringBuilder spab = new StringBuilder();
				if (parameters.containsKey("positives") && parameters.containsKey("negatives")) {
					spab.append("<pre>");

					SpabApi spabApi = new SpabApi();

					try {
						spabApi.setLambda(Float.valueOf(parameters.get("lambda")));
					} catch (NumberFormatException e) {
						internalServerError(exchange, e.getMessage());
						return;
					}
					try {
						spabApi.setMaxIterations(Integer.valueOf(parameters.get("iterations")));
					} catch (NumberFormatException e) {
						internalServerError(exchange, e.getMessage());
						return;
					}
					for (String query : parameters.get("positives")
							.split("(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)*")) {
						spabApi.addPositive(query);
					}
					for (String query : parameters.get("negatives")
							.split("(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)(\\r\\n|\\r|\\n)*")) {
						spabApi.addNegative(query);
					}
					try {
						spabApi.run();
					} catch (SpabException e) {
						String text = "Internal Server Error" + System.lineSeparator() + e.getMessage();
						exchange.getResponseHeaders().add("Content-type", "text/plain");
						exchange.sendResponseHeaders(500, text.length());
						OutputStream os = exchange.getResponseBody();
						os.write(text.getBytes());
						os.close();
					}
					for (CandidateVertex candidateVertex : spabApi.getBestCandidates()) {
						spab.append(candidateVertex.getInfoLine());
						spab.append(System.lineSeparator());

					}

					spab.append("</pre>");
				}

				String html = getResource("templates/html.html");
				String form = getResource("templates/form.html");
				String pos = getResource("data/positive.txt");
				String neg = getResource("data/negative.txt");
				form = form.replaceFirst("<!--POSITIVES-->", pos);
				form = form.replaceFirst("<!--NEGATIVES-->", neg);
				form = form.replaceFirst("<!--LAMBDA-->", "0.1");
				form = form.replaceFirst("<!--ITERATIONNS-->", "100");
				html = html.replaceFirst("<!--TITLE-->", TITLE);
				html = html.replaceFirst("<!--HEAD-->", "");
				html = html.replaceFirst("<!--BODY-->", form + spab.toString());
				
				// Avoid Chrome ERR_BLOCKED_BY_XSS_AUDITOR
				exchange.getResponseHeaders().add("X-XSS-Protection", "0");
				
				exchange.getResponseHeaders().add("Content-type", "text/html");
				exchange.sendResponseHeaders(200, html.length());
				OutputStream os = exchange.getResponseBody();
				os.write(html.getBytes());
				os.close();
			} else {
				String text = "Not Found";
				exchange.getResponseHeaders().add("Content-type", "text/plain");
				exchange.sendResponseHeaders(404, text.length());
				OutputStream os = exchange.getResponseBody();
				os.write(text.getBytes());
				os.close();
			}
		}
	}

	protected void internalServerError(HttpExchange exchange, String message) throws IOException {
		String text = "Internal Server Error" + System.lineSeparator();
		exchange.getResponseHeaders().add("Content-type", "text/plain");
		exchange.sendResponseHeaders(500, message.length() + text.length());
		OutputStream os = exchange.getResponseBody();
		os.write(text.getBytes());
		os.write(message.getBytes());
		os.close();
	}

	/**
	 * Parses query string.
	 * 
	 * @see https://www.codeproject.com/Tips/1040097/Create-simple-http-server-in-Java
	 */
	protected Map<String, String> parseQuery(String query) throws UnsupportedEncodingException {
		Map<String, String> parameters = new HashMap<String, String>();
		if (query != null) {
			String pairs[] = query.split("[&]");
			for (String pair : pairs) {
				String param[] = pair.split("[=]");
				String key = null;
				String value = null;
				if (param.length > 0) {
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
				}
				if (param.length > 1) {
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				}
				parameters.put(key, value);
			}
		}
		return parameters;
	}

	/**
	 * Loads from webdemo resource directory.
	 * 
	 * @throws FileNotFoundException if resource was not found
	 */
	protected String getResource(String resource) throws FileNotFoundException {
		String webdemoResourcePath = "org/dice_research/spab/webdemo/";
		File file;
		try {
			file = Resources.getResource(webdemoResourcePath + resource);
		} catch (NullPointerException e) {
			throw new FileNotFoundException(resource);
		}
		return FileReader.readFileToString(file.getPath(), StandardCharsets.UTF_8);
	}

}