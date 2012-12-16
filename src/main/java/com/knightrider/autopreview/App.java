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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class App {

	/**
	 * Main entry point for this application.
	 *
	 * @param args
	 * 		the application arguments, all of which are ignored.
	 */
	public static void main(final String... args) {
		final String directory = getMonitoredDirectory();
		final Path dir = Paths.get(directory);
		final DirectoryMonitor monitor = new DirectoryMonitor(dir);
		monitor.addListener(new LaunchViewerDirectoryListener());
		monitor.processEvents();
	}

	/**
	 * Returns a string representing the directory to monitor, which is determined by the base.dir and sub.dir.format properties.
	 * Additionally, the location if the properties file is determined by the config system property.
	 *
	 * @return the full path to the directory to be monitored.
	 */
	private static String getMonitoredDirectory() {
		final Properties properties = new Properties();

		// Look for a -Dconfig property specified at startup, this determins teh location of the config file.
		final String config = System.getProperty("config");

		if (config == null) {
			throw new RuntimeException("No config property specified.");
		}

		try (final InputStream inputStream = new FileInputStream(config)) {
			properties.load(inputStream);
		} catch (FileNotFoundException ex) {
			throw new RuntimeException("Unable to locate autopreview.properties at " + config, ex);
		} catch (IOException ex) {
			throw new RuntimeException("Failed to load autopreview.properties", ex);
		}

		final String baseDirectory = (String) properties.get("base.dir");
		final String directoryFormat = (String) properties.get("sub.dir.format");
		final SimpleDateFormat FORMAT = new SimpleDateFormat(directoryFormat);

		final String directory = baseDirectory + FORMAT.format(new Date());
		final File file = new File(directory);
		if (!file.exists() && !file.mkdirs()) {
			throw new RuntimeException("Failed to create directory: " + directory);
		}
		return directory;
	}
}
