package org.geogit.storage.intergration.hbase;

import java.io.File;

import org.geogit.api.Platform;
import org.geogit.api.TestPlatform;
import org.geogit.di.GeogitModule;
import org.geogit.test.integration.CommitOpTest;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Throwables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class HbaseCommitOpTest extends CommitOpTest {
	 @Rule
	 public TemporaryFolder mockWorkingDirTempFolder = new TemporaryFolder();
	 @Override
	 protected Injector createInjector() {
	        File workingDirectory;
	        try {
	            workingDirectory = mockWorkingDirTempFolder.getRoot();
	        } catch (Exception e) {
	            throw Throwables.propagate(e);
	        }
	        Platform testPlatform = new TestPlatform(workingDirectory);
	        return Guice.createInjector(Modules.override(new GeogitModule()).with(
	                new HbaseTestStorageModule(), new TestModule(testPlatform)));
	    }
}
