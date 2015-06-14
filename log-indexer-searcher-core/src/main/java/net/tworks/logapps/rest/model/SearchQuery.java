/**
 * 
 */
package net.tworks.logapps.rest.model;

import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * @author asgs
 *
 *         Models a search query the user has fired from the front end.
 */
public class SearchQuery {

	/**
	 * This is the full query as entered by the user.
	 */
	private String fullQuery;

	/**
	 * The default constructor.
	 * 
	 * @param fullQuery
	 */
	public SearchQuery(String fullQuery) {
		this.fullQuery = fullQuery;
	}

	/**
	 * This is a collection of custom key-value items that could be used to
	 * refine the search. These items could originally be discovered during
	 * admin configuration as part of the Log source's pattern layout.
	 */
	private List<String> keyValues;

	/**
	 * The level of the log statement that this query comes under.
	 */
	private String logLevel;

	/**
	 * This is the thing the user actually wants to search for. In case there
	 * are no key-value items identified, this becomes the same as the rawQuery
	 * field itself.
	 */
	private String mainQuery;

	/**
	 * The index to be searched for.
	 */
	private String searchIndex;

	/**
	 * The type of source to be searched for.
	 */
	private String sourceType;

	/**
	 * The thread which could log this statement.
	 */
	private String threadName;

	/**
	 * The duration for which the query has to be run under.
	 */
	private long timeDuration;

	/**
	 * The unit of the timeDuration field.
	 */
	private ChronoUnit timeUnit;

	/**
	 * @return the fullQuery
	 */
	public String getFullQuery() {
		return fullQuery;
	}

	/**
	 * @return the keyValues
	 */
	public List<String> getKeyValues() {
		return keyValues;
	}

	/**
	 * @return the logLevel
	 */
	public String getLogLevel() {
		return logLevel;
	}

	/**
	 * @return the mainQuery
	 */
	public String getMainQuery() {
		return mainQuery;
	}

	/**
	 * @return the searchIndex
	 */
	public String getSearchIndex() {
		return searchIndex;
	}

	/**
	 * @return the sourceType
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * @return the threadName
	 */
	public String getThreadName() {
		return threadName;
	}

	/**
	 * @return the timeDuration
	 */
	public long getTimeDuration() {
		return timeDuration;
	}

	/**
	 * @return the timeUnit
	 */
	public ChronoUnit getTimeUnit() {
		return timeUnit;
	}

	/**
	 * @param fullQuery
	 *            the fullQuery to set
	 */
	public void setFullQuery(String fullQuery) {
		this.fullQuery = fullQuery;
	}

	/**
	 * @param keyValues
	 *            the keyValues to set
	 */
	public void setKeyValues(List<String> keyValues) {
		this.keyValues = keyValues;
	}

	/**
	 * @param logLevel
	 *            the logLevel to set
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	/**
	 * @param mainQuery
	 *            the mainQuery to set
	 */
	public void setMainQuery(String mainQuery) {
		this.mainQuery = mainQuery;
	}

	/**
	 * @param searchIndex
	 *            the searchIndex to set
	 */
	public void setSearchIndex(String searchIndex) {
		this.searchIndex = searchIndex;
	}

	/**
	 * @param sourceType
	 *            the sourceType to set
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * @param threadName
	 *            the threadName to set
	 */
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}

	/**
	 * @param timeDuration
	 *            the timeDuration to set
	 */
	public void setTimeDuration(long timeDuration) {
		this.timeDuration = timeDuration;
	}

	/**
	 * @param timeUnit
	 *            the timeUnit to set
	 */
	public void setTimeUnit(ChronoUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

}
