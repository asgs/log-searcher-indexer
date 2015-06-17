/**
 * 
 */
package net.tworks.logapps.common.database;

import javax.annotation.PreDestroy;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;
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
	private static final String TOMCAT_JNDI_PREFIX = "java:/comp/env";
	private static final String JNDI_NAME = "jdbc/searchdb";
	private static JdbcTemplate jdbcTemplate;
	/*
	 * @Autowired
	 * 
	 * @Qualifier("searchDBDataSource")
	 */
	private DataSource dataSource;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public DataSourceManager() {
		init();
	}

	/**
	 * Initializes the dataSource and jdbcTemplate instances.
	 */
	public void init() {
		// TODO - try to replace this piece with Spring configuration.
		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext
					.lookup(TOMCAT_JNDI_PREFIX);
			dataSource = (DataSource) envContext.lookup(JNDI_NAME);
			jdbcTemplate = new JdbcTemplate(dataSource);
			logger.info("Created jdbcTemplate instance.");
		} catch (NamingException e) {
			logger.error(MarkerFactory.getMarker("FATAL"),
					"Unable to create jdbcTemplate instance. Cause is {}.", e);
		}
	}

	/*
	 * @PostConstruct public void initJdbcTemplate() { jdbcTemplate = new
	 * JdbcTemplate(dataSource); logger.info("Created jdbcTemplate instance.");
	 * }
	 */

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
