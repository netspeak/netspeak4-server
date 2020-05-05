package org.netspeak.server;

import java.util.HashMap;
import java.util.Map;

import org.netspeak.Netspeak;
import org.netspeak.generated.NetspeakMessages.Request;

/**
 * This class manages instances of {@link Netspeak}, that can be registered and unregistered by an
 * application under some user-defined key. In the future this class might serve proxy objects to
 * forward or dispatch {@link Request} instead of returning the {@link Netspeak} instance itself.
 */
public final class CorpusRegistry {

	private static String DEFAULT_KEY;
	private static final Map<String, Corpus> CORPORA = new HashMap<>();

	private CorpusRegistry() {}

	public static Corpus get(String key) {
		return CORPORA.get(key);
	}

	public static void register(String key, Corpus corpus) {
		CORPORA.put(key, corpus);
	}

	public static void unregister(String key) {
		CORPORA.remove(key);
	}

	public static Iterable<Map.Entry<String, Corpus>> entrySet() {
		return CORPORA.entrySet();
	}

	public static void setDefaultKey(String defaultKey) {
		DEFAULT_KEY = defaultKey;
	}

	public static String getDefaultKey() {
		return DEFAULT_KEY;
	}

}
