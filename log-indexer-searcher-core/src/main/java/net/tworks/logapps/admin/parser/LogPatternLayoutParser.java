/**
 * 
 */
package net.tworks.logapps.admin.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author asgs
 * 
 *         This class parses the log file's pattern layout to identify if there
 *         are key-value pairs. These tokens will help speed up the search
 *         process than when querying a keyword in an entire log content.
 *         Therefore, this class merely looks for patterns where key-value pairs
 *         are separated by an equal <code>=</code>sign. This class can later be
 *         augmented by supporting additional separator characters like colon,
 *         semi-colon, etc., based on the actual requirements.
 */
public class LogPatternLayoutParser {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final static String TIMESTAMP_PATTERN_STRING = ".*%d[ate]*\\{([\\w.\\s'/\\-':]+)[\\}]*.*";

	private final static Pattern TIMESTAMP_REGEX_PATTERN = Pattern
			.compile(TIMESTAMP_PATTERN_STRING);

	private String logPatternLayout;

	/**
	 * Initializes the instance with the pattern layout string.
	 * 
	 * @param logPatternLayout
	 */
	public LogPatternLayoutParser(String logPatternLayout) {
		this.logPatternLayout = logPatternLayout;
		logger.info("Configured LogPatternLayoutParser for the layout {}.",
				logPatternLayout);
	}

	/**
	 * Generates a list of key names which follow the standard = separated
	 * key-value format.
	 * 
	 * @return List of key names.
	 */
	public List<String> generateKeyValueTokenNames() {
		List<String> tokenNameList = new ArrayList<String>();
		String[] splits = logPatternLayout.split(" ");
		List<String> list = Arrays.asList(splits);
		list.stream().forEach(string -> {
			String[] tokens = string.split("=");
			if (tokens.length == 2) {
				tokenNameList.add(tokens[0]);
				if (logger.isDebugEnabled()) {
					logger.debug("Found a = separated key {}.", tokens[0]);
				}
			}
		});
		logger.info("Constructed a token list of size {} for the layout {}",
				tokenNameList.size(), logPatternLayout);
		return tokenNameList;
	}

	/**
	 * Retrieves the Timestamp format specified by the pattern layout.
	 * 
	 * @return The format of the Timestamp.
	 */
	public String parseTimeStampFormat() {
		String timeStampFormat = null;
		Matcher matcher = TIMESTAMP_REGEX_PATTERN.matcher(logPatternLayout);
		while (matcher.find()) {
			timeStampFormat = matcher.group(1);
			break;
		}
		return timeStampFormat;
	}

	/**
	 * Pattern matches the source to check if there's a Timestamp present.
	 * 
	 * @return whether Timestamp is present.
	 */
	public boolean hasTimeStampField() {
		Matcher matcher = TIMESTAMP_REGEX_PATTERN.matcher(logPatternLayout);
		return matcher.matches();
	}

	/**
	 * Parses the each log line to find the position of the Timestamp field, if
	 * any.
	 * 
	 * @return position of the Timestamp field.
	 */
	public int findPositionOfTimeStampField() {
		int index = 0;
		if (hasTimeStampField()) {

			String[] splits = logPatternLayout.split(" ");
			for (String split : splits) {
				if (split.matches(TIMESTAMP_PATTERN_STRING)) {
					break;
				}
				index++;
			}
			return index;
		} else {
			return --index;
		}
	}

	/*
	 * public static void main(String[] args) { //
	 * System.out.println(parseLogLevel()); LogPatternLayoutParser
	 * logPatternLayoutParser = new LogPatternLayoutParser(
	 * "%X{IP} %X{field1} %X{field2} [%date{dd/MMM/yyyy:HH:mm:ss Z}] %msg%n");
	 * System.out.println(logPatternLayoutParser.hasTimeStampField());
	 * System.out.println(logPatternLayoutParser.parseTimeStampFormat());
	 * System.out.println(logPatternLayoutParser
	 * .findPositionOfTimeStampField()); }
	 */

}
