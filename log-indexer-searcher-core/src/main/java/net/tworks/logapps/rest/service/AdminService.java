/**
 * 
 */
package net.tworks.logapps.rest.service;

import java.util.List;

import net.tworks.logapps.admin.database.ConfigureSourceTypeDAO;
import net.tworks.logapps.admin.parser.LogPatternLayoutParser;
import net.tworks.logapps.common.database.exception.DatabaseConfigurationException;
import net.tworks.logapps.common.model.SourceTypeConfiguration;
import net.tworks.logapps.rest.model.ConfigurationResult;

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
 *         Service to enable administrators to add and configure new
 *         source_types, i.e. new log files. It accepts the log file location
 *         and the log pattern layout to parse any key value pairs that could be
 *         indexed for faster retrieval of log data.
 * 
 */
@RestController
@RequestMapping("/admin")
public class AdminService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConfigureSourceTypeDAO configureSourceTypeDAO;

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
			logger.info("Successfully configured for indexing.");
		} catch (DatabaseConfigurationException databaseConfigurationException) {
			configurationResult.setResult(false);
			configurationResult.setMessage("Failed! Reason is "
					+ databaseConfigurationException.getMessage());
			logger.error("Failed! Reason is {}.",
					databaseConfigurationException);
		}
		return configurationResult;
	}
}
