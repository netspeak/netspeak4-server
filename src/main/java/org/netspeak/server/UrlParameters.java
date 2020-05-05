package org.netspeak.server;


public final class UrlParameters {

	private UrlParameters() {}

	// -------------------------------------------------------------------------
	// URL query parameters (REST interface)
	// -------------------------------------------------------------------------

	public static final String CALLBACK = "callback"; // (string)
	public static final String DASHBOARD = "dashboard"; // (flag)
	public static final String CORPUS = "corpus"; // (string)
	public static final String FORMAT = "format"; // [text|json]
	public static final String MAXFREQ = "maxfreq"; // (uint64)
	public static final String NMAX = "nmax"; // (uint32)
	public static final String NMIN = "nmin"; // (uint32)
	public static final String PHIGH = "phigh"; // (uint32)
	public static final String PLOW = "plow"; // (uint32)
	public static final String QUERY = "query"; // (string)
	public static final String RAW = "raw"; // (flag)
	public static final String TOPK = "topk"; // (uint32)
	public static final String MAX_REGEX_MATCHES = "maxregexmatches";

}
