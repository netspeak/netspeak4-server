package org.netspeak.server;


import static java.util.Objects.requireNonNull;

import org.netspeak.Netspeak;


/**
 * A container for a Netspeak Corpus
 */
public class Corpus {

	private final Netspeak service;
	private final String name;
	private final String language;


	public Corpus(Netspeak service, String name, String language) {
		this.service = requireNonNull(service);
		this.name = requireNonNull(name);
		this.language = requireNonNull(language);
	}

	public Netspeak getService() {
		return service;
	}

	public String getName() {
		return name;
	}

	/**
	 * The ISO-639-1 code of the language of this corpus.
	 *
	 * @return
	 */
	public String getLanguage() {
		return language;
	}

}
