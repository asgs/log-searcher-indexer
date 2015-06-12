/**
 * 
 */
package net.tworks.logapps.admin.parser;

import java.util.ArrayList;
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

	private final static String TIMESTAMP_PATTERN_STRING = ".*%d[ate]*\\{([\\w\\.\\:]+)}.*";

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
	 * @return
	 */
	public List<String> generateKeyValueTokenNames() {
		List<String> tokenNameList = new ArrayList<String>();
		String[] splits = logPatternLayout.split(" ");
		for (String string : splits) {
			String[] tokens = string.split("=");
			if (tokens.length == 2) {
				tokenNameList.add(tokens[0]);
				if (logger.isDebugEnabled()) {
					logger.debug("Found a = separated key {}.", tokens[0]);
				}
			}
		}
		logger.info("Constructed a token list of size {} for the layout {}",
				tokenNameList.size(), logPatternLayout);
		return tokenNameList;
	}

	/**
	 * Retrieves the time stamp format specified by the pattern layout.
	 * 
	 * @return
	 */
	public String parseTimeStampFormat() {
		String timeStampFormat = null;
		// String logPatternLayout =
		// "%-30(%d{HH:mm:ss.SSS} [%thread]) %-5level %logger{32} - %msg%n";
		Matcher matcher = TIMESTAMP_REGEX_PATTERN.matcher(logPatternLayout);
		while (matcher.find()) {
			timeStampFormat = matcher.group(1);
			break;
		}
		return timeStampFormat;
	}

	public static void main(String[] args) {
		// System.out.println(parseLogLevel());
	}

}
