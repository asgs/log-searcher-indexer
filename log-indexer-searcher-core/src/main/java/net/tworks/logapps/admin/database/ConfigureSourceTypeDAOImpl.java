/**
 * 
 */
package net.tworks.logapps.admin.database;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Component;

import net.tworks.logapps.common.database.DBTableManager;
import net.tworks.logapps.common.database.DataSourceManager;
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

	private DBTableManager dbTableManager;
	
	private String sqlForSourceIndexMapping = "insert into index_mapping (search_index, source_type) values (?, ?)"; 

	/**
	 * Configures the new source type in the database.
	 * 
	 * @param sourceTypeConfiguration
	 * @return whether the operation was successful or not.
	 */
	public boolean configureNewSourceType(
			SourceTypeConfiguration sourceTypeConfiguration) {

		// Check if source index is present. if present, need to add a row in
		// the index_mapping table for this source_type.
		//dbTableManager.insertRow("insert new source_index and source_type");
		
		JdbcTemplate jdbcTemplate = DataSourceManager.getInstance().getJdbcTemplate();
		
		/*jdbcTemplate.execute(sqlForSourceIndexMapping, new PreparedStatementCallback<T>() {
		})*/

		// add a new row to the source_mapping table to map the source_type and
		// source it's coming from.

		// add a new row to the source_metadata table to infuse information
		// related to this source_type.

		return true;
	}

}
