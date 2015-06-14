/**
 * 
 */
package net.tworks.logapps.search.dao;

import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.model.SearchKeyValue;
import net.tworks.logapps.index.watch.FileWatcher;

/**
 * @author asgs
 * 
 *         A unified interface to query for search results given various
 *         combinations the user has control over.
 *
 */
@Component
public class LogSearchDAOImpl implements LogSearchDAO {

	@Autowired
	private FileWatcher fileWatcher;

	@Autowired
	private DataSourceManager dataSourceManager;

	private JdbcTemplate jdbcTemplate;

	private final String sqlForSearchWithIndexOnly = "select raw_event_data from raw_event where raw_event_data like ? source_type IN (select source_type from index_mapping where search_index='?');";

	private final String sqlForSearchWithSourceType = "select raw_event_data from raw_event where source_type = ? and where raw_event_data like ? ";

	private final String sqlForRawSearch = "select raw_event_data from raw_event where raw_event_data like ?";

	private final String sqlForSearchWithKeys = "";

	@PostConstruct
	public void setJdbcTemplate() {
		jdbcTemplate = dataSourceManager.getJdbcTemplate();
	}

	@Override
	public String[] searchByRawQuery(String rawQuery) {
		List<String> queryForList = jdbcTemplate.queryForList(sqlForRawSearch,
				new Object[] { "%" + rawQuery + "%" }, String.class);
		return queryForList.toArray(new String[queryForList.size()]);
	}

	@Override
	public String[] searchByKeysWithRawQuery(SearchKeyValue[] keyValues,
			String mainQuery) {
		return null;
	}

	@Override
	public String[] searchByRawQueryForAGivenTimeFrame(String rawQuery,
			long timeDuration, ChronoUnit timeUnit) {
		return null;
	}

	@Override
	public String[] searchByKeysWithRawQueryForAGivenTimeFrame(
			SearchKeyValue[] keyValues, String mainQuery, long timeDuration,
			ChronoUnit timeUnit) {
		return null;
	}

}
