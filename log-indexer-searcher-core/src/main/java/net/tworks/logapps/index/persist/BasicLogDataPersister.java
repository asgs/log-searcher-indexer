/**
 * 
 */
package net.tworks.logapps.index.persist;

import static net.tworks.logapps.common.util.Constants.ORACLE_TIMESTAMP_FORMAT;
import static net.tworks.logapps.common.util.Constants.WINDOWS_LINE_SEPARATOR;

import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;

import net.tworks.logapps.admin.parser.LogPatternLayoutParser;
import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.model.SourceDTO;
import net.tworks.logapps.index.metadata.StructuredEventMetaDataEnumerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * @author asgs
 *
 *         A default implementation to persist the given Log file data to a
 *         Database.
 */
@Component
public class BasicLogDataPersister implements LogDataPersister, Observer {

	@Autowired
	private DataSourceManager dataSourceManager;

	@Autowired
	private StructuredEventMetaDataEnumerator structuredEventMetaDataEnumerator;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private JdbcTemplate jdbcTemplate;

	private final String sqlForGettingSourcePatternLayout = "select source_type, pattern_layout from source_metadata where source = ?";

	private final String sqlForInsertingRawEventData = "insert into raw_event (raw_event_data, event_timestamp, source_type) values(?, ?, ?)";

	private final String sqlForInsertingStructuredEventData = "insert into structured_event ";

	@PostConstruct
	public void initJDBCTemplate() {
		jdbcTemplate = dataSourceManager.getJdbcTemplate();
	}

