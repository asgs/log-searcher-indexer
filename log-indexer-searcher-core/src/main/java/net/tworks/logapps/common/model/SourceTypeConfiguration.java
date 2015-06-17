/**
 * 
 */
package net.tworks.logapps.common.model;

import java.util.List;

/**
 * @author asgs
 * 
 *         Model class to represent the configuration required in adding a new
 *         source type or a log file.
 * 
 */
public class SourceTypeConfiguration {

	/**
	 * The search index of the source.
	 */
	private String sourceIndex;

	/**
	 * The type of the source.
	 */
	private String sourceType;

	/**
	 * The source - indicating the full path in the file system.
	 */
	private String source;

	/**
	 * THe pattern layout of the source (or log file).
	 */
	private String logPatternlayout;

	/**
	 * Key-values deciphered from the layout.
	 */
	private List<String> tokens;

	/**
	 * The format of the Timestamp the log content will have.
	 */
	private String timeStampFormat;

	/**
	 * @return the sourceIndex
	 */
	public String getSourceIndex() {
		return sourceIndex;
	}

	/**
	 * @param sourceIndex
	 *            the sourceIndex to set
	 */
	public void setSourceIndex(String sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	/**
	 * No-arg constructor allowing setting up the individual fields later.
	 */
	public SourceTypeConfiguration() {

	}

	/**
	 * The default constructor used to populate the individual fields.
	 * 
	 * @param sourceType
	 * @param source
	 * @param logPatternlayout
	 * @param sourceIndex
	 */
	public SourceTypeConfiguration(String sourceType, String source,
			String logPatternlayout, String sourceIndex) {
		this.sourceType = sourceType;
		this.source = source;
		this.logPatternlayout = logPatternlayout;
		this.sourceIndex = sourceIndex;
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
	 * @return the logPatternlayout
	 */
	public String getLogPatternlayout() {
		return logPatternlayout;
	}

	/**
	 * @param logPatternlayout
	 *            the logPatternlayout to set
	 */
	public void setLogPatternlayout(String logPatternlayout) {
		this.logPatternlayout = logPatternlayout;
	}

	/**
	 * @return the tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/**
	 * @param tokens
	 *            the tokens to set
	 */
	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	/**
	 * @return the timeStampFormat
	 */
	public String getTimeStampFormat() {
		return timeStampFormat;
	}

	/**
	 * @param timeStampFormat
	 *            the timeStampFormat to set
	 */
	public void setTimeStampFormat(String timeStampFormat) {
		this.timeStampFormat = timeStampFormat;
	}

}
