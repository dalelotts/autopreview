/**
 *  Copyright (C) 2012 Knight Rider Consulting, Inc.
 *  support@knightrider.com
 *  http://www.knightrider.com
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/
 **/

package com.knightrider.autopreview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Monitors a directory (or tree) for changes to files.
 */

class DirectoryMonitor {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMonitor.class);
	private final Map<WatchKey, Path> keys;
	private final List<DirectoryListener> listeners = new ArrayList<>();
	private final WatchService watcher;


	/**
	 * Creates a DirectoryMonitor and registers the given directory
	 *
	 * @param dir
	 * 		the directory to watch
	 */

	DirectoryMonitor(final Path dir) {
		try {
			watcher = FileSystems.getDefault().newWatchService();
			keys = new HashMap<>();
			register(dir);
			LOG.info("Monitoring {}", dir.toString());
		} catch (IOException ex) {
			throw new RuntimeException("Failed to watch directory: " + dir, ex);
		}
	}

	/**
	 * Adds a listener to this monitor.
	 *
	 * @param listener
	 * 		the listener to add.
	 */
	void addListener(final DirectoryListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}


	/**
	 * Notifies all registered listeners that a file has been created.
	 *
	 * @param path
	 * 		the path to the created file.
	 */
	private void created(final Path path) {
		synchronized (listeners) {
			for (final DirectoryListener listener : listeners) {
				listener.created(path);
			}
		}
	}


	/**
	 * Process all events for keys queued to the watcher
	 */

	void processEvents() {
		while (true) {

			// wait for key to be signalled
			final WatchKey key;
			try {
				key = watcher.take();
			} catch (InterruptedException ignore) {
				return;
			}

			final Path dir = keys.get(key);
			if (dir == null) {
				LOG.error("WatchKey not recognized! {} does not exist in {}", key, keys);
				continue;
			}

			for (final WatchEvent<?> event : key.pollEvents()) {
				final WatchEvent.Kind kind = event.kind();

				// TBD - provide example of how OVERFLOW event is handled
				if (kind == StandardWatchEventKinds.OVERFLOW) {
					continue;
				}

				// Context for directory entry event is the file name of entry
				@SuppressWarnings({"unchecked", "SuppressionAnnotation"}) final WatchEvent<Path> ev = (WatchEvent<Path>) (event);
				final Path name = ev.context();
				final Path child = dir.resolve(name);

				LOG.info("{}: {}", event.kind().name(), child);

				created(child);
			}

			// reset key and remove from set if directory no longer accessible
			final boolean invalid = !key.reset();
			if (invalid) {
				keys.remove(key);

				// all directories are inaccessible
				if (keys.isEmpty()) {
					break;
				}
			}
		}
	}


	/**
	 * Register the given directory with the WatchService
	 *
	 * @param dir
	 * 		Path being watched.
	 */

	private void register(final Path dir) {
		final WatchKey key;
		try {
			key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
		} catch (IOException ex) {
			throw new RuntimeException("Registration failed for " + dir, ex);
		}
		if (LOG.isTraceEnabled()) {
			final Path prev = keys.get(key);
			if (prev == null) {
				LOG.info("register: {}", dir);
			} else {
				if (!dir.equals(prev)) {
					LOG.info("update: {}  ->  {}", prev, dir);
				}
			}
		}
		keys.put(key, dir);
	}
}