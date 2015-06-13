/**
 * 
 */
package net.tworks.logapps.rest.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tworks.logapps.common.database.DataSourceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asgs
 * 
 */
@RestController
@RequestMapping("/test")
public class TestDBService {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSourceManager dataSourceManager;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public void queryResults() {

		jdbcTemplate = dataSourceManager.getJdbcTemplate();
		/*
		 * jdbcTemplate .queryForObject(
		 * "select to_timestamp_tz(event_timestamp) from raw_event where event_id=?"
		 * , new Object[] { 2 }, new RowMapper<Object>() {
		 * 
		 * @Override public Object mapRow(ResultSet resultSet, int rowNum)
		 * throws SQLException { Date date = null; try { if (resultSet.next()) {
		 * Timestamp timestamp = resultSet .getTimestamp(2); date = new
		 * Date(timestamp .getTime()); logger.info("Date is {}.", date); } else
		 * { logger.info("No rows found."); }
		 * 
		 * } catch (SQLException e) { e.printStackTrace(); } return date; }
		 * 
		 * });
		 */

		Timestamp timestamp = jdbcTemplate.queryForObject(
				"select event_timestamp from raw_event where event_id=2",
				Timestamp.class);
		String timestampFormat = jdbcTemplate
				.queryForObject(
						"select sm.timestamp_format from source_metadata sm INNER JOIN raw_event re ON re.source_type = sm.source_type where re.event_id=2",
						String.class);
		Date date = new Date(timestamp.getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat(timestampFormat);
		logger.info("Date is {}.", dateFormat.format(date));
		System.out.println("Ran query. Printing results.");

	}
}
