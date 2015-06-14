/**
 * 
 */
package net.tworks.logapps.search.dao;

import java.time.temporal.ChronoUnit;

import net.tworks.logapps.common.model.SearchKeyValue;

/**
 * @author asgs
 * 
 *         A unified interface to query for search results given various
 *         combinations the user has control over.
 *
 */
public interface LogSearchDAO {

	/**
	 * Returns the results for the raw query.
	 * 
	 * @param rawQuery
	 *            The query as is without any key value pairs.
	 * @return Array of results.
	 */
	String[] searchByRawQuery(String rawQuery);

	/**
	 * Returns the results for the main query with key values.
	 * 
	 * @param keyValues
	 *            Key values to make the query more specific.
	 * @param mainQuery
	 *            The full query minus key values.
	 * @return Array of results.
	 */
	String[] searchByKeysWithRawQuery(SearchKeyValue[] keyValues,
			String mainQuery);

	/**
	 * Returns the results for the raw query with key values for the duration
	 * provided.
	 * 
	 * @param rawQuery
	 *            The query as is without any key value pairs.
	 * @param timeDuration
	 *            The duration for which the query will be applied to.
	 * @param timeUnit
	 *            The unit of the timeDuration field.
	 * @return Array of results.
	 */
	String[] searchByRawQueryForAGivenTimeFrame(String rawQuery,
			long timeDuration, ChronoUnit timeUnit);

	/**
	 * Returns the results for the main query with key values for the duration
	 * provided.
	 * 
	 * @param keyValues
	 *            Key values to make the query more specific.
	 * @param mainQuery
	 *            The full query minus key values.
	 * @param timeDuration
	 *            The duration for which the query will be applied to.
	 * @param timeUnit
	 *            The unit of the timeDuration field.
	 * @return Array of results.
	 */
	String[] searchByKeysWithRawQueryForAGivenTimeFrame(
			SearchKeyValue[] keyValues, String mainQuery, long timeDuration,
			ChronoUnit timeUnit);

}
