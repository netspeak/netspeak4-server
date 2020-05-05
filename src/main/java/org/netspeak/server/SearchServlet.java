package org.netspeak.server;

import static org.netspeak.server.InitParameters.CORPUS_LANGUAGE;
import static org.netspeak.server.InitParameters.CORPUS_NAME;
import static org.netspeak.server.InitParameters.CORPUS_REGISTRY_KEY;
import static org.netspeak.server.InitParameters.MAX_PHRASE_COUNT;
import static org.netspeak.server.InitParameters.PHRASE_LENGTH_MAX;
import static org.netspeak.server.InitParameters.PHRASE_LENGTH_MIN;
import static org.netspeak.server.InitParameters.PRUNING_HIGH;
import static org.netspeak.server.InitParameters.PRUNING_LOW;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.netspeak.Configuration;
import org.netspeak.ErrorCode;
import org.netspeak.JsonUtil;
import org.netspeak.Netspeak;
import org.netspeak.NetspeakUtil;
import org.netspeak.generated.NetspeakMessages.Phrase;
import org.netspeak.generated.NetspeakMessages.RawResponse;
import org.netspeak.generated.NetspeakMessages.Request;
import org.netspeak.generated.NetspeakMessages.Response;

/**
 * This is the Netspeak <code>search</code> servlet, that contains the Netspeak
 * application logic. It receives requests by url GET-parameters and serves
 * serialized response objects as JSON or plain text.
 */
public class SearchServlet extends HttpServlet {

	private static final long serialVersionUID = -5051717748142095908L;

	private Logger apiLogger = Logger.getLogger(SearchServlet.class.getName() + ".api");
	private Logger webLogger = Logger.getLogger(SearchServlet.class.getName() + ".web");
	private Logger errLogger = Logger.getLogger(SearchServlet.class.getName() + ".err");

	private ServletConfig config;
	private RequestLimits limits;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchServlet() {
		super();
	}

