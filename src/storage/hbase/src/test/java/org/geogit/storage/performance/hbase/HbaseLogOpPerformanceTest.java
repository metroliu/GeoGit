package org.geogit.storage.performance.hbase;

import org.geogit.di.GeogitModule;
import org.geogit.storage.intergration.hbase.HbaseTestStorageModule;
import org.geogit.test.performance.LogOpPerformanceTest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class HbaseLogOpPerformanceTest extends LogOpPerformanceTest {
	 @Override
	protected Injector createInjector(){
		return Guice.createInjector(Modules.override(new GeogitModule())
				.with(new HbaseTestStorageModule()));
	}
}
