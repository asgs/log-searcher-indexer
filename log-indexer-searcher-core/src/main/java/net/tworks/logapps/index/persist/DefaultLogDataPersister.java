/**
 * 
 */
package net.tworks.logapps.index.persist;

import java.util.Observable;
import java.util.Observer;

import net.tworks.logapps.common.database.DataSourceManager;
import net.tworks.logapps.common.model.SourceDTO;
import net.tworks.logapps.index.watch.FileWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author asgs
 *
 *         A default implementation to persist the given Log file data to a
 *         Database.
 */
@Component
public class DefaultLogDataPersister implements LogDataPersister, Observer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final JdbcTemplate jdbcTemplate = DataSourceManager.getInstance()
			.getJdbcTemplate();

	@Autowired
	private FileWatcher fileWatcher;

	private final String sqlForGettingSourcePatternLayout = "select pattern_layout from source_metadata where source = ?";

	@Override
	public void persistLogData(String source, String logContents) {
		// 1. Retrieve the source_metadata for the given source.

		String sourcePatternLayout = jdbcTemplate.queryForObject(
				sqlForGettingSourcePatternLayout, new String[] { source },
				String.class);
		logger.info("sourcePatternLayout is " + sourcePatternLayout);
	}

	public static void main(String[] args) {
		new DefaultLogDataPersister().persistLogData("blah2", null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof SourceDTO) {
			SourceDTO sourceDTO = (SourceDTO) arg;
			logger.info(
					"Received event notification on update of the file {}.",
					sourceDTO.getSource());
			persistLogData(sourceDTO.getSource(), sourceDTO.getSourceContents());
		} else {
			logger.warn("Discarded a non-SourceDTO {} event update.", arg);
		}

	}

}
