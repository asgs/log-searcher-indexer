/**
 * 
 */
package net.tworks.logapps.index.watch;

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
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import net.tworks.logapps.common.model.SourceDTO;
import net.tworks.logapps.index.persist.LogDataPersister;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sun.nio.file.SensitivityWatchEventModifier;

/**
 * @author asgs
 * 
 *         An implementation based on the Java 7's in-built
 *         <code>WatchService</code> API.
 *
 */
@Component
public class Jdk7FileWatcher extends Observable implements FileWatcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private WatchService watchService;

	private Map<String, Long> filePositionMap;

	private boolean keepThreadRunning;

	@Autowired
	private LogDataPersister logDataPersister;

	@PostConstruct
	public void initialize() {
		try {
			watchService = FileSystems.getDefault().newWatchService();
			filePositionMap = new ConcurrentHashMap<String, Long>();
			logger.info("WatchService initialized with watchService {}.",
					watchService);
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					pollFileChanges();

				}
			});
			keepThreadRunning = true;
			thread.start();
			logger.info("WatchService background daemon spawned off.");
			registerObserver(logDataPersister);
		} catch (IOException e) {
			logger.error("Exception creating WatchService {}.", e);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Jdk7FileWatcher fileWatcherTest = new Jdk7FileWatcher();
		fileWatcherTest.initialize();
		fileWatcherTest
				.watchOutForChanges("D:/DLs/apache-tomcat-8.0.15-windows-x64/logs/localhost_access_log.2015-06-12.txt");

	}

	private void registerFileForWatching(final String fileName) {
		logger.info("Going to register file {}.", fileName);
		int lastIndexOfSlash = fileName.lastIndexOf("/");
		String directoryName = fileName.substring(0, lastIndexOfSlash);
		Path directory = Paths.get(directoryName);
		try {
			logger.info("Directory instance is {}.", directory);
			logger.info("watchService instance is {}.", watchService);
			directory.register(watchService, new WatchEvent.Kind[] {
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE },
					SensitivityWatchEventModifier.HIGH);
			filePositionMap.put(fileName, 0L);
			logger.info(
					"Successfully register directory {} to the WatchService {}.",
					directory);
		} catch (IOException e) {
			logger.error("Exception registering directory {}; cause is {}.",
					directory, e);
		}
	}

	private void pollFileChanges() {
		while (keepThreadRunning) {
			WatchKey watchKey;
			try {
				watchKey = watchService.take();
				List<WatchEvent<?>> pollEvents = watchKey.pollEvents();
				for (WatchEvent watchEvent : pollEvents) {
					Path path = (Path) watchEvent.context();
					String string = path.toString();
					logger.info(
							"Received watch notification for {}. WatchEvent kind is {}",
							string, watchEvent.kind().name());
					Set<String> keySet = filePositionMap.keySet();
					for (String fileName : keySet) {
						readNewContent(fileName);
					}

				}
				watchKey.reset();
			} catch (InterruptedException e) {
				logger.error(
						"Exception retrieving watchKey from WatchService. cause is {}.",
						e);
			}

		}
	}

	private void readFile(String fileName) {
		try {
			byte[] byteArray = IOUtils
					.toByteArray(new FileInputStream(fileName));
			filePositionMap.put(fileName, (long) byteArray.length);
			logger.info("Current content below.");
			String fileContents = new String(byteArray, "UTF-8");
			logger.info(fileContents);
			// This should ideally be delegated to the parser to split the
			// messages and persist to the data store.
			setChanged();
			SourceDTO sourceDTO = new SourceDTO(fileName, fileContents);
			notifyObservers(sourceDTO);
		} catch (IOException e) {
			logger.error("Exception reading file {}; cause is {}.", fileName, e);
		}
	}

	private void readNewContent(String fileName) {
		try {
			FileInputStream fileInputStream = new FileInputStream(fileName);
			long byteLocation = filePositionMap.get(fileName);
			fileInputStream.skip(byteLocation);
			byte[] byteArray = IOUtils.toByteArray(fileInputStream);
			byteLocation += byteArray.length;
			filePositionMap.put(fileName, byteLocation);
			logger.info("New content below.");
			String fileContents = new String(byteArray, "UTF-8");
			// The separator should be configurable based on the Platform.
			String[] lines = fileContents.split("\n");
			for (String line : lines) {
				logger.info(line);
			}
			setChanged();
			SourceDTO sourceDTO = new SourceDTO(fileName, fileContents);
			notifyObservers(sourceDTO);

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

	public void registerObserver(Observer observer) {
		logger.info("Registering observer {}.", observer);
		addObserver(observer);
		logger.info("Registered observer {}.", observer);
	}

	@Override
	public void cleanUp() {
		keepThreadRunning = false;
		if (watchService != null) {
			try {
				watchService.close();
			} catch (IOException e) {
				logger.error("Error closing watchService {}", e);
			}
		}

	}
}
