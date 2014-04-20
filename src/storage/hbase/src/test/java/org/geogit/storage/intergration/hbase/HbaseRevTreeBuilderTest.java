package org.geogit.storage.intergration.hbase;

import org.geogit.di.GeogitModule;
import org.geogit.test.integration.RevTreeBuilderTest;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;

public class HbaseRevTreeBuilderTest extends RevTreeBuilderTest {

    protected Injector createInjector() {
        return Guice.createInjector(Modules.override(new GeogitModule()).with(
                new HbaseTestStorageModule()));
    }
}
