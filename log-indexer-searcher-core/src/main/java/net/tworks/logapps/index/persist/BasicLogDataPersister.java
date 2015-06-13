/**
 * 
 */
package net.tworks.logapps.index.persist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import net.tworks.logapps.admin.parser.LogPatternLayoutParser;
import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.model.SourceDTO;
import net.tworks.logapps.index.watch.FileWatcher;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 *
 *         A default implementation to persist the given Log file data to a
 *         Database.
 */
@Component
public class BasicLogDataPersister implements LogDataPersister, Observer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JdbcTemplate jdbcTemplate = DataSourceManager.getInstance()
			.getJdbcTemplate();

	private static final String ORACLE_TIMESTAMP_FORMAT = "dd-MMM-YY hh.mm.ss.SSS a XXX";

	@Autowired
	private FileWatcher fileWatcher;

	private final String sqlForGettingSourcePatternLayout = "select source_type, pattern_layout from source_metadata where source = ?";

	private final String sqlForInsertingRawData = "insert into raw_event (raw_event_data, event_timestamp, source_type) values(?, ?, ?)";

	@Override
	public void persistLogData(String source, String logContents) {
		// 1. Retrieve the pattern_layout from the table source_metadata for the
		// given source.

		String[] sourceMetadata = jdbcTemplate.queryForObject(
				sqlForGettingSourcePatternLayout, new String[] { source },
				new RowMapper<String[]>() {

					@Override
					public String[] mapRow(ResultSet resulSet, int rowNum)
							throws SQLException {
						String[] strings = new String[2];
						strings[0] = resulSet.getString(1);
						strings[1] = resulSet.getString(2);
						return strings;
					}

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

			try {
				jdbcTemplate.update(sqlForInsertingRawData, new Object[] {
						line, oracleTimeStampValue, sourceType });
				if (logger.isDebugEnabled()) {
					logger.debug("Successfully copied log line to raw_event table.");
				}
			} catch (DataAccessException e) {
				logger.error(
						"Error inserting log content to raw_event table. cause is {}.",
						e);
			}

		}

	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		String readLines = IOUtils
				.toString(
						new FileInputStream(
								"G:/dev/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-13.txt"),
						"UTF-8");
		new BasicLogDataPersister().persistLogData("blah2", readLines);
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
			logger.warn("Discarded a non-SourceDTO {} event update.", arg);
		}

	}

}
