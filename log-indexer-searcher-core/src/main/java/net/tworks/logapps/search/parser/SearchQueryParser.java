/**
 * 
 */
package net.tworks.logapps.search.parser;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.tworks.logapps.rest.model.SearchQuery;

/**
 * @author asgs
 * 
 *         The default (and may be the only) implementation to parse the given
 *         search query and generate the <code>SearchQuery</code> domain model
 *         object.
 *
 */
public class SearchQueryParser {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private List<String> keyValueList = new ArrayList<String>();

	private static final String REGEX_STRING_KEY_VALUE = "([\\w\\-]*)?=([\\w\\-]*)?";

	private static final Pattern REGEX_PATTERN_KEY_VALUE = Pattern
			.compile(REGEX_STRING_KEY_VALUE);

	/**
	 * The query on which this parser is going to act.
	 */
	private String fullQuery;

	/**
	 * The duration for which the query has to be run under.
	 */
	private long timeDuration;

	/**
	 * The unit of the timeDuration field.
	 */
	private ChronoUnit timeUnit;

	public SearchQueryParser(String fullQuery, long timeDuration,
			String timeUnit) {
		this.fullQuery = fullQuery;
		this.timeDuration = timeDuration;
		if (timeUnit == null) {
			this.timeUnit = null;
		} else {
			this.timeUnit = ChronoUnit.valueOf(timeUnit);
		}

	}

	/**
	 * The method to infer the search query and make it search-able.
	 * 
	 * @return A SearchQuery instance.
	 */
	public SearchQuery parse() {
		SearchQuery searchQuery = new SearchQuery(fullQuery);
		Map<String, String> keyValues = parseKeyValues();
		searchQuery.setSearchIndex(parseSearchIndex(keyValues));
		searchQuery.setSourceType(parseSourceType(keyValues));
		searchQuery.setThreadName(parseThreadName(keyValues));
		searchQuery.setLogLevel(parseLogLevel(keyValues));
		searchQuery.setMainQuery(getUnmatchedString());
		searchQuery.setKeyValues(keyValueList);
		logger.info("SearchQuery parsed and populated.");
		return searchQuery;
	}

	public String parseSearchIndex(Map<String, String> keyValues) {
		for (Map.Entry<String, String> keyEntry : keyValues.entrySet()) {
			if (keyEntry.getKey().equals("index")) {
				return keyEntry.getValue();
			}
		}
		return null;
	}

	public String parseSourceType(Map<String, String> keyValues) {
		for (Map.Entry<String, String> keyEntry : keyValues.entrySet()) {
			if (keyEntry.getKey().equals("source_type")) {
				return keyEntry.getValue();
			}
		}
		return null;
	}

	public String parseThreadName(Map<String, String> keyValues) {
		for (Map.Entry<String, String> keyEntry : keyValues.entrySet()) {
			if (keyEntry.getKey().equals("thread_name")) {
				return keyEntry.getValue();
			}
		}
		return null;
	}

	public String parseLogLevel(Map<String, String> keyValues) {
		for (Map.Entry<String, String> keyEntry : keyValues.entrySet()) {
			if (keyEntry.getKey().equals("log_level")) {
				return keyEntry.getValue();
			}
		}
		return null;
	}

	public Map<String, String> parseKeyValues() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		Matcher matcher = REGEX_PATTERN_KEY_VALUE.matcher(fullQuery);
		logger.info("Key value pairs parsed from the query below.");
		while (matcher.find()) {
			logger.info("{}={}.", matcher.group(1), matcher.group(2));
			keyValueList.add(matcher.group(1) + "=" + matcher.group(2));
			map.put(matcher.group(1), matcher.group(2));
		}
		return map;
	}

	public String getUnmatchedString() {
		if (keyValueList.isEmpty()) {
			return fullQuery;
		} else {
			StringBuilder builder = new StringBuilder(fullQuery);
			for (String string : keyValueList) {
				int indexOf = builder.indexOf(string);
				builder = builder.delete(indexOf, indexOf + string.length());
			}
			String string = builder.toString();
			if (string != null) {
				return builder.toString().trim();
			} else {
				return string;
			}
		}
	}

	public static void main(String[] args) {

		SearchQueryParser searchQueryParser = new SearchQueryParser(
				"index=blah_blah source_type=catalina_log thread_name=webcontainer-1 My search query goes here... log_level=debug guid=valuea userId=valueb ",
				100, "MINUTES");
		// System.out.println(searchQueryParser.fullQuery.split(" "));
		/*
		 * System.out.println(searchQueryParser.parseSearchIndex());
		 * System.out.println(searchQueryParser.parseSourceType());
		 * System.out.println(searchQueryParser.parseThreadName());
		 * System.out.println(searchQueryParser.parseLogLevel());
		 */
		System.out.println(searchQueryParser.parseKeyValues());
		System.out.println(searchQueryParser.getUnmatchedString());

	}

}
