/**
 * 
 */
package net.tworks.logapps.indexing;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * @author asgs
 * 
 *         An implementation based on the Java 7's in-built
 *         <code>WatchService</code> API.
 *
 */
public class Jdk7FileWatcher implements FileWatcher {

	private static WatchService watchService;

	private static Map<String, Long> filePositionMap;

	public void initializeWatcher() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			filePositionMap = new ConcurrentHashMap<String, Long>();
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					pollFileChanges();

				}
			});
			thread.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Jdk7FileWatcher fileWatcherTest = new Jdk7FileWatcher();
		fileWatcherTest.initializeWatcher();
		fileWatcherTest
				.watchOutForChanges("D:/DLs/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-12.txt");

	}

	private static void registerFileForWatching(final String fileName) {
		int lastIndexOfSlash = fileName.lastIndexOf("/");
		String directoryName = fileName.substring(0, lastIndexOfSlash);
		Path directory = Paths.get(directoryName);
		try {
			directory.register(watchService, new WatchEvent.Kind[] {
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE },
					SensitivityWatchEventModifier.HIGH);
			filePositionMap.put(fileName, 0L);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void pollFileChanges() {
		while (true) {
			WatchKey watchKey;
			try {
				watchKey = watchService.take();
				List<WatchEvent<?>> pollEvents = watchKey.pollEvents();
				for (WatchEvent watchEvent : pollEvents) {
					Path path = (Path) watchEvent.context();
					String string = path.toString();
					System.out.println(string);
					System.out.println(watchEvent.kind().name());
					Set<String> keySet = filePositionMap.keySet();
					for (String fileName : keySet) {
						readNewContent(fileName);
					}

					// readNewContent("D:/DLs/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-12.txt");
				}
				watchKey.reset();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private static void readFile(String fileName) {
		try {
			byte[] byteArray = IOUtils
					.toByteArray(new FileInputStream(fileName));
			filePositionMap.put(fileName, (long) byteArray.length);
			System.out.println("Current content below.");
			System.out.println(new String(byteArray, "UTF-8"));
			// This should ideally be delegated to the parser to split the
			// messages and persist to the data store.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void readNewContent(String fileName) {
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			long byteLocation = filePositionMap.get(fileName);
			fileInputStream.skip(byteLocation);
			byte[] byteArray = IOUtils.toByteArray(fileInputStream);
			byteLocation += byteArray.length;
			filePositionMap.put(fileName, byteLocation);
			System.out.println("New content below.");
			String string = new String(byteArray, "UTF-8");
			// The separator should be configurable based on the Platform.
			String[] splits = string.split("\n");
			for (String split : splits) {
				System.out.println(split);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void watchOutForChanges(String fullyQualifiedFileName) {
		registerFileForWatching(fullyQualifiedFileName);
		readFile(fullyQualifiedFileName);
	}
}
