/**
 * 
 */
package net.tworks.logapps.rest.model;

/**
 * @author asgs
 * 
 *         A model representing a source type a particular source belongs to.
 */
public class SourceType {

	/**
	 * The fully qualified name of the source file.
	 */
	private String source;

	/**
	 * An identifier to relate the type of source to.
	 */
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
