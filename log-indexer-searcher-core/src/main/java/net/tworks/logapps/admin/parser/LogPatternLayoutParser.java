/**
 * 
 */
package net.tworks.logapps.admin.parser;

import java.util.ArrayList;
import java.util.List;

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

	private String logPatternLayout;

	public LogPatternLayoutParser(String logPatternLayout) {
		this.logPatternLayout = logPatternLayout;
		logger.info("Configured LogPatternLayoutParser for the layout {}.",
				logPatternLayout);
	}

	public List<String> generateTokenNames() {
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

}
