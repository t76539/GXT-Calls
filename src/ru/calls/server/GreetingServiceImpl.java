package ru.calls.server;

import ru.calls.client.services.GreetingService;
import ru.calls.shared.FieldVerifier;

import java.util.logging.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements GreetingService {

	Logger logger = Logger.getLogger("MyLogger");

	public String greetServer(String input) throws IllegalArgumentException {
		logger.info("GreetingServiceImpl: greetServer");
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException("Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);
		
		String books = checkBookManager();
		
		return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
				+ userAgent
				+ "<br><b>" + books;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}
	
	private String checkBookManager() {
		BookManager manager = new BookManager();
		manager.setup();
		manager.create();
		String books = manager.displayBooks();
		manager.exit();				
		return books;
	}
}