	@Override
	@Transactional
	public void persistLogData(String source, String logContents) {
		// 1. Retrieve the pattern_layout and source from the table
		// source_metadata for the given source.

		String[] sourceMetadata = jdbcTemplate.queryForObject(
				sqlForGettingSourcePatternLayout, new String[] { source }, (
						ResultSet resultSet, int rowNum) -> {

					String[] strings = new String[2];
					strings[0] = resultSet.getString(1);
					strings[1] = resultSet.getString(2);
					return strings;
				});

		// 2. Insert the row(s) to raw_event table.
		String sourceType = sourceMetadata[0];
		String sourcePatternLayout = sourceMetadata[1];
		logger.info("sourcePatternLayout is " + sourcePatternLayout);
		LogPatternLayoutParser logPatternLayoutParser = new LogPatternLayoutParser(
				sourcePatternLayout);
		logPatternLayoutParser.generateKeyValueTokenNames();
		int indexOfTimeStampField = logPatternLayoutParser
				.findPositionOfTimeStampField();
		String oracleTimeStampValue = null;
		String timeStampFormat = logPatternLayoutParser.parseTimeStampFormat();
		logger.info("TimeStampFormat is {}.", timeStampFormat);
		String[] lines = logContents.split(WINDOWS_LINE_SEPARATOR);
		for (String line : lines) {
			// Skip stack traces for now.
			if (StringUtils.isEmpty(line) || line.startsWith("\t")) {
				logger.info("Received an empty line. Skipping and moving on to the next.");
				continue;
			}
			// Escaping is required in Oracle.
			line = line.replace("'", "''");
			// Commented because of too much log verbosity.
			// logger.info("Current log line is {}.", line);
			String[] tokens = line.split(" ");
			if (indexOfTimeStampField != -1) {
				String timeStampValue = "";
				String[] splits = timeStampFormat.split(" ");
				if (splits.length > 1) {
					int noOfWords = splits.length;
					for (int counter = 0; counter < noOfWords; counter++) {
						logger.info(
								"Token size is {}. indexOfTimeStampField is {}. noOfWords is {}. counter is {}.",
								tokens.length, indexOfTimeStampField,
								noOfWords, counter);
						if (!timeStampValue.equals("")) {
							timeStampValue = timeStampValue + " "
									+ tokens[indexOfTimeStampField + counter];
						} else {
							timeStampValue = timeStampValue
									+ tokens[indexOfTimeStampField + counter];
						}
					}
				} else {
					timeStampValue = tokens[indexOfTimeStampField];
				}
				timeStampValue = timeStampValue.replaceAll("\\[", "");
				// This hack
				if (timeStampFormat.contains("z")
						|| timeStampFormat.contains("Z")
						|| timeStampFormat.contains("XXX")) {
					timeStampValue = timeStampValue + " +0530";
				}
				logger.info("Timestamp from logs is {}.", timeStampValue);
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						timeStampFormat);
				try {
					Date parse = dateFormat.parse(timeStampValue);
					String format = dateFormat.format(parse);
					logger.info("Parsed format {}", format);
					SimpleDateFormat dateFormat2 = new SimpleDateFormat(
							ORACLE_TIMESTAMP_FORMAT);
					oracleTimeStampValue = dateFormat2.format(parse);
					logger.info("Final date in Oracle format is {}.",
							oracleTimeStampValue);
				} catch (ParseException e) {
					logger.error("Error parsing the timestamp {}.", e);
					SimpleDateFormat dateFormat2 = new SimpleDateFormat(
							ORACLE_TIMESTAMP_FORMAT);
					oracleTimeStampValue = dateFormat2.format(new Date());
					logger.info("Default timeStamp created as {}.",
							oracleTimeStampValue);
				}
			} else {
				// Generate a timestamp as of current time.
				SimpleDateFormat dateFormat2 = new SimpleDateFormat(
						ORACLE_TIMESTAMP_FORMAT);
				oracleTimeStampValue = dateFormat2.format(new Date());
				logger.info("Default timeStamp created as {}.",
						oracleTimeStampValue);

			}

			if (!StringUtils.isEmpty(line)) {
				try {
					jdbcTemplate.update(sqlForInsertingRawEventData,
							new Object[] { line, oracleTimeStampValue,
									sourceType });
					if (logger.isDebugEnabled()) {
						logger.debug("Successfully copied log line to raw_event table.");
					}
				} catch (DataAccessException e) {
					logger.error(
							"Error inserting log content to raw_event table. cause is {}.",
							e);
					return;
				}
				String logMessage = parseLogMessage(line, sourcePatternLayout);
				List<String> metaData = structuredEventMetaDataEnumerator
						.retrieveMetadata();
				Map<String, String> tokenMap = parseTokens(metaData, line);
				// 3. Insert the row(s) to the structured_event table.
				StringBuilder builder = new StringBuilder(
						sqlForInsertingStructuredEventData);
				builder.append("(message_content,");
				StringBuilder valueBuilder = new StringBuilder(" values('");
				valueBuilder.append(logMessage);
				valueBuilder.append("',");
				String threadName = parseThreadName(line, sourcePatternLayout);
				if (threadName != null) {
					builder.append("thread_name,");
					valueBuilder.append("'");
					valueBuilder.append(threadName);
					valueBuilder.append("',");
				}

				String logLevel = parseLogLevel(line, sourcePatternLayout);
				if (logLevel != null) {
					builder.append("log_level,");
					valueBuilder.append("'");
					valueBuilder.append(logLevel);
					valueBuilder.append("',");
				}

				if (!tokenMap.isEmpty()) {
					tokenMap.forEach((String key, String value) -> {
						builder.append(key);
						builder.append(",");
						valueBuilder.append(value);
						valueBuilder.append("',");
					});					
				} else {
					logger.info("tokenMap is empty.");
				}
				builder.deleteCharAt(builder.length() - 1);
				valueBuilder.deleteCharAt(valueBuilder.length() - 1);
				builder.append(")");
				valueBuilder.append(")");
				String finalQuery = builder.toString()
						+ valueBuilder.toString();
				logger.info("Insert query for structured_event is {}.",
						finalQuery);
				jdbcTemplate.update(finalQuery);
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully copied log line to structured_event table.");
				}

			} else {
				logger.info("Received an empty line. Discarding it.");
			}

		}

	}

	/**
	 * Parses the Log level of a given log line based on its pattern layout.
	 * 
	 * @return Log Level of the statement
	 */
	private String parseLogLevel(String logLine, String patternLayout) {
		if (!patternLayout.contains("%level")) {
			return null;
		}

		String[] tokens = patternLayout.split(" ");
		int counter = 0;
		for (String token : tokens) {
			counter++;
			if (token.contains("%level")) {
				break;
			}
		}
		tokens = logLine.split(" ");
		int indexOfLogMessage = counter - 1;
		String logMessage = tokens[indexOfLogMessage];
		logger.info("Log level is {}.", logMessage);
		return logMessage;
	}

	/**
	 * Parses the Log level of a given log line based on its pattern layout.
	 * 
	 * @return Log Level of the statement
	 */
	private String parseThreadName(String logLine, String patternLayout) {

		if (!patternLayout.contains("%thread")) {
			return null;
		}

		String[] tokens = patternLayout.split(" ");
		int counter = 0;
		for (String token : tokens) {
			counter++;
			if (token.contains("%thread")) {
				break;
			}
		}
		tokens = logLine.split(" ");
		int indexOfThreadName = counter - 1;
		String threadName = tokens[indexOfThreadName];
		logger.info("Thread name is {}.", threadName);
		return threadName;
	}

	/**
	 * Parses the actual log content of each log line.
	 * 
	 * @param logLine
	 * @param patternLayout
	 * @return Log message
	 */
	private String parseLogMessage(String logLine, String patternLayout) {
		String[] tokens = patternLayout.split(" ");
		int counter = 0;
		for (String token : tokens) {
			counter++;
			if (token.contains("%msg")) {
				break;
			}
		}
		tokens = logLine.split(" ");
		int indexOfLogMessage = counter - 1;
		int indexInLogLine = logLine.indexOf(tokens[indexOfLogMessage]);
		String logMessage = logLine.substring(indexInLogLine);
		logger.info("logMessage is {}.", logMessage);
		return logMessage;
	}

	/**
	 * Strips the key-value pairs from each log line.
	 * 
	 * @param metaData
	 * @param logLine
	 * @return Map of key-value pairs.
	 */
	private Map<String, String> parseTokens(List<String> metaData,
			String logLine) {
		String[] tokens = new String[metaData.size()];
		int counter = 0;
		Map<String, String> tokenMap = new LinkedHashMap<String, String>();
		for (String metaDatum : metaData) {
			int index;
			if ((index = logLine.indexOf(metaDatum + "=")) != -1) {
				String temp = logLine.substring(index);
				int indexOfSpace = temp.indexOf(' ');
				tokens[counter] = logLine
						.substring(index, index + indexOfSpace);
				logger.info("Retrieved token {}.", tokens[counter]);
				String[] splits = tokens[counter].split(" ");
				tokenMap.put(splits[0], splits[1]);
			}
			counter++;
		}
		return tokenMap;
	}

	/*
	 * public static void main(String[] args) throws FileNotFoundException,
	 * IOException {
	 * 
	 * String readLines = IOUtils .toString( new FileInputStream(
	 * "G:/dev/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-13.txt"
	 * ), "UTF-8"); // new BasicLogDataPersister().persistLogData("blah2",
	 * readLines); // new BasicLogDataPersister().parseLogMessage(); new
	 * BasicLogDataPersister() .parseTokens( Arrays.asList("guid", "userId"),
	 * "127.0.0.1 - - [13/Jun/2015:21:16:29 +0530] guid=ssdsd-dsds userId=someName-blah \"GET /log-indexer-searcher-webapp/log/retrieveAvailableLogs HTTP/1.1\" 200 146"
	 * );
	 * 
	 * 
	 * new BasicLogDataPersister().parseThreadName(); }
	 */

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof SourceDTO) {
			SourceDTO sourceDTO = (SourceDTO) arg;
			logger.info(
					"Received event notification on update of the file {}.",
					sourceDTO.getSource());
			persistLogData(sourceDTO.getSource(), sourceDTO.getSourceContents());
		} else {
			logger.warn(
					"Discarded an event update from non-SourceDTO of type {}.",
					arg != null ? arg.getClass() : null);
		}

	}

}
