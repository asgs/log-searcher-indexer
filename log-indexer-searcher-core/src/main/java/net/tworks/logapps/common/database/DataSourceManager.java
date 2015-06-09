/**
 * 
 */
package net.tworks.logapps.common.database;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author asgs
 * 
 *         Single class to manage all database related operations. It internally
 *         delegates the connection open/close/pooling to the Spring JDBC API.
 *         It's a singleton implementation, so <code>getInstance()</code> will
 *         have to called by the callers.
 * 
 */
public class DataSourceManager {
	private static final DataSourceManager INSTANCE = new DataSourceManager();
	private static final String JNDI_NAME = "jdbc/searchdb";
	private static JdbcTemplate jdbcTemplate;
	private static DataSource dataSource;

	private DataSourceManager() {
		// Prevent instantiation.
	}

	public void init() {
		Context initContext;
		try {
			initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup(JNDI_NAME);
			jdbcTemplate = new JdbcTemplate(dataSource);
			System.out.println("Created jdbcTemplate.");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
