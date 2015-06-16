/**
 * 
 */
package net.tworks.logapps.search.dao;

import static net.tworks.logapps.common.util.Constants.ORACLE_TIMESTAMP_FORMAT;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.model.SearchKeyValue;
import net.tworks.logapps.index.watch.FileWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 * 
 *         Implements the common search methods.
 *
 */
@Component
public class LogSearchDAOImpl implements LogSearchDAO {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileWatcher fileWatcher;

	@Autowired
	private DataSourceManager dataSourceManager;

	private JdbcTemplate jdbcTemplate;

	private final String sqlForSearchWithIndexOnly = "select raw_event_data from raw_event where raw_event_data like ? and source_type IN (select source_type from index_mapping where search_index = ?)";

	private final String sqlForSearchWithSourceType = "select raw_event_data from raw_event where source_type = ? and raw_event_data like ?";

	private final String sqlForRawSearch = "select raw_event_data from raw_event where raw_event_data like ?";

	private final String sqlForSearchWithKeys = "";

	private final String sqlForRawSearchWithTime = "select raw_event_data from raw_event where raw_event_data like ? and event_timestamp > ?";

	private final String sqlForSearchWithTimeAndIndex = "select raw_event_data from raw_event where raw_event_data like ? and event_timestamp > ? and source_type IN (select source_type from index_mapping where search_index = ?)";

	private final String sqlForSearchWithTimeAndSourceType = "select raw_event_data from raw_event where raw_event_data like ? and event_timestamp > ? and source_type = ?";

	@PostConstruct
	public void setJdbcTemplate() {
		jdbcTemplate = dataSourceManager.getJdbcTemplate();
	}

	@Override
	public String[] searchByRawQuery(String rawQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering {} to query with rawQuery.", getClass()
					.getSimpleName());
		}
		List<String> queryForList = jdbcTemplate.queryForList(sqlForRawSearch,
				new Object[] { "%" + rawQuery + "%" }, String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	@Override
	public String[] searchByKeysWithMainQuery(SearchKeyValue[] keyValues,
			String mainQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering {} to query with keyValues and mainQuery.",
					getClass().getSimpleName());
		}
		List<SearchKeyValue> asList = Arrays.asList(keyValues);
		List<String> queryForList = null;
		boolean indexFound = false;
		boolean sourceTypeFound = false;
		String sourceType = null;
		String searchIndex = null;
		for (SearchKeyValue keyValue : asList) {
			if (keyValue.getKey().equalsIgnoreCase("source_type")) {
				sourceTypeFound = true;
				sourceType = keyValue.getValue();
				break;
			} else if (keyValue.getKey().equalsIgnoreCase("index")) {
				indexFound = true;
				searchIndex = keyValue.getValue();
			}
		}

		if (sourceTypeFound) {
			queryForList = jdbcTemplate.queryForList(
					sqlForSearchWithSourceType, new Object[] { sourceType,
							"%" + mainQuery + "%" }, String.class);
		} else if (indexFound) {
			queryForList = jdbcTemplate.queryForList(sqlForSearchWithIndexOnly,
					new Object[] { "%" + mainQuery + "%", searchIndex },
					String.class);
		} else {
			return searchByRawQuery(mainQuery);
		}

		return queryForList.toArray(new String[queryForList.size()]);
	}

	@Override
	public String[] searchByRawQueryForAGivenTimeFrame(String rawQuery,
			long timeDuration, ChronoUnit timeUnit) {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Entering {} to query with rawQuery, timeDuration, and timeUnit.",
					getClass().getSimpleName());
		}
		// Powerful Java 8 API inspired from Joda library.
		LocalDateTime localDateTime = LocalDateTime.now().minus(timeDuration,
				timeUnit);
		long epochMilli = localDateTime.atZone(ZoneId.systemDefault())
				.toInstant().toEpochMilli();
		String oracleTimeStampValue = null;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(
				ORACLE_TIMESTAMP_FORMAT);
		oracleTimeStampValue = dateFormat2.format(new Date(epochMilli));
		logger.info("Query will search for events happened at or later {}.",
				oracleTimeStampValue);
		List<String> queryForList = jdbcTemplate.queryForList(
				sqlForRawSearchWithTime, new Object[] { "%" + rawQuery + "%",
						oracleTimeStampValue }, String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	@Override
	public String[] searchByKeysWithMainQueryForAGivenTimeFrame(
			SearchKeyValue[] keyValues, String mainQuery, long timeDuration,
			ChronoUnit timeUnit) {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Entering {} to query with keyValues, mainQuery, and timeDuration.",
					getClass().getSimpleName());
		}

		List<SearchKeyValue> asList = Arrays.asList(keyValues);
		List<String> queryForList = null;
		boolean indexFound = false;
		boolean sourceTypeFound = false;
		String sourceType = null;
		String searchIndex = null;
		for (SearchKeyValue keyValue : asList) {
			if (keyValue.getKey().equalsIgnoreCase("source_type")) {
				sourceTypeFound = true;
				sourceType = keyValue.getValue();
				break;
			} else if (keyValue.getKey().equalsIgnoreCase("index")) {
				indexFound = true;
				searchIndex = keyValue.getValue();
			}
		}

		LocalDateTime localDateTime = LocalDateTime.now().minus(timeDuration,
				timeUnit);
		long epochMilli = localDateTime.atZone(ZoneId.systemDefault())
				.toInstant().toEpochMilli();
		String oracleTimeStampValue = null;
		SimpleDateFormat dateFormat2 = new SimpleDateFormat(
				ORACLE_TIMESTAMP_FORMAT);
		oracleTimeStampValue = dateFormat2.format(new Date(epochMilli));
		logger.info("Query will search for events happened at or later {}.",
				oracleTimeStampValue);

		if (sourceTypeFound) {
			queryForList = jdbcTemplate.queryForList(
					sqlForSearchWithTimeAndSourceType, new Object[] {
							"%" + mainQuery + "%", oracleTimeStampValue,
							sourceType }, String.class);
		} else if (indexFound) {
			queryForList = jdbcTemplate.queryForList(
					sqlForSearchWithTimeAndIndex, new Object[] {
							"%" + mainQuery + "%", oracleTimeStampValue,
							searchIndex }, String.class);
		} else {
			return searchByRawQueryForAGivenTimeFrame(mainQuery, timeDuration,
					timeUnit);
		}
		return queryForList.toArray(new String[queryForList.size()]);
	}

}
