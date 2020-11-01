package com.blz.employeepayrolljavaio;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class Java8WatchServiceExample {
	private final WatchService watcher;
	private final Map<WatchKey, Path> dirWatchers;

	// CREATES A WATCH-SERVICE AND REGISTERS THE GIVEN DIRECTORY
	public Java8WatchServiceExample(Path dir) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		this.dirWatchers = new HashMap<WatchKey, Path>();
		scanAndRegisterDirectories(dir);
	}

	// REGISTER THE GIVEN DIRECTORY WITH THE WATCH-SERVICE
	private void registerDirMatchers(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		dirWatchers.put(key, dir);
	}

	// REGISTER THE GIVEN DIRECTORY, AND ALL ITS SUB-DIRECTORIES, WITH THE
	// WATCH-SERVICE
	private void scanAndRegisterDirectories(final Path start) throws IOException {
		// REGISTER DIRECTORY AND SUB-DIRECTORIES
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerDirMatchers(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	// PROCESS ALL EVENTS FOR KEYS QUEUED TO THE WATCHER
	void processEvents() {
		while (true) {
			WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException x) {
				return;
			}
			Path dir = dirWatchers.get(key);
			if (dir == null)
				continue;
			for (WatchEvent<?> event : key.pollEvents()) {
				WatchEvent.Kind kind = event.kind();
				Path name = ((WatchEvent<Path>) event).context();
				Path child = dir.resolve(name);
				System.out.format("%s: %s\n", event.kind().name(), child);// PRINT-OUT EVENT

				// IF DIRECTORY IS CREATED, THEN REGISTER IT AND ITS SUB-DIRECTORIES
				if (kind.equals(ENTRY_CREATE)) {
					try {
						if (Files.isDirectory(child))
							scanAndRegisterDirectories(child);
					} catch (IOException x) {

					}
				} else if (kind.equals(ENTRY_DELETE)) {
					if (Files.isDirectory(child))
						dirWatchers.remove(key);
				}
			}

			// RESET KEY AND REMOVE FROM SET IF DIRECTORY NO LONGER AVAILABLE
			boolean valid = key.reset();
			if (!valid)
				dirWatchers.remove(key);
			if (dirWatchers.isEmpty())
				break;// ALL DIECTORIES INACCESSIBLE
		}
	}
}