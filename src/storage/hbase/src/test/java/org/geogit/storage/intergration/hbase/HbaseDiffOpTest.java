package org.geogit.storage.intergration.hbase;

import org.geogit.di.GeogitModule;
import org.geogit.test.integration.DiffOpTest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class HbaseDiffOpTest extends DiffOpTest {
	@Override
	protected Injector createInjector() {
		return Guice.createInjector(Modules.override(new GeogitModule()).with(
                new HbaseTestStorageModule()));
	}
}
