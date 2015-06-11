/**
 * 
 */
package net.tworks.logapps.rest.model;

/**
 * @author asgs
 * 
 */
public class SourceType {

	private String source;

	private String sourceType;

	public SourceType(String source, String sourceType) {
		this.source = source;
		this.sourceType = sourceType;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public void setSource(String source) {
		this.source = source;
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