	/**
	 * Reads the servlet's {@link ServletConfig} that contains the parameters
	 * defined in the servlet descriptor (web.xml).
	 *
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			// Read request limits from web.xml.
			limits = new RequestLimits();

			limits.setMaxPhraseCount(parseInitRequestLimit(config, MAX_PHRASE_COUNT));

			limits.setPhraseLengthMax(parseInitRequestLimit(config, PHRASE_LENGTH_MAX));
			limits.setPhraseLengthMin(parseInitRequestLimit(config, PHRASE_LENGTH_MIN));

			limits.setPruningHigh(parseInitRequestLimit(config, PRUNING_HIGH));
			limits.setPruningLow(parseInitRequestLimit(config, PRUNING_LOW));

			CorpusRegistry.setDefaultKey(config.getInitParameter(InitParameters.CORPUS_DEFAULT_KEY));

			// Read Netspeak config from web.xml and start service.
			Configuration[] netspeakConfigs = loadConfigurations(config);

			for (Configuration netspeakConfig : netspeakConfigs) {
				Netspeak service = new Netspeak(netspeakConfig);

				String key = getRequiredProperty(netspeakConfig, CORPUS_REGISTRY_KEY);
				String name = getRequiredProperty(netspeakConfig, CORPUS_NAME);
				String language = getRequiredProperty(netspeakConfig, CORPUS_LANGUAGE);

				if (CorpusRegistry.get(key) != null) {
					throw new RuntimeException("The corpus '" + CorpusRegistry.get(key).getName()
							+ "' is already registered under the key " + key);
				}

				CorpusRegistry.register(key, new Corpus(service, name, language));
			}

			// workaround
			this.config = config;
		} catch (Exception e) {
			errLogger.info("Exception during servlet initialization", e);
			throw new ServletException(e);
		}
	}

	private static int parseInitRequestLimit(ServletConfig config, String name) throws Exception {
		String ip = config.getInitParameter(name);
		if (ip == null) {
			String values = "";
			Enumeration<String> e = config.getInitParameterNames();
			while (e.hasMoreElements()) {
				String n = e.nextElement();
				values += "\n" + n + " = " + config.getInitParameter(n);
			}
			throw new Exception("Undefined request limit " + name + " in web.xml\nConfig:" + values);
		}
		try {
			return Integer.parseUnsignedInt(ip);
		} catch (NumberFormatException e) {
			throw new Exception("Invalid request limit " + name + " in web.xml: '" + ip + "' is not a valid int32.");
		}
	}

	private static String getRequiredProperty(Properties properties, String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			throw new RuntimeException("The configuration requires a '" + key + "' property.");
		}
		return value;
	}

	@Override
	public ServletConfig getServletConfig() {
		// The base class does not store this object.
		return config;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO: Handle the case that the given corpus parameter is an invalid corpus
		// key
		String corpus = request.getParameter(UrlParameters.CORPUS);
		if (corpus == null) {
			corpus = CorpusRegistry.getDefaultKey();
		}
		final Netspeak service = CorpusRegistry.get(corpus).getService();

		if (isQueryRequest(request)) {
			logRequest(request);
			response.setContentType("text/plain"); // for text and json
			response.setCharacterEncoding("UTF-8");
			final Request nsRequest = buildRequest(request);
			final String callback = request.getParameter(UrlParameters.CALLBACK);
			final String format = request.getParameter(UrlParameters.FORMAT);
			final String raw = request.getParameter(UrlParameters.RAW);
			if (raw != null) {
				// a raw response is always send in json format
				RawResponse nsRawResponse = service.searchRaw(nsRequest);
				printTo(nsRawResponse, response.getWriter(), callback);
				logError(request, nsRawResponse);
			} else {
				Response nsResponse = service.search(nsRequest);
				printTo(nsResponse, response.getWriter(), callback,
						format != null && format.toLowerCase().equals("json"));
				logError(request, nsResponse);
			}
		} else if (isDashboardRequest(request)) {
			request.setAttribute("initParameters", getInitParameters());
			request.setAttribute("netspeakProperties", service.getProperties());
			request.getRequestDispatcher("WEB-INF/dashboard.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	private Request buildRequest(HttpServletRequest request) {
		final String query = request.getParameter(UrlParameters.QUERY);
		final Long maxfreq = getOptionalUInt64(request, UrlParameters.MAXFREQ);
		final Integer nmax = getOptionalUInt32(request, UrlParameters.NMAX);
		final Integer nmin = getOptionalUInt32(request, UrlParameters.NMIN);
		final Integer phigh = getOptionalUInt32(request, UrlParameters.PHIGH);
		final Integer plow = getOptionalUInt32(request, UrlParameters.PLOW);
		final Integer topk = getOptionalUInt32(request, UrlParameters.TOPK);
		final Integer max_regex_matches = getOptionalUInt32(request, UrlParameters.MAX_REGEX_MATCHES);

		Request.Builder builder = Request.newBuilder().setQuery(query);

		if (maxfreq != null) {
			builder.setMaxPhraseFrequency(maxfreq);
		}
		if (nmax != null) {
			builder.setPhraseLengthMax(Math.min(nmax, limits.getPhraseLengthMax()));
		}
		if (nmin != null) {
			builder.setPhraseLengthMin(Math.max(nmin, limits.getPhraseLengthMin()));
		}
		if (phigh != null) {
			builder.setPruningHigh(Math.min(phigh, limits.getPruningHigh()));
		}
		if (plow != null) {
			// TODO: Wouldn't you want to use Math.max ?
			builder.setPruningLow(Math.min(plow, limits.getPruningLow()));
		}
		if (topk != null) {
			builder.setMaxPhraseCount(Math.min(topk, limits.getMaxPhraseCount()));
		}
		if (max_regex_matches != null) {
			builder.setMaxRegexwordMatches(max_regex_matches);
		}

		return builder.build();
	}

	private static Integer getOptionalUInt32(HttpServletRequest request, String name) {
		final String value = request.getParameter(name);
		if (value != null) {
			try {
				return Integer.parseUnsignedInt(value);
			} catch (NumberFormatException e) {
				/* Ignore invalid uint32 */ }
		}
		return null;
	}

	private static Long getOptionalUInt64(HttpServletRequest request, String name) {
		final String value = request.getParameter(name);
		if (value != null) {
			try {
				return Long.parseUnsignedLong(value);
			} catch (NumberFormatException e) {
				/* Ignore invalid uint64 */ }
		}
		return null;
	}

	private Map<String, String> getInitParameters() {
		// Use TreeMap instead of HashMap to keep the keys sorted.
		Map<String, String> params = new TreeMap<>();
		Enumeration<String> names = config.getInitParameterNames();
		while (names.hasMoreElements()) {
			final String name = names.nextElement();
			params.put(name, config.getInitParameter(name));
		}
		return params;
	}

	private boolean isDashboardRequest(HttpServletRequest request) {
		return request.getParameter(UrlParameters.DASHBOARD) != null;
	}

	private boolean isQueryRequest(HttpServletRequest request) {
		return request.getParameter(UrlParameters.QUERY) != null;
	}

	// TODO: Do we really want to log IP addresses (privacy concerns)?
	// A hash of the ID should be anonymous enough and can still be used as an
	// identifier.

	private void logError(HttpServletRequest request, RawResponse response) {
		if (response.getErrorCode() != ErrorCode.NO_ERROR.getErrorCode()
				&& response.getErrorCode() != ErrorCode.INVALID_QUERY.getErrorCode()) {
			errLogger.info(String.format("IP: %s AGENT: %s QUERY: %s ERRCODE: %d ERRMSG: %s", request.getRemoteAddr(),
					request.getHeader("user-agent"), request.getParameter(UrlParameters.QUERY), response.getErrorCode(),
					response.getErrorMessage()));
		}
	}

	private void logError(HttpServletRequest request, Response response) {
		if (response.getErrorCode() != ErrorCode.NO_ERROR.getErrorCode()
				&& response.getErrorCode() != ErrorCode.INVALID_QUERY.getErrorCode()) {
			errLogger.info(String.format("IP: %s AGENT: %s QUERY: %s ERRCODE: %d ERRMSG: %s", request.getRemoteAddr(),
					request.getHeader("user-agent"), request.getParameter(UrlParameters.QUERY), response.getErrorCode(),
					response.getErrorMessage()));
		}
	}

	private void logRequest(HttpServletRequest request) {
		final String ip = request.getRemoteAddr();
		final String agent = request.getHeader("user-agent");
		final String query = request.getParameter(UrlParameters.QUERY);
		final String logMessage = String.format("IP: %s AGENT: %s QUERY: %s", ip, agent, query);
		if (agent != null && isBrowser(agent)) {
			webLogger.info(logMessage);
		} else {
			apiLogger.info(logMessage);
		}
	}

	private static final void printTo(RawResponse response, PrintWriter writer, String callback) throws IOException {
		if (callback != null) {
			writer.append(callback).append('(');
			JsonUtil.writeRawResponse(response, writer);
			writer.append(')').append(';');
		} else {
			JsonUtil.writeRawResponse(response, writer);
		}
	}

	private static final void printTo(Response response, PrintWriter writer, String callback, boolean asJson)
			throws IOException {
		if (asJson) {
			if (callback != null) {
				writer.append(callback).append('(');
				JsonUtil.writeResponse(response, writer);
				writer.append(')').append(';');
			} else {
				JsonUtil.writeResponse(response, writer);
			}
		} else {
			// For format=text the callback parameter will be ignored.
			for (Phrase phrase : response.getPhraseList()) {
				writer.printf("%d\t%d\t%s\n", phrase.getId(), phrase.getFrequency(), NetspeakUtil.toString(phrase));
			}
		}
	}

	private static final boolean isBrowser(String userAgent) {
		// There are tons of possible user-agent strings out there:
		// http://www.user-agents.org/
		// Here we do only test the most widely used browser engines.
		return userAgent.matches("(?i).*((mozilla)|(msie)|(webkit)|(opera)).*");
	}

	private static Configuration[] loadConfigurations(ServletConfig config) throws IOException {
		String[] confFiles = config.getInitParameter(InitParameters.CORPUS_CONFIGURATION_FILES).split(",");

		ArrayList<Configuration> configs = new ArrayList<>();

		for (String file : confFiles) {
			// Search absolute paths in the file system;
			// relative paths in the classpath
			try (InputStream is = file.startsWith("/") ? new FileInputStream(file)
					: SearchServlet.class.getResourceAsStream("/" + file)) {
				if (is == null) {
					throw new FileNotFoundException("Failed to load config file:" + file);
				}
				Configuration configuration = new Configuration();
				configuration.load(is);
				configs.add(configuration);
			}
		}

		return configs.toArray(new Configuration[0]);
	}
}
