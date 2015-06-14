/**
 * 
 */
package net.tworks.logapps.common.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 * 
 *         Single class to manage all database related operations. It internally
 *         delegates the connection open/close/pooling to the Spring JDBC API.
 *         It's a singleton implementation, so <code>getInstance()</code> will
 *         have to called by the callers.
 * 
 */
@Component
public class DataSourceManager {
	private static DataSourceManager INSTANCE;
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

	public void init() {
		// TODO - try to replace this piece with Spring configuration.
		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup(JNDI_NAME);
			jdbcTemplate = new JdbcTemplate(dataSource);
			logger.info("Created jdbcTemplate instance.");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * @PostConstruct public void initJdbcTemplate() { jdbcTemplate = new
	 * JdbcTemplate(dataSource); logger.info("Created jdbcTemplate instance.");
	 * }
	 */

	public static DataSourceManager getInstance() {
		return INSTANCE;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void destroy() {
		if (dataSource != null) {
			dataSource = null;
		}
		if (jdbcTemplate != null) {
			jdbcTemplate = null;
		}
	}

}
