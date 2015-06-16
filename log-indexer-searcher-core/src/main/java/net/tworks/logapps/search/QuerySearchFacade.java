/**
 * 
 */
package net.tworks.logapps.search;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.tworks.logapps.common.model.SearchKeyValue;
import net.tworks.logapps.rest.model.SearchQuery;
import net.tworks.logapps.search.dao.LogSearchDAO;

/**
 * @author asgs
 * 
 *         The facade for querying logs from the database with the various
 *         combinations available, without letting the client or the Controller
 *         layer know about the complexity involved in accomplishing it.
 */
@Component
public class QuerySearchFacade {

	@Autowired
	private LogSearchDAO logSearchDAO;

	public String[] retrieveSearchResults(SearchQuery searchQuery) {

		if (searchQuery.getTimeDuration() == 0) {
			if (searchQuery.getKeyValues().isEmpty()) {
				// No key value pairs available.
				return logSearchDAO
						.searchByRawQuery(searchQuery.getFullQuery());
			} else {
				return logSearchDAO.searchByKeysWithMainQuery(
						extractSearchKeyValues(searchQuery),
						searchQuery.getMainQuery());
			}
		} else {
			if (searchQuery.getKeyValues().isEmpty()) {
				// No key value pairs available.
				return logSearchDAO.searchByRawQueryForAGivenTimeFrame(
						searchQuery.getFullQuery(),
						searchQuery.getTimeDuration(),
						searchQuery.getTimeUnit());
			} else {
				return logSearchDAO
						.searchByKeysWithMainQueryForAGivenTimeFrame(
								extractSearchKeyValues(searchQuery),
								searchQuery.getMainQuery(),
								searchQuery.getTimeDuration(),
								searchQuery.getTimeUnit());
			}
		}
	}

	/**
	 * A trivial method to convert the Model with Rest layer to the one with DAO
	 * layer.
	 * 
	 * @param searchQuery
	 * @return Array of {@link SearchKeyValue}
	 */
	private SearchKeyValue[] extractSearchKeyValues(SearchQuery searchQuery) {
		List<String> keyValues = searchQuery.getKeyValues();
		Collections.sort(keyValues);
		if (keyValues.isEmpty()) {
			return null;
		} else {
			SearchKeyValue[] searchKeyValues = new SearchKeyValue[keyValues
					.size()];
			int index = 0;
			for (String string : keyValues) {
				String[] splits = string.split("=");
				if (splits.length == 2) {
					SearchKeyValue searchKeyValue = new SearchKeyValue(
							splits[0], splits[1]);
					searchKeyValues[index] = searchKeyValue;
				}
				index++;
			}
			return searchKeyValues;
		}
	}

}
