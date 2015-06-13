/**
 * 
 */
package net.tworks.logapps.rest.service;

import java.util.List;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.rest.model.SourceType;
import net.tworks.logapps.rest.model.SourceTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSourceManager dataSourceManager;

	@RequestMapping(value = "/generate", method = RequestMethod.GET)
	public boolean generateLogContent(
			@RequestParam(value = "sourceType") String sourceType,
			@RequestParam(value = "source") String source) {

		return true;

	}

	@RequestMapping(value = "/retrieveAvailableLogs", method = RequestMethod.GET)
	public SourceTypes queryResults() {

		jdbcTemplate = dataSourceManager.getJdbcTemplate();
		logger.info("Ran query. Printing results.");

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
		return sourceTypes2;

	}
}
