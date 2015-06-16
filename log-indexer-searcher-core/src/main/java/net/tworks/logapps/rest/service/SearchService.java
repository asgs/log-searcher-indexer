/**
 * 
 */
package net.tworks.logapps.rest.service;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.rest.model.SearchQuery;
import net.tworks.logapps.search.QuerySearchFacade;
import net.tworks.logapps.search.parser.SearchQueryParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asgs
 * 
 */
@RestController
@RequestMapping("/search")
public class SearchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private QuerySearchFacade querySearchFacade;

	@Autowired
	private DataSourceManager dataSourceManager;

	/**
	 * Currently it supports searching for queries for a given time frame. e.g.
	 * last 5 minutes, last 1 hour, last 2 days, etc., It could be enhanced
	 * further taking two date or date-time ranges for refined filtering.
	 * 
	 * @param query
	 *            The search query user has entered.
	 * @param time
	 *            The value of time in long format.
	 * @param timeunit
	 *            The unit as specified in <code>ChronoUnit</code>.
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public String[] queryResults(@RequestParam(value = "text") String query,
			@RequestParam(required = false, value = "timeframe") Long time,
			@RequestParam(required = false, value = "timeunit") String timeUnit) {

		if (time == null) {
			time = 0L;
		}
		SearchQueryParser parser = new SearchQueryParser(query, time, timeUnit);
		SearchQuery searchQuery = parser.parse();
		String[] searchResults = null;
		try {
			searchResults = querySearchFacade
					.retrieveSearchResults(searchQuery);
			logger.info("Ran query. Printing results.");
		} catch (Exception e) {
			logger.error("Error retrieving search results. Cause is {}.", e);
		}
		return searchResults;

	}

}
