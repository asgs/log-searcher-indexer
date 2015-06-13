/**
 * 
 */
package net.tworks.logapps.index.persist;

import java.io.FileNotFoundException;
import java.io.IOException;
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
import net.tworks.logapps.index.watch.FileWatcher;

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
	private FileWatcher fileWatcher;

	@Autowired
	private StructuredEventMetaDataEnumerator structuredEventMetaDataEnumerator;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private JdbcTemplate jdbcTemplate;

	private static final String ORACLE_TIMESTAMP_FORMAT = "dd-MMM-YY hh.mm.ss.SSS a XXX";

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
		// 1. Retrieve the pattern_layout from the table source_metadata for the
		// given source.

		String[] sourceMetadata = jdbcTemplate.queryForObject(
				sqlForGettingSourcePatternLayout, new String[] { source }, (
						ResultSet resultSet, int rowNum) -> {

					String[] strings = new String[2];
					strings[0] = resultSet.getString(1);
					strings[1] = resultSet.getString(2);
					return strings;
				});

		// String sourcePatternLayout =
		// "%X{IP} %X{field1} %X{field2} [%date{dd/MMM/yyyy:HH:mm:ssZ} guid=%{guid} userId=%{userId} %msg%n";
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
		String[] lines = logContents.split("\n");
		for (String line : lines) {
			String[] tokens = line.split(" ");
			if (indexOfTimeStampField != -1) {
				String timeStampValue = tokens[indexOfTimeStampField];
				timeStampValue = timeStampValue.replaceAll("\\[", "");
				timeStampValue = timeStampValue + " +0530";
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				// Generate a timestamp as of current time.
				SimpleDateFormat dateFormat2 = new SimpleDateFormat(
						ORACLE_TIMESTAMP_FORMAT);
				oracleTimeStampValue = dateFormat2.format(new Date());
				logger.info("Default timeStamp created as {}.",
						oracleTimeStampValue);

			}

			// insert into raw_event (raw_event_data, event_timestamp,
			// source_type) values('dfdf', TO_TIMESTAMP_TZ('13-Jun-15
			// 02.23.04.000 AM +0530'), 'info_log')

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
				}

				String logMessage = parseLogMessage(line, sourcePatternLayout);
				List<String> metaData = structuredEventMetaDataEnumerator
						.retrieveMetadata();
				Map<String, String> tokenMap = parseTokens(metaData, line);
				StringBuilder builder = new StringBuilder(
						sqlForInsertingStructuredEventData);
				builder.append("(message_content,");
				StringBuilder valueBuilder = new StringBuilder(" values('");
				valueBuilder.append(logMessage);
				valueBuilder.append("',");
				logger.info("Before tokenization.");
				logger.info("builder is {}.", builder.toString());
				logger.info("valueBuilder is {}.", valueBuilder.toString());
				if (!tokenMap.isEmpty()) {
					for (Entry<String, String> entry : tokenMap.entrySet()) {
						builder.append(entry.getKey());
						builder.append(",");
						valueBuilder.append(entry.getValue());
						valueBuilder.append(",");
					}
				} else {
					logger.info("tokenMap is empty.");
				}
				logger.info("Before tokenization.");
				logger.info("builder is {}.", builder.toString());
				logger.info("valueBuilder is {}.", valueBuilder.toString());
				builder.deleteCharAt(builder.length() - 1);
				valueBuilder.deleteCharAt(valueBuilder.length() - 1);
				logger.info("After last char deletion.");
				logger.info("builder is {}.", builder.toString());
				logger.info("valueBuilder is {}.", valueBuilder.toString());
				builder.append(")");
				valueBuilder.append(")");
				logger.info("Before concatenation.");
				logger.info("builder is {}.", builder.toString());
				logger.info("valueBuilder is {}.", valueBuilder.toString());
				String finalQuery = builder.toString()
						+ valueBuilder.toString();
				logger.info("Insert query for structured_event is {}.",
						finalQuery);
				jdbcTemplate.update(finalQuery);

			} else {
				logger.info("Received an empty line. Discarding it.");
			}

		}

	}

	private String parseLogMessage(String logLine, String patternLayout) {
		/*
		 * String patternLayout =
		 * "%X{IP} %X{field1} %X{field2} [%date{dd/MMM/yyyy:HH:mm:ss Z}] %msg%n"
		 * ; String logLine =
		 * "127.0.0.1 - - [13/Jun/2015:21:16:29 +0530] \"GET /log-indexer-searcher-webapp/log/retrieveAvailableLogs HTTP/1.1\" 200 146"
		 * ;
		 */
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
		logger.info("logMessage is " + logMessage);
		return logMessage;
	}

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
				logger.info("Retrieved token " + tokens[counter]);
				String[] splits = tokens[counter].split(" ");
				tokenMap.put(splits[0], splits[1]);
			}
			counter++;
		}
		return tokenMap;
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		/*
		 * String readLines = IOUtils .toString( new FileInputStream(
		 * "G:/dev/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-13.txt"
		 * ), "UTF-8"); // new BasicLogDataPersister().persistLogData("blah2",
		 * readLines); // new BasicLogDataPersister().parseLogMessage(); new
		 * BasicLogDataPersister() .parseTokens( Arrays.asList("guid",
		 * "userId"),
		 * "127.0.0.1 - - [13/Jun/2015:21:16:29 +0530] guid=ssdsd-dsds userId=someName-blah \"GET /log-indexer-searcher-webapp/log/retrieveAvailableLogs HTTP/1.1\" 200 146"
		 * );
		 */
	}

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
