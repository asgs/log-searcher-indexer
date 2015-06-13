/**
 * 
 */
package net.tworks.logapps.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.tworks.logapps.index.persist.LogDataPersister;
import net.tworks.logapps.index.watch.FileWatcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author asgs
 * 
 *         A listener bound to this application context's life cycle. This
 *         listener initializes the Data sources required to operate this web
 *         application.
 * 
 */
public class FileWatcherStartupInitializer implements ServletContextListener {

	@Autowired
	private FileWatcher fileWatcher;

	@Autowired
	private LogDataPersister logDataPersister;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		/*
		 * WebApplicationContextUtils
		 * .getRequiredWebApplicationContext(event.getServletContext())
		 * .getAutowireCapableBeanFactory().autowireBean(this);
		 */
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		fileWatcher.initialize();
		fileWatcher.registerObserver(logDataPersister);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		fileWatcher.cleanUp();
	}

}
