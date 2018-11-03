package org.dice_research.spab.webdemo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

/**
 * SPAB web server.
 * 
 * Dev note: On Eclipse com.sun.net.httpserver problems set project libraries
 * accessible: com/sun/net/httpserver/** https://stackoverflow.com/a/25945740
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

		if (args.length == 0) {
			System.err.println("Please provide a port for the webserver");
			System.exit(1);
		}

		int port = Integer.parseInt(args[0]);

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/spab", new WebHandler());
		server.createContext("/", new Webserver());
		server.start();

		System.out.println("Webserver startet at port " + port);
	}

	@Override
	public void handle() throws WebserverIoException {
		String form = new String();
		try {
			Map<String, String> parameters = new HashMap<String, String>();
			fillParameters(parameters);
			form = getForm(true, parameters);
		} catch (IOException e) {
			setInternalServerError("Error " + e.getMessage());
			return;
		}
		setOkWithBody(form);
	}

}