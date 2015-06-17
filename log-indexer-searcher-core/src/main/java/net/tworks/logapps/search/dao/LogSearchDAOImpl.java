/**
 * 
 */
package net.tworks.logapps.search.dao;

import static net.tworks.logapps.common.util.Constants.ORACLE_TIMESTAMP_FORMAT;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

	private final String sqlForSearchWithIndexOnly = "select raw_event_data from raw_event where upper(raw_event_data) like ? and source_type IN (select source_type from index_mapping where upper(search_index) = ?)";

	private final String sqlForSearchWithSourceType = "select raw_event_data from raw_event where upper(source_type) = ? and upper(raw_event_data) like ?";

	private final String sqlForRawSearch = "select raw_event_data from raw_event where upper(raw_event_data) like ?";

	private final String sqlForRawSearchWithTime = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ?";

	private final String sqlForSearchWithTimeAndIndex = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ? and source_type IN (select source_type from index_mapping where upper(search_index) = ?)";

	private final String sqlForSearchWithTimeAndSourceType = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ? and upper(source_type) = ?";

	private final String sqlForSearchWithSourceTypeAndOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and upper(source_type) = ? and event_id IN (select event_id from structured_event where";

	private final String sqlForSearchWithIndexAndOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and upper(source_type) IN (select upper(source_type) from index_mapping where upper(search_index) = ?) and event_id IN (select event_id from structured_event where";

	private final String sqlForSearchWithOnlyOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_id IN (select event_id from structured_event where";

	private final String sqlForSearchWithTimeSourceTypeAndOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ? and upper(source_type) = ? and event_id IN (select event_id from structured_event where";

	private final String sqlForSearchWithTimeIndexAndOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ? and upper(source_type) IN (select upper(source_type) from index_mapping where upper(search_index) = ?) and event_id IN (select event_id from structured_event where";

	private final String sqlForSearchWithTimeOnlyOtherKeys = "select raw_event_data from raw_event where upper(raw_event_data) like ? and event_timestamp > ? and event_id IN (select event_id from structured_event where";

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
		String uppedRawQuery = rawQuery.toUpperCase();
		List<String> queryForList = jdbcTemplate.queryForList(sqlForRawSearch,
				new Object[] { "%" + uppedRawQuery + "%" }, String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	/**
	 * A quick utility to see if a particular key is present in the given search
	 * query.
	 * 
	 * @param keyValues
	 *            The collection of key-values.
	 * @param key
	 *            The key to check for.
	 * @return True/False
	 */
	private boolean contains(List<SearchKeyValue> keyValues, String key) {
		return keyValues.stream().anyMatch(
				keyValue -> keyValue.getKey().equalsIgnoreCase(key));
	}

	@Override
	public String[] searchByKeysWithMainQuery(SearchKeyValue[] keyValues,
			String mainQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering {} to query with keyValues and mainQuery.",
					getClass().getSimpleName());
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		if (kvList.size() > 2 && contains(kvList, "source_type")
				&& contains(kvList, "index")) {
			return sqlForSearchWithSourceTypeAndOtherKeys(keyValues, mainQuery);
		} else if (kvList.size() >= 2 && !contains(kvList, "source_type")
				&& contains(kvList, "index")) {
			return sqlForSearchWithIndexAndOtherKeys(keyValues, mainQuery);
		} else if (kvList.size() >= 1 && !contains(kvList, "source_type")
				&& !contains(kvList, "index")) {
			return sqlForSearchWithOnlyOtherKeys(keyValues, mainQuery);
		} else {
			List<String> queryForList = null;
			boolean indexFound = false;
			boolean sourceTypeFound = false;
			String sourceType = null;
			String searchIndex = null;
			for (SearchKeyValue keyValue : kvList) {
				if (keyValue.getKey().equalsIgnoreCase("source_type")) {
					sourceTypeFound = true;
					sourceType = keyValue.getValue();
					break;
				} else if (keyValue.getKey().equalsIgnoreCase("index")) {
					indexFound = true;
					searchIndex = keyValue.getValue();
				}
			}
			String uppedMainQuery = mainQuery.toUpperCase();
			if (sourceTypeFound) {
				String uppedSourceType = sourceType.toUpperCase();
				queryForList = jdbcTemplate.queryForList(
						sqlForSearchWithSourceType, new Object[] {
								uppedSourceType, "%" + uppedMainQuery + "%" },
						String.class);
			} else if (indexFound) {
				String uppedSearchIndex = searchIndex.toUpperCase();
				queryForList = jdbcTemplate.queryForList(
						sqlForSearchWithIndexOnly, new Object[] {
								"%" + uppedMainQuery + "%", uppedSearchIndex },
						String.class);
			} else {
				return searchByRawQuery(mainQuery);
			}

			return queryForList.toArray(new String[queryForList.size()]);
		}
	}

	private String[] sqlForSearchWithSourceTypeAndOtherKeys(
			SearchKeyValue[] keyValues, String mainQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithSourceTypeAndOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mainQuery.toUpperCase());

		String finalQuery = sqlForSearchWithSourceTypeAndOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("source_type")) {
				String sourceType = keyValue.getValue();
				String uppedSourceType = sourceType.toUpperCase();
				objectList.add(uppedSourceType);
				break;
			}
		}

		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}
		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	private String[] sqlForSearchWithIndexAndOtherKeys(
			SearchKeyValue[] keyValues, String mainQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithIndexAndOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mainQuery.toUpperCase());

		String finalQuery = sqlForSearchWithIndexAndOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("index")) {
				String index = keyValue.getValue();
				String uppedIndex = index.toUpperCase();
				objectList.add(uppedIndex);
				break;
			}
		}

		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}

		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	private String[] sqlForSearchWithOnlyOtherKeys(SearchKeyValue[] keyValues,
			String mainQuery) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithOnlyOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add("%" + mainQuery.toUpperCase() + "%");
		String finalQuery = sqlForSearchWithOnlyOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}
		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
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
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				ORACLE_TIMESTAMP_FORMAT);
		oracleTimeStampValue = dateFormat.format(new Date(epochMilli));
		logger.info("Query will search for events happened at or later {}.",
				oracleTimeStampValue);
		String uppedRawQuery = rawQuery.toUpperCase();
		List<String> queryForList = jdbcTemplate.queryForList(
				sqlForRawSearchWithTime, new Object[] {
						"%" + uppedRawQuery + "%", oracleTimeStampValue },
				String.class);
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

		LocalDateTime localDateTime = LocalDateTime.now().minus(timeDuration,
				timeUnit);
		long epochMilli = localDateTime.atZone(ZoneId.systemDefault())
				.toInstant().toEpochMilli();
		String oracleTimeStampValue = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				ORACLE_TIMESTAMP_FORMAT);
		oracleTimeStampValue = dateFormat.format(new Date(epochMilli));
		logger.info("Query will search for events happened at or later {}.",
				oracleTimeStampValue);

		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		if (kvList.size() > 2 && contains(kvList, "source_type")
				&& contains(kvList, "index")) {
			return sqlForSearchWithTimeSourceTypeAndOtherKeys(keyValues,
					mainQuery, oracleTimeStampValue);
		} else if (kvList.size() >= 2 && !contains(kvList, "source_type")
				&& contains(kvList, "index")) {
			return sqlForSearchWithTimeIndexAndOtherKeys(keyValues, mainQuery,
					oracleTimeStampValue);
		} else if (kvList.size() >= 1 && !contains(kvList, "source_type")
				&& !contains(kvList, "index")) {
			return sqlForSearchWithTimeOnlyOtherKeys(keyValues, mainQuery,
					oracleTimeStampValue);
		} else {
			List<String> queryForList = null;
			boolean indexFound = false;
			boolean sourceTypeFound = false;
			String sourceType = null;
			String searchIndex = null;
			for (SearchKeyValue keyValue : kvList) {
				if (keyValue.getKey().equalsIgnoreCase("source_type")) {
					sourceTypeFound = true;
					sourceType = keyValue.getValue();
					break;
				} else if (keyValue.getKey().equalsIgnoreCase("index")) {
					indexFound = true;
					searchIndex = keyValue.getValue();
				}
			}

			String uppedMainQuery = mainQuery.toUpperCase();
			if (sourceTypeFound) {
				String uppedSourceType = sourceType.toUpperCase();
				queryForList = jdbcTemplate.queryForList(
						sqlForSearchWithTimeAndSourceType, new Object[] {
								"%" + uppedMainQuery + "%",
								oracleTimeStampValue, uppedSourceType },
						String.class);
			} else if (indexFound) {
				String uppedSearchIndex = searchIndex.toUpperCase();
				queryForList = jdbcTemplate.queryForList(
						sqlForSearchWithTimeAndIndex, new Object[] {
								"%" + uppedMainQuery + "%",
								oracleTimeStampValue, uppedSearchIndex },
						String.class);
			} else {
				return searchByRawQueryForAGivenTimeFrame(mainQuery,
						timeDuration, timeUnit);
			}
			return queryForList.toArray(new String[queryForList.size()]);
		}
	}

	private String[] sqlForSearchWithTimeSourceTypeAndOtherKeys(
			SearchKeyValue[] keyValues, String mainQuery,
			String oracleTimeStampValue) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithTimeSourceTypeAndOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mainQuery.toUpperCase());
		objectList.add(oracleTimeStampValue);
		String finalQuery = sqlForSearchWithTimeSourceTypeAndOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("source_type")) {
				String sourceType = keyValue.getValue();
				String uppedSourceType = sourceType.toUpperCase();
				objectList.add(uppedSourceType);
				break;
			}
		}

		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}
		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	private String[] sqlForSearchWithTimeIndexAndOtherKeys(
			SearchKeyValue[] keyValues, String mainQuery,
			String oracleTimeStampValue) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithTimeIndexAndOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add(mainQuery.toUpperCase());

		objectList.add(oracleTimeStampValue);
		String finalQuery = sqlForSearchWithTimeIndexAndOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("index")) {
				String index = keyValue.getValue();
				String uppedIndex = index.toUpperCase();
				objectList.add(uppedIndex);
				break;
			}
		}

		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}

		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	private String[] sqlForSearchWithTimeOnlyOtherKeys(
			SearchKeyValue[] keyValues, String mainQuery,
			String oracleTimeStampValue) {
		if (logger.isDebugEnabled()) {
			logger.debug("Entering sqlForSearchWithTimeOnlyOtherKeys.");
		}
		List<SearchKeyValue> kvList = Arrays.asList(keyValues);
		List<Object> objectList = new ArrayList<Object>();
		objectList.add("%" + mainQuery.toUpperCase() + "%");
		objectList.add(oracleTimeStampValue);
		String finalQuery = sqlForSearchWithTimeOnlyOtherKeys;
		for (SearchKeyValue keyValue : kvList) {
			if (keyValue.getKey().equalsIgnoreCase("log_level")) {
				finalQuery += " upper(log_level) like ?";
				String logLevel = keyValue.getValue();
				String uppedLogLevel = logLevel.toUpperCase();
				objectList.add("%" + uppedLogLevel + "%");
			} else if (keyValue.getKey().equalsIgnoreCase("thread_name")) {
				if (!finalQuery.endsWith("where")) {
					finalQuery += " and";
				}
				finalQuery += " upper(thread_name) like ?";
				String threadName = keyValue.getValue();
				String uppedThreadName = threadName.toUpperCase();
				objectList.add("%" + uppedThreadName + "%");
			}
		}
		finalQuery += ")";
		List<String> queryForList = jdbcTemplate
				.queryForList(finalQuery,
						objectList.toArray(new Object[objectList.size()]),
						String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

}
