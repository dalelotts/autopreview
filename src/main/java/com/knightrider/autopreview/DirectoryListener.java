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

import java.nio.file.Path;

/**
 * Interface implemented by classes that respond to files being created in the monitored directory.
 */
public interface DirectoryListener {

	/**
	 * Notifies listener of a file created in the monitored directory.
	 *
	 * @param path
	 * 		Path representing the created file.
	 */
	void created(Path path);
}
