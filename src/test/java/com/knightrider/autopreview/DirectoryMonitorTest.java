/**
 CONTENTS PROPRIETARY AND CONFIDENTIAL

 Copyright © 2013 Knight Rider Consulting, Inc. All rights reserved.
 http://www.knightrider.com

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 POSSIBILITY OF SUCH DAMAGE.
 */
package com.knightrider.autopreview;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Dale "Ducky" Lotts
 * @since 3/16/13
 */
public final class DirectoryMonitorTest {

	private static final Logger LOG = LoggerFactory.getLogger(DirectoryMonitorTest.class);
	private DirectoryMonitor monitor;
	private Path tempDirectory;

	@Test
	public void addListener() {
		monitor.addListener(new MockListener());
	}

	@Test
	public void processEvents() {

		final MockListener listener = new MockListener();
		monitor.addListener(listener);

		final List<Path> actualPaths = new ArrayList<>();

		final ScheduledExecutorService pool = Executors.newSingleThreadScheduledExecutor(Executors.defaultThreadFactory());

		final Runnable createTempFile = new Runnable() {
			public void run() {
				try {
					actualPaths.add(Files.createTempFile(tempDirectory, null, null));
				} catch (IOException ex) {
					Assert.fail(ex.getMessage());
				}
			}
		};

		final ScheduledFuture<?> createTempFileHandle = pool.scheduleAtFixedRate(createTempFile, 500, 10, TimeUnit.MILLISECONDS);

		pool.schedule(new Runnable() {
			public void run() {
				createTempFileHandle.cancel(true);
			}
		}, 1000, TimeUnit.MILLISECONDS);


		pool.schedule(new Runnable() {
			public void run() {
				monitor.cancel();
			}
		}, 1500, TimeUnit.MILLISECONDS);

		monitor.processEvents();  // Blocking call

		Assert.assertTrue(listener.getPaths().containsAll(actualPaths));
	}

	@Before
	public void setupTempDirectory() throws IOException {
		tempDirectory = Files.createTempDirectory("DirectoryMonitorTest");
		monitor = new DirectoryMonitor(tempDirectory);
	}


	private static final class MockListener implements DirectoryListener {

		private final List<Path> paths = new ArrayList<>();

		@Override
		public void created(final Path path) {
			paths.add(path);
		}

		private List<Path> getPaths() {
			return Collections.unmodifiableList(paths);
		}
	}
}
