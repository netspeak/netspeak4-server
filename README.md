# Netspeak 4 server

[![Actions Status](https://github.com/netspeak/netspeak4-server/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/netspeak/netspeak4-server/actions)

This project is the Netspeak 4 Tomcat server which provides the web API.


## Build

```bash
gradle war
```

The war will be located at `build/libs/netspeak4-server.war`.


## Run

You can then copy the war into your Tomcat `webapps/` folder.

To run the war, you also need to have the Netspeak 4 C++ library plus dependencies installed.
The index configuration files might also have to be adjusted.


## Configure

__TODO: There are many details which have to be documented.__

All configuration files are located in `src/main/webapp/WEB-INF/classes/`.

__Netspeak indexes:__

All files with a name like `netspeak-*.properties` will be read as corpus configuration files.
These files offer various properties which determine the location of the index and how Netspeak handles the index.

You can add or remove indexes by adding or removing (or renaming) configuration files.

An example of a configuration file:

```properties
# A unique key for the corpus
# This will be the value of the 'corpus' parameter in requests
corpus.registry.key=web-en
# The English name of the indexed language
corpus.name=English
# The ISO 639-1 language code of the indexed language
corpus.language=en

cache.capacity=100000

path.to.phrase.corpus=/netspeak/netspeak4-web-en/phrase-corpus
path.to.phrase.dictionary=/netspeak/netspeak4-web-en/phrase-dictionary
path.to.phrase.index=/netspeak/netspeak4-web-en/phrase-index
path.to.postlist.index=/netspeak/netspeak4-web-en/postlist-index
path.to.hash.dictionary=/netspeak/netspeak4-web-en/hash-dictionary
path.to.regex.vocabulary=/netspeak/netspeak4-web-en/regex-vocabulary
```

__Log4j:__

You can change the output paths of the logs in `log4j.xml`.

By default log will be written to `/var/log/netspeak/`.


---

## Contributors

Michael Schmidt (2018 - 2020)

Martin Trenkmann (2008 - 2013)

Martin Potthast (2008 - 2020)

Benno Stein (2008 - 2020)

