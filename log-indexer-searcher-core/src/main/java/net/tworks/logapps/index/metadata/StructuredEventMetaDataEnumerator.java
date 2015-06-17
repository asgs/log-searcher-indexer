/**
 * 
 */
package net.tworks.logapps.index.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import net.tworks.logapps.common.database.DataSourceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 * 
 *         A Utility to query and maintain the custom columns that are/were
 *         created on the fly to improve the log search. These columns are
 *         completely optional, hence we query for those that are null-able.
 *
 */
@Component
public class StructuredEventMetaDataEnumerator {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static List<String> structuredEventMetaData = new ArrayList<String>();

	private static final String QUERY_TO_GET_COLUMN_NAMES = "select COLUMN_NAME from ALL_TAB_COLUMNS where TABLE_NAME='STRUCTURED_EVENT' and NULLABLE='Y'";

	@Autowired
	private DataSourceManager dataSourceManager;

	private JdbcTemplate jdbcTemplate;

	@PostConstruct
	public void init() {
		jdbcTemplate = dataSourceManager.getJdbcTemplate();
		List<String> queryForList = jdbcTemplate.queryForList(
				QUERY_TO_GET_COLUMN_NAMES, String.class);
		structuredEventMetaData.addAll(queryForList);
		logger.info("Retrieved column names for the structured_event call.");
	}

	public List<String> retrieveMetadata() {
		return structuredEventMetaData;
	}

}