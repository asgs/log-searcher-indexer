/**
 * 
 */
package net.tworks.logapps.rest.model;

/**
 * @author asgs
 * 
 *         Model class to represent the result of an Admin configuration action.
 * 
 */
public class ConfigurationResult {

	private Boolean result;

	private String message;

	/**
	 * @return the result
	 */
	public Boolean getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(Boolean result) {
		this.result = result;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
