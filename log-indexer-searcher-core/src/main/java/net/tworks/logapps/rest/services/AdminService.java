/**
 * 
 */
package net.tworks.logapps.rest.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author asgs
 * 
 */
@RestController
@RequestMapping("/admin")
public class AdminService {

	@RequestMapping(value = "/configure", method = RequestMethod.GET)
	public void queryResults(
			@RequestParam(value = "loglocation") String logLocation) {

	}
}
