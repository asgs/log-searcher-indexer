/**
 * 
 */
package net.tworks.logapps.rest.service;

import java.util.List;
import java.util.Map;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.rest.model.SearchResults;

import org.springframework.jdbc.core.JdbcTemplate;
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

	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/query", method = RequestMethod.GET)
	public SearchResults queryResults(@RequestParam(value = "data") String query) {

		SearchResults results = new SearchResults();
		String[] resultsArray = { "log line1", "log line2", "log line3" };
		results.setResults(resultsArray);
		jdbcTemplate = DataSourceManager.getInstance().getJdbcTemplate();
		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select * from users");
		System.out.println("Ran query. Printing results.");
		for (Map<String, Object> map : queryForList) {
			for (String string : map.keySet()) {
				System.out.println(string + ":" + map.get(string));
			}
		}
		return results;

	}

}
