/**
 * 
 */
package net.tworks.logapps.index.watch;

import java.util.Observer;

/**
 * @author asgs
 * 
 *         An interface which enables watching a file in the file system for any
 *         changes.
 * 
 */
public interface FileWatcher {

	/**
	 * A method that will stuff to initialize the watcher component. This is
	 * exposed so that the servlet context can invoke it during the application
	 * startup and keep it running in the background. The interface doesn't
	 * specify anything as to what should be done, so it's left to the
	 * implementors.
	 */
	void initialize();

	/**
	 * Interface method to be implemented to watch out for any changes in the
	 * given file.
	 * 
	 * @param file
	 */
	void watchOutForChanges(String fullyQualifiedFileName);

	/**
	 * Adds a convenience for the callers to subscribe to events that may be
	 * generated by the FileWatcher. E.g. a change in file content, or a
	 * deletion of that file might be interesting to some callers.
	 * 
	 * @param observer
	 *            Observer who is interested in certain events the File Watcher
	 *            may generate.
	 */
	void registerObserver(Observer observer);

	/**
	 * A signal to close down all its work and release all the resources it has
	 * been using.
	 */
	void cleanUp();

}