package org.geogit.storage.intergration.hbase;

import org.geogit.di.GeogitModule;
import org.geogit.test.integration.LogOpTest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class HbaseLogOpTest extends LogOpTest {

	@Override
	protected Injector createInjector() {
		// TODO Auto-generated method stub
		 return Guice.createInjector(Modules.override(new GeogitModule()).with(
	                new HbaseTestStorageModule()));
	}

}
