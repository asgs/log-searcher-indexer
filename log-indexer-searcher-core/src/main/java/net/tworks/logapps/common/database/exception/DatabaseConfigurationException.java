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

	/**
	 * Default no-arg constructor.
	 */
	public DatabaseConfigurationException() {
		super();
	}

	/**
	 * Constructs an instance taking in the message passed in.
	 * 
	 * @param message
	 *            Message to be embedded.
	 */
	public DatabaseConfigurationException(String message) {
		super(message);
	}

	/**
	 * Constructs an instance taking in the Throwable as the new cause.
	 * 
	 * @param throwable
	 *            Throwable to be added as the cause.
	 */
	public DatabaseConfigurationException(Throwable throwable) {
		super(throwable);
	}

}
