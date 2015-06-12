/**
 * 
 */
package net.tworks.logapps.common.model;

/**
 * @author asgs
 *
 *         A Data Transfer Object for the Database layer, representing a Source
 *         File (or a Log file).
 */
public class SourceDTO {

	private String source;

	private String sourceContents;

	public SourceDTO(String source, String sourceContents) {
		this.source = source;
		this.sourceContents = sourceContents;
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
	 * @return the sourceContents
	 */
	public String getSourceContents() {
		return sourceContents;
	}

	/**
	 * @param sourceContents
	 *            the sourceContents to set
	 */
	public void setSourceContents(String sourceContents) {
		this.sourceContents = sourceContents;
	}

}
