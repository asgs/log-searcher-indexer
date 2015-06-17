/**
 * 
 */
package net.tworks.logapps.index.watch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import net.tworks.logapps.common.database.DataSourceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 * 
 *         This class is responsible for retrieving the existing sources/logs
 *         configured in the database and request them to be watched for changes
 *         by the <code>FileWatcher</code> daemon.
 *
 */
@Component
public class DatabaseSourceWatchEnabler {

	private static final String SQL_TO_GET_SOURCES = "select source from source_mapping";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileWatcher fileWatcher;

	@Autowired
	private DataSourceManager dataSourceManager;

	@PostConstruct
	public void watchSourcesConfiguredInDb() {
		JdbcTemplate jdbcTemplate = dataSourceManager.getJdbcTemplate();

		List<String> sources = jdbcTemplate.queryForList(SQL_TO_GET_SOURCES,
				String.class);
		if (sources.isEmpty()) {
			logger.info("No sources available in Database for File watch.");
			return;
		}
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(() -> {
			Thread.currentThread().setName("DatabaseSourceWatchEnabler");
			sources.stream().forEach((String source) -> {
				fileWatcher.watchOutForChanges(source, false);
			});
		});
		logger.info("Successfully configured watch for the sources {}.",
				sources);
		executorService.shutdown();

	}
}
