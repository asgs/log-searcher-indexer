/**
 * 
 */
package net.tworks.logapps.rest.model;

/**
 * @author asgs
 * 
 */
public class SourceType {

	private String logLocation;

	private String sourceType;

	public SourceType(String logLocation, String sourceType) {
		this.logLocation = logLocation;
		this.sourceType = sourceType;
	}

	/**
	 * @return the logLocation
	 */
	public String getLogLocation() {
		return logLocation;
	}

	/**
	 * @param logLocation
	 *            the logLocation to set
	 */
	public void setLogLocation(String logLocation) {
		this.logLocation = logLocation;
	}

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @param sourceType
	 *            the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}
