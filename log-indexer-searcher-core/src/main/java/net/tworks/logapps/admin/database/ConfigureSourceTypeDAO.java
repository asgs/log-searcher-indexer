/**
 * 
 */
package net.tworks.logapps.admin.database;

import net.tworks.logapps.common.model.SourceTypeConfiguration;

/**
 * @author asgs
 * 
 *         This interface declares that the implementers should define mechanism
 *         to persist the log source type's configuration to the database. This
 *         is an administration operation that is done once for every new log
 *         type to be indexed.
 */
public interface ConfigureSourceTypeDAO {

	/**
	 * Configures the new source type in the database.
	 * 
	 * @param sourceTypeConfiguration
	 * @return whether the operation was successful or not.
	 */
	boolean configureNewSourceType(
			SourceTypeConfiguration sourceTypeConfiguration);

}
