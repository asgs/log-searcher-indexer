/**
 * 
 */
package net.tworks.logapps.common.database.exception;

/**
 * @author asgs
 * 
 *         A generic exception indicating something went wrong with accessing
 *         the database.
 * 
 */
public class DatabaseConfigurationException extends Exception {

	/**
	 * Generated Serial Version UID.
	 */
	private static final long serialVersionUID = 432338453858927807L;

	public DatabaseConfigurationException() {
		super();
	}

	public DatabaseConfigurationException(String message) {
		super(message);
	}

	public DatabaseConfigurationException(Throwable throwable) {
		super(throwable);
	}

}
