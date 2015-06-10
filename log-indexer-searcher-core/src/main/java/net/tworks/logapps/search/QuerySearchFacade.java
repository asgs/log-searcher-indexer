/**
 * 
 */
package net.tworks.logapps.search;

import net.tworks.logapps.common.model.SearchInputCriteria;

/**
 * @author asgs
 * 
 *         The facade for querying anything from the database.
 */
public class QuerySearchFacade {

	private String[] getSearchResults(String searchString) {
		return null;
	}

	private String[] getSearchResultsWithFieldElements(String searchString,
			String... fields) {
		return null;
	}

	public String[] retrieveSearchResults(
			SearchInputCriteria searchInputCriteria) {
		if (searchInputCriteria.getTokenPairs() != null) {
			return getSearchResultsWithFieldElements(
					searchInputCriteria.getSearchString(),
					searchInputCriteria.getTokenPairs());
		} else {
			return getSearchResults(searchInputCriteria.getSearchString());
		}
	}

}
