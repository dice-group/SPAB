package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

/**
 * SPAB web server.
 * 
 * Dev note: On Eclipse com.sun.net.httpserver problems
 * https://stackoverflow.com/a/25945740
 *
 * @author Adrian Wilke
 */
public class Webserver extends AbstractHandler {

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
		server.createContext("/spab", new WebHandler());
		server.createContext("/", new Webserver());
		server.start();

		System.out.println(port);

	}

	@Override
	public void handle() throws WebserverIoException {
		String form = new String();
		try {
			form = getResource("templates/form.html");
			String pos = getResource("data/positive.txt");
			String neg = getResource("data/negative.txt");
			form = form.replaceFirst("<!--POSITIVES-->", pos);
			form = form.replaceFirst("<!--NEGATIVES-->", neg);
			form = form.replaceFirst("<!--LAMBDA-->", "0.1");
			form = form.replaceFirst("<!--ITERATIONNS-->", "100");
		} catch (IOException e) {
			setInternalServerError("Error " + e.getMessage());
			return;
		}
		setOkWithBody(form);
	}

}