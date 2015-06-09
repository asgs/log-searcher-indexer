/**
 * 
 */
package net.tworks.logapps.indexing;

import java.io.File;

/**
 * @author asgs
 * 
 *         An interface which enables watching a file in the file system for any
 *         changes.
 * 
 */
public interface FileWatcher {

	/**
	 * Interface method to be implemented to watch out for any changes in the
	 * given file.
	 * 
	 * @param file
	 */
	void watchOutForChanges(File file);

}
