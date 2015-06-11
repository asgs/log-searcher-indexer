/**
 * 
 */
package net.tworks.logapps.admin.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import net.tworks.logapps.common.database.DBTableManager;
import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.database.exception.DatabaseConfigurationException;
import net.tworks.logapps.common.model.SourceTypeConfiguration;

/**
 * @author asgs
 * 
 *         An implementation persisting the configuration onto an Oracle
 *         database instance.
 * 
 */
@Component
public class ConfigureSourceTypeDAOImpl implements ConfigureSourceTypeDAO {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private DBTableManager dbTableManager;

	private final String sqlForIndexMapping = "insert into index_mapping (search_index, source_type) values (?, ?)";

	private final String sqlForSourceMapping = "insert into source_mapping (source_type, source) values (?, ?)";

	private final String sqlForSourceMetadata = "insert into source_metadata (source_type, file_path) values (?, ?)";

	private StringBuilder sqlForAlterTableStructuredEvent = new StringBuilder(
			"alter table structured_event");

	/**
	 * Configures the new source type in the database.
	 * 
	 * @param sourceTypeConfiguration
	 * @return whether the operation was successful or not.
	 */
	public boolean configureNewSourceType(
			SourceTypeConfiguration sourceTypeConfiguration)
			throws DatabaseConfigurationException {

		JdbcTemplate jdbcTemplate = DataSourceManager.getInstance()
				.getJdbcTemplate();

		// Check if source index is present. if present, need to add a row in
		// the index_mapping table for this source_type.
		// dbTableManager.insertRow("insert new source_index and source_type");
		if (sourceTypeConfiguration.getSourceIndex() != null) {
			if (!configureIndexMapping(jdbcTemplate, sourceTypeConfiguration)) {
				throw new DatabaseConfigurationException(
						"Failed to configure the index_mapping table.");
			}
		}

		// add a new row to the source_mapping table to map the source_type and
		// source it's coming from.
		if (!configureSourceMapping(jdbcTemplate, sourceTypeConfiguration)) {
			throw new DatabaseConfigurationException(
					"Failed to configure the source_mapping table.");
		}

		// add a new row to the source_metadata table to infuse information
		// related to this source_type.
		if (!configureSourceMetadata(jdbcTemplate, sourceTypeConfiguration)) {
			throw new DatabaseConfigurationException(
					"Failed to configure the source_metadata table.");
		}

		// If there are tokens discovered in the log file pattern layout, alter
		// the table structured_event to dynamically add corresponding columns.

		List<String> tokens = sourceTypeConfiguration.getTokens();
		for (String token : tokens) {
			sqlForAlterTableStructuredEvent.append(" add ");
			sqlForAlterTableStructuredEvent.append(token);
			sqlForAlterTableStructuredEvent.append(" varchar2(100)");
		}

		try {
			//TODO Check if the column already exists.
			//SELECT column_name FROM USER_TAB_COLUMNS WHERE table_name = 'structured_event'
			jdbcTemplate.execute(sqlForAlterTableStructuredEvent.toString());
		} catch (DataAccessException dataAccessException) {
			logger.error("Error altering table structured_event; cause is {}", dataAccessException);
		}

		return true;
	}

	private boolean configureIndexMapping(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {
		logger.info("Going to configure index_mapping table.");
		return jdbcTemplate.execute(sqlForIndexMapping, (PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceIndex());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setQueryTimeout(20);
				logger.info("About to configure index_mapping table.");
				preparedStatement.execute();
				logger.info("Configured index_mapping table.");
			} catch (SQLException sqlException) {
				logger.error(
						"Exception configuring index_mapping table. Cause;",
						sqlException);
				return false;
			}
			return true;
		});
		
		
	}

	private boolean configureSourceMapping(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {

		return jdbcTemplate.execute(sqlForSourceMapping, (PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getLogLocation());
				preparedStatement.execute();
				logger.info("Configured source_mapping table.");
			} catch (SQLException sqlException) {
				logger.error(
						"Exception configuring source_mapping table. Cause;",
						sqlException);
				return false;
			}
			return true;
		});
	}

	private boolean configureSourceMetadata(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {

		return jdbcTemplate.execute(sqlForSourceMetadata, (PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getLogLocation());
				preparedStatement.execute();
				logger.info("Configured source_metadata table.");
			} catch (SQLException sqlException) {
				logger.error(
						"Exception configuring source_metadata table. Cause;",
						sqlException);
				return false;
			}
			return true;
		});
	}

}
