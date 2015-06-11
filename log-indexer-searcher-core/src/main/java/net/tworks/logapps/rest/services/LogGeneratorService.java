/**
 * 
 */
package net.tworks.logapps.rest.services;

import java.util.List;
import java.util.Map;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.rest.model.SearchResults;
import net.tworks.logapps.rest.model.SourceType;
import net.tworks.logapps.rest.model.SourceTypes;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asgs
 * 
 *         This is more like a convenience module for the user to be able to
 *         generate log content quickly so that search could be demonstrated
 *         dynamically.
 * 
 */
@RestController
@RequestMapping("/log")
public class LogGeneratorService {

	private JdbcTemplate jdbcTemplate;

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
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

	@RequestMapping(value = "/retrieveAvailableLogs", method = RequestMethod.GET)
	public SourceTypes queryResults() {

		jdbcTemplate = DataSourceManager.getInstance().getJdbcTemplate();
		System.out.println("Ran query. Printing results.");

		List<SourceType> sourceTypes = jdbcTemplate.query(
				"select source_type, source from source_mapping", (resultSet,
						rowNum) -> {
					String sourceType = resultSet.getString("source_type");
					String source = resultSet.getString("source");
					SourceType type = new SourceType(source, sourceType);
					return type;
				});

		SourceTypes sourceTypes2 = new SourceTypes();
		sourceTypes2.setSourceTypes(sourceTypes);
		;
		return sourceTypes2;

	}
}
