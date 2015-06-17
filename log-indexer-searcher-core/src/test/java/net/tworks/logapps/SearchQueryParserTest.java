/**
 * 
 */
package net.tworks.logapps;

import static org.junit.Assert.*;

import java.util.Map;

import net.tworks.logapps.search.parser.SearchQueryParser;

import org.junit.Test;

/**
 * @author asgs
 *
 */
public class SearchQueryParserTest {

	private SearchQueryParser searchQueryParser;

	public SearchQueryParserTest() {
		searchQueryParser = new SearchQueryParser(
				"index=blah_blah source_type=catalina_log thread_name=webcontainer-1 My search query goes here... log_level=debug guid=valuea userId=valueb",
				100, "MINUTES");
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#SearchQueryParser(java.lang.String, long, java.lang.String)}
	 * .
	 */
	@Test
	public final void testSearchQueryParser() {
		SearchQueryParserTest searchQueryParserTest = null;
		try {
			searchQueryParserTest = new SearchQueryParserTest();
		} catch (Exception e) {
			fail("Failed to create instance of SearchQueryParser");
		} finally {
			searchQueryParserTest = null;
		}

	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parse()}.
	 */
	@Test
	public final void testParse() {
		try {
			searchQueryParser.parse();
		} catch (Exception e) {
			fail("Failed to invoke parse() successfully.");
		}

	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parseSearchIndex(java.util.Map)}
	 * .
	 */
	@Test
	public final void testParseSearchIndex() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		} else if (searchQueryParser.parseSearchIndex(parseKeyValues) == null) {
			fail("Couldn't retrieve SearchIndex.");
		}

	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parseSourceType(java.util.Map)}
	 * .
	 */
	@Test
	public final void testParseSourceType() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		} else if (searchQueryParser.parseSourceType(parseKeyValues) == null) {
			fail("Couldn't retrieve SourceType.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parseThreadName(java.util.Map)}
	 * .
	 */
	@Test
	public final void testParseThreadName() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		} else if (searchQueryParser.parseThreadName(parseKeyValues) == null) {
			fail("Couldn't retrieve ThreadName.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parseLogLevel(java.util.Map)}
	 * .
	 */
	@Test
	public final void testParseLogLevel() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		} else if (searchQueryParser.parseLogLevel(parseKeyValues) == null) {
			fail("Couldn't retrieve LogLevel.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#parseKeyValues()}
	 * .
	 */
	@Test
	public final void testParseKeyValues() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.search.parser.SearchQueryParser#getUnmatchedString()}
	 * .
	 */
	@Test
	public final void testGetUnmatchedString() {
		Map<String, String> parseKeyValues = searchQueryParser.parseKeyValues();
		if (parseKeyValues.isEmpty()) {
			fail("Couldn't retrieve KVs.");
		} else if (searchQueryParser.getUnmatchedString() == null) {
			fail("Couldn't retrieve UnmatchedString.");
		}
	}

}
