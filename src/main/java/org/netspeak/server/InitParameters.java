package org.netspeak.server;


public final class InitParameters {

	private InitParameters() {}

	// -------------------------------------------------------------------------
	// web.xml parameter names
	// -------------------------------------------------------------------------

	public static final String CORPUS_DEFAULT_KEY = "corpus.default.key";
	public static final String CORPUS_CONFIGURATION_FILES = "corpus.configuration.files";

	public static final String CORPUS_NAME = "corpus.name";
	public static final String CORPUS_LANGUAGE = "corpus.language";
	public static final String CORPUS_REGISTRY_KEY = "corpus.registry.key";

	public static final String MAX_PHRASE_COUNT = "limit.max.phrase.count";
	public static final String PHRASE_LENGTH_MAX = "limit.phrase.length.max";
	public static final String PHRASE_LENGTH_MIN = "limit.phrase.length.min";
	public static final String PRUNING_HIGH = "limit.pruning.high";
	public static final String PRUNING_LOW = "limit.pruning.low";

}
