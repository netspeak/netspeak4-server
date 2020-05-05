package org.netspeak.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/corpus")
public class CorpusServlet extends HttpServlet {

	private static final long serialVersionUID = 7972760584717251159L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setStatus(200);

		String callback = request.getParameter("callback");

		final PrintWriter writer = response.getWriter();
		if (callback != null) {
			response.setContentType("application/javascript");
			writer.print(callback);
			writer.print("(");
			printCorporaJSON(writer);
			writer.print(");");
		} else {
			response.setContentType("application/json");
			printCorporaJSON(writer);
		}
	}

	private static void printCorporaJSON(PrintWriter writer) {
		// TODO: Proper JSON output

		writer.printf("{\"default\":\"%s\",\"corpora\":[", CorpusRegistry.getDefaultKey());

		boolean first = true;
		for (Entry<String, Corpus> entry : CorpusRegistry.entrySet()) {
			final String key = entry.getKey();
			final Corpus corpus = entry.getValue();

			if (!first)
				writer.print(',');
			else
				first = false;

			writer.printf("{\"key\":\"%s\",\"name\":\"%s\",\"language\":\"%s\"}", key, corpus.getName(),
			        corpus.getLanguage());
		}
		writer.printf("]}");
	}

}
