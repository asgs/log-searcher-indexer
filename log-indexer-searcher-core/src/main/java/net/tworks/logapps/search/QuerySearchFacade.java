/**
 * 
 */
package net.tworks.logapps.search;

import net.tworks.logapps.rest.model.SearchQuery;

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
			SearchQuery searchQuery) {
		/*if (searchQuery.getTokenPairs() != null) {
			return getSearchResultsWithFieldElements(
					searchQuery.getSearchString(),
					searchQuery.getTokenPairs());
		} else {
			return getSearchResults(searchQuery.getSearchString());
		}*/
		return null;
	}

}
