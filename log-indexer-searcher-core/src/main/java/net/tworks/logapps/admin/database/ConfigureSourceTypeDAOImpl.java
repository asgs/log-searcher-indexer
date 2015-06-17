/**
 * 
 */
package net.tworks.logapps.admin.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.database.exception.DatabaseConfigurationException;
import net.tworks.logapps.common.model.SourceTypeConfiguration;
import net.tworks.logapps.index.watch.FileWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

	@Autowired
	private FileWatcher fileWatcher;

	@Autowired
	private DataSourceManager dataSourceManager;

	/**
	 * This query will add a row to map the search index and the source type.
	 */
	private final String sqlForIndexMapping = "insert into index_mapping (search_index, source_type) values (?, ?)";

	/**
	 * This query will add a row to map the source type and the actual source
	 * (or log file).
	 */
	private final String sqlForSourceMapping = "insert into source_mapping (source_type, source) values (?, ?)";

	/**
	 * This query adds a row to record all meta-data about a particular source.
	 */
	private final String sqlForSourceMetadata = "insert into source_metadata (source_type, source, pattern_layout, timestamp_format) values (?, ?, ?, ?)";

	/**
	 * This query will alter the structured_event to add new columns
	 * corresponding to the key-values discovered from the log pattern layout.
	 */
	private StringBuilder sqlForAlterTableStructuredEvent = new StringBuilder(
			"alter table structured_event");

	/**
	 * Configures the new source type in the database.
	 * 
	 * @param sourceTypeConfiguration
	 * @return whether the operation was successful or not.
	 */
	@Transactional
	public boolean configureNewSourceType(
			SourceTypeConfiguration sourceTypeConfiguration)
			throws DatabaseConfigurationException {

		JdbcTemplate jdbcTemplate = dataSourceManager.getJdbcTemplate();

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
		if (!tokens.isEmpty()) {
			tokens.stream().forEach(token -> {
				sqlForAlterTableStructuredEvent.append(" add ");
				sqlForAlterTableStructuredEvent.append(token);
				sqlForAlterTableStructuredEvent.append(" varchar2(100)");
			});
			try {
				// We can also check if the column already exists.
				// SELECT column_name FROM USER_TAB_COLUMNS WHERE table_name =
				// 'structured_event'
				jdbcTemplate
						.execute(sqlForAlterTableStructuredEvent.toString());
			} catch (DataAccessException dataAccessException) {
				logger.warn(
						"Error altering table structured_event; cause is {}",
						dataAccessException);
			}

		}

		fileWatcher.watchOutForChanges(sourceTypeConfiguration.getSource());

		return true;
	}

	/**
	 * This method adds a row to map the search index and the source type.
	 * 
	 * @param jdbcTemplate
	 *            Spring's instance to do database transactions.
	 * @param sourceTypeConfiguration
	 *            The model object representing the source.
	 * @return whether the operation was successful.
	 */
	@Transactional
	public boolean configureIndexMapping(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {
		return jdbcTemplate.execute(sqlForIndexMapping, (
				PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceIndex());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setQueryTimeout(20);
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

	/**
	 * This method adds a row to map the source type and the actual source (or
	 * log file).
	 * 
	 * @param jdbcTemplate
	 *            Spring's instance to do database transactions.
	 * @param sourceTypeConfiguration
	 *            The model object representing the source.
	 * @return whether the operation was successful.
	 */
	@Transactional
	public boolean configureSourceMapping(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {

		return jdbcTemplate.execute(sqlForSourceMapping, (
				PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getSource());
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

	/**
	 * This method adds a row to record all meta-data about a particular source.
	 * 
	 * @param jdbcTemplate
	 *            Spring's instance to do database transactions.
	 * @param sourceTypeConfiguration
	 *            The model object representing the source.
	 * @return whether the operation was successful.
	 */
	@Transactional
	public boolean configureSourceMetadata(JdbcTemplate jdbcTemplate,
			final SourceTypeConfiguration sourceTypeConfiguration) {

		return jdbcTemplate.execute(sqlForSourceMetadata, (
				PreparedStatement preparedStatement) -> {
			try {
				preparedStatement.setString(1,
						sourceTypeConfiguration.getSourceType());
				preparedStatement.setString(2,
						sourceTypeConfiguration.getSource());
				preparedStatement.setString(3,
						sourceTypeConfiguration.getLogPatternlayout());
				preparedStatement.setString(4,
						sourceTypeConfiguration.getTimeStampFormat());
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
