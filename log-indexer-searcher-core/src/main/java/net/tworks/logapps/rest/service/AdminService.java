/**
 * 
 */
package net.tworks.logapps.rest.service;

import java.util.List;

import net.tworks.logapps.admin.database.ConfigureSourceTypeDAO;
import net.tworks.logapps.admin.parser.LogPatternLayoutParser;
import net.tworks.logapps.common.database.exception.DatabaseConfigurationException;
import net.tworks.logapps.common.model.SourceTypeConfiguration;
import net.tworks.logapps.index.watch.FileWatcher;
import net.tworks.logapps.rest.model.ConfigurationResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asgs
 * 
 *         Service to enable administrators to add and configure new
 *         source_types, i.e. new log files. It accepts the log file location
 *         and the log pattern layout to parse any key value pairs that could be
 *         indexed for faster retrieval of log data.
 * 
 */
@RestController
@RequestMapping("/admin")
public class AdminService {

	@Autowired
	private ConfigureSourceTypeDAO configureSourceTypeDAO;

	@Autowired
	private FileWatcher fileWatcher;

	@RequestMapping(value = "/configure", method = RequestMethod.GET)
	public ConfigurationResult configureSourceType(
			@RequestParam(value = "source") String source,
			@RequestParam(value = "logPatternlayout") String logPatternLayout,
			@RequestParam(value = "sourceType") String sourceType,
			@RequestParam(value = "sourceIndex") String sourceIndex) {

		SourceTypeConfiguration sourceTypeConfiguration = new SourceTypeConfiguration(
				sourceType, source, logPatternLayout);
		// SourceIndex is not mandatory. Which means this sourceType can be
		// added to the (only) existing sourceIndex in the database repository.
		if (sourceIndex != null) {
			sourceTypeConfiguration.setSourceIndex(sourceIndex);
		}
		LogPatternLayoutParser logPatternLayoutParser = new LogPatternLayoutParser(
				logPatternLayout);

		List<String> tokens = logPatternLayoutParser
				.generateKeyValueTokenNames();
		String timeStampFormat = logPatternLayoutParser.parseTimeStampFormat();
		sourceTypeConfiguration.setTimeStampFormat(timeStampFormat);

		sourceTypeConfiguration.setTokens(tokens);
		ConfigurationResult configurationResult = new ConfigurationResult();
		try {
			configureSourceTypeDAO
					.configureNewSourceType(sourceTypeConfiguration);
			configurationResult.setResult(true);
			configurationResult
					.setMessage("Successfully configured for indexing.");
			//fileWatcher.watchOutForChanges(source);
		} catch (DatabaseConfigurationException databaseConfigurationException) {
			configurationResult.setResult(false);
			configurationResult
					.setMessage("Failed to configured for indexing. Reason is "
							+ databaseConfigurationException.getMessage());
		}
		return configurationResult;
	}
}
