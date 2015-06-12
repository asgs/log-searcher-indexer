/**
 * 
 */
package net.tworks.logapps.index.persist;

import java.util.Observer;

/**
 * @author asgs
 *
 *         A generic interface which defines the contract as to how the Log file
 *         data are to be persisted to, say a Database or flat file system.
 */
public interface LogDataPersister extends Observer {

	/**
	 * This method allows the caller to persist the log content to the database.
	 * 
	 * @param source
	 *            - The name of the source whose contents to be persisted.
	 * @param logContents
	 *            - The content string which will be persisted.
	 */
	public void persistLogData(String source, String logContents);

}
