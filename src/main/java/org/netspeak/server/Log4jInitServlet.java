package org.netspeak.server;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.xml.DOMConfigurator;

public class Log4jInitServlet extends HttpServlet {

	private static final long serialVersionUID = -143184474978596117L;

	// https://logging.apache.org/log4j/1.2/manual.html

	@Override
	public void init() {
		String prefix = getServletContext().getRealPath("/");
		String file = getInitParameter("log4j-init-file");

		if (file != null) {
			DOMConfigurator.configure(prefix + file);
		}
	}

}
