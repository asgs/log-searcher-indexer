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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
public class Jdk7FileWatcher implements FileWatcher {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final AtomicInteger atomicInteger = new AtomicInteger();

	private WatchService watchService;

	private Map<String, Long> filePositionMap;

	private boolean threadShouldRun;

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
			threadShouldRun = true;
			thread.setName("FileWatcherDaemonThread-"
					+ atomicInteger.incrementAndGet());
			thread.start();
			logger.info("WatchService background daemon spawned off.");
			// registerObserver(logDataPersister);
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
			if (logger.isDebugEnabled()) {
				logger.debug("Directory instance is {}.", directory);
				logger.debug("watchService instance is {}.", watchService);
			}
			directory.register(watchService, new WatchEvent.Kind[] {
					StandardWatchEventKinds.ENTRY_MODIFY,
					StandardWatchEventKinds.ENTRY_CREATE,
					StandardWatchEventKinds.ENTRY_DELETE },
					SensitivityWatchEventModifier.HIGH);
			filePositionMap.put(fileName, 0L);
			logger.info(
					"Successfully registered directory {} to the WatchService.",
					directory);
		} catch (IOException e) {
			logger.error("Exception registering directory {}; cause is {}.",
					directory, e);
		}
	}

	private void pollFileChanges() {
		while (threadShouldRun) {
			WatchKey watchKey = null;
			try {
				logger.info("Polling for watchService events.");
				watchKey = watchService.take();
				List<WatchEvent<?>> pollEvents = watchKey.pollEvents();
				for (@SuppressWarnings("rawtypes")
				WatchEvent watchEvent : pollEvents) {
					Path path = (Path) watchEvent.context();
					String fileName = path.toString();
					logger.info(
							"Received watch notification for {}. WatchEvent kind is {}",
							fileName, watchEvent.kind().name());
					Set<String> keySet = filePositionMap.keySet();
					for (String fullFileName : keySet) {
						logger.info("File name from filePositionMap is {}.",
								fullFileName);
						if (fullFileName.endsWith(fileName)) {
							logger.info("Going to read new content from {}.",
									fullFileName);
							readNewContent(fullFileName);
							break;
						}

					}

				}
				watchKey.reset();
			} catch (InterruptedException e) {
				logger.error(
						"Exception retrieving watchKey from WatchService. cause is {}.",
						e);
				if (watchKey != null) {
					watchKey.reset();
				}
			} catch (Exception exception) {
				logger.error(
						"Exception polling changes. Continuing to poll though. Cause is {}.",
						exception);
				if (watchKey != null) {
					logger.info("Resetting the watchKey.");
					watchKey.reset();
				}
			}

		}
	}

	private void readFile(String fileName) {
		try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
			byte[] byteArray = IOUtils.toByteArray(fileInputStream);
			filePositionMap.put(fileName, (long) byteArray.length);
			logger.info("Current content below.");
			String fileContents = new String(byteArray, "UTF-8");
			logger.info(fileContents);
			// This should ideally be delegated to the parser to split the
			// messages and persist to the data store.
			// setChanged();
			// SourceDTO sourceDTO = new SourceDTO(fileName, fileContents);
			// notifyObservers(sourceDTO);
			logDataPersister.persistLogData(fileName, fileContents);
		} catch (IOException e) {
			logger.error("Exception reading file {}; cause is {}.", fileName, e);
		}
	}

	private void readNewContent(String fileName) {
		try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
			long byteLocation = filePositionMap.get(fileName);
			fileInputStream.skip(byteLocation);
			byte[] byteArray = IOUtils.toByteArray(fileInputStream);
			byteLocation += byteArray.length;
			filePositionMap.put(fileName, byteLocation);
			String fileContents = new String(byteArray, "UTF-8");
			if (logger.isDebugEnabled()) {
				// The separator should be configurable based on the Platform.
				String[] lines = fileContents.split("\n");
				logger.info("New content below.");
				for (String line : lines) {
					logger.debug(line);
				}
			}
			// setChanged();
			// SourceDTO sourceDTO = new SourceDTO(fileName, fileContents);
			// notifyObservers(sourceDTO);
			logDataPersister.persistLogData(fileName, fileContents);

		} catch (IOException e) {
			logger.error("Exception reading file {}; cause is {}.", fileName, e);
		}
	}

	@Override
	public void watchOutForChanges(String fullyQualifiedFileName) {
		registerFileForWatching(fullyQualifiedFileName);
		readFile(fullyQualifiedFileName);
	}

	/*
	 * public void registerObserver(Observer observer) {
	 * logger.info("Registering observer {}.", observer); addObserver(observer);
	 * logger.info("Registered observer {}.", observer); }
	 */

	@Override
	@PreDestroy
	public void cleanUp() {
		threadShouldRun = false;
		if (watchService != null) {
			try {
				watchService.close();
				logger.info("Shutting down watchService and the background thread.");
			} catch (IOException e) {
				logger.error("Error closing watchService {}", e);
			}
		}

	}
}
