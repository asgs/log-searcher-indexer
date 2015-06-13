/**
 * 
 */
package net.tworks.logapps.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.tworks.logapps.common.database.DataSourceManager;

/**
 * @author asgs
 * 
 *         A listener bound to this application context's life cycle. This
 *         listener initializes the Data sources required to operate this web
 *         application.
 * 
 */
public class DataSourceInitializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent event) {
		DataSourceManager.getInstance().init();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		DataSourceManager.getInstance().destroy();
	}

}
