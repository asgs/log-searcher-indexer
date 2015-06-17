/**
 * 
 */
package net.tworks.logapps.common.database;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 * 
 *         Single class to manage all database related operations. It internally
 *         delegates the connection open/close/pooling to the Spring JDBC API.
 * 
 */
@Component
public class DataSourceManager {
	private static JdbcTemplate jdbcTemplate;

	@Autowired
	@Qualifier("searchDBDataSource")
	private DataSource dataSource;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Initializes the jdbcTemplate instance.
	 */
	@PostConstruct
	public void initJdbcTemplate() {
		jdbcTemplate = new JdbcTemplate(dataSource);
		logger.info("Created jdbcTemplate instance.");
	}

	/**
	 * Returns an instance of the {@link DataSource} configured in the Web
	 * Container.
	 * 
	 * @return DataSource instance.
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * Returns an instance of the {@link JdbcTemplate} created with an instance
	 * of {@link DataSource}.
	 * 
	 * @return Instance of {@link JdbcTemplate}
	 */
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	/**
	 * Nulls out the Database objects to help easing the clearance of resources.
	 * Invoked just before the context gets destroyed.
	 */
	@PreDestroy
	public void destroy() {
		if (dataSource != null) {
			dataSource = null;
		}
		if (jdbcTemplate != null) {
			jdbcTemplate = null;
		}
	}

}
