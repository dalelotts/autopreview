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

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A DirectoryListener that lauches the default application when a new file is created.
 */
final class LaunchViewerDirectoryListener implements DirectoryListener {

	private static final Logger LOG = LoggerFactory.getLogger(LaunchViewerDirectoryListener.class);

	@Override // DirectoryListener
	public void created(final Path path) {
		try {
			Desktop.getDesktop().open(path.toFile());
		} catch (IOException ex) {
			LOG.error("Unable to launch viewer for: " + path, ex);
		}
	}
}
