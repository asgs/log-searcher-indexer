/**
 * 
 */
package net.tworks.logapps;

import static org.junit.Assert.*;
import net.tworks.logapps.admin.parser.LogPatternLayoutParser;

import org.junit.Test;

/**
 * @author asgs
 *
 */
public class LogPatternLayoutParserTest {

	private LogPatternLayoutParser logPatternLayoutParser;

	/**
	 * 
	 */
	public LogPatternLayoutParserTest() {
		logPatternLayoutParser = new LogPatternLayoutParser(
				"%X{IP} %X{field1} %X{field2} [%date{dd/MMM/yyyy:HH:mm:ssZ} %msg%n");
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.admin.parser.LogPatternLayoutParser#LogPatternLayoutParser(java.lang.String)}
	 * .
	 */
	@Test
	public final void testLogPatternLayoutParser() {
		new LogPatternLayoutParserTest();
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.admin.parser.LogPatternLayoutParser#generateKeyValueTokenNames()}
	 * .
	 */
	@Test
	public final void testGenerateKeyValueTokenNames() {
		if (!logPatternLayoutParser.generateKeyValueTokenNames().isEmpty()) {
			fail("Failed! Unable to generate tokens.");
		}

	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.admin.parser.LogPatternLayoutParser#parseTimeStampFormat()}
	 * .
	 */
	@Test
	public final void testParseTimeStampFormat() {
		if (logPatternLayoutParser.parseTimeStampFormat() == null) {
			fail("Couldn't retrieve the TS format.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.admin.parser.LogPatternLayoutParser#hasTimeStampField()}
	 * .
	 */
	@Test
	public final void testHasTimeStampField() {
		if (!logPatternLayoutParser.hasTimeStampField()) {
			fail("Couldn't find TS field.");
		}
	}

	/**
	 * Test method for
	 * {@link net.tworks.logapps.admin.parser.LogPatternLayoutParser#findPositionOfTimeStampField()}
	 * .
	 */
	@Test
	public final void testFindPositionOfTimeStampField() {
		if (logPatternLayoutParser.findPositionOfTimeStampField() == -1) {
			fail("Position of TS Field is invalid.");
		}
	}

}
