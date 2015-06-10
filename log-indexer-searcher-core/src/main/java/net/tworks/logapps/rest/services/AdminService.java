/**
 * 
 */
package net.tworks.logapps.rest.services;

import java.util.List;

import net.tworks.logapps.admin.database.ConfigureSourceTypeDAO;
import net.tworks.logapps.admin.parser.LogPatternLayoutParser;
import net.tworks.logapps.common.model.SourceTypeConfiguration;

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

	@RequestMapping(value = "/configure", method = RequestMethod.GET)
	public void configureSourceType(
			@RequestParam(value = "logLocation") String logLocation,
			@RequestParam(value = "logPatternlayout") String logPatternLayout,
			@RequestParam(value = "sourceType") String sourceType,
			@RequestParam(value = "sourceIndex") String sourceIndex) {

		SourceTypeConfiguration sourceTypeConfiguration = new SourceTypeConfiguration(
				sourceType, logLocation, logPatternLayout);
		// SourceIndex is not mandatory. Which means this sourceType can be
		// added to the (only) existing sourceIndex in the database repository.
		if (sourceIndex != null) {
			sourceTypeConfiguration.setSourceIndex(sourceIndex);
		}
		LogPatternLayoutParser logPatternLayoutParser = new LogPatternLayoutParser(
				logPatternLayout);

		List<String> tokens = logPatternLayoutParser.generateTokenNames();

		sourceTypeConfiguration.setTokens(tokens);

		configureSourceTypeDAO.configureNewSourceType(sourceTypeConfiguration);

	}
}
