package org.dice_research.spab.webdemo;

import java.io.IOException;

/**
 * Wrapper to be aware of {@link IOException}, which is allowed in
 * {@link AbstractHandler#handle(com.sun.net.httpserver.HttpExchange)}.
 *
 * @author Adrian Wilke
 */
public class WebserverIoException extends IOException {

	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public WebserverIoException() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	public WebserverIoException(String message) {
		super(message);
	}

	/**
	 * {@inheritDoc}
	 */
	public WebserverIoException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * {@inheritDoc}
	 */
	public WebserverIoException(Throwable cause) {
		super(cause);
	}
}
