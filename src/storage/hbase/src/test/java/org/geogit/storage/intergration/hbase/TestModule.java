package org.geogit.storage.intergration.hbase;

import org.geogit.api.Platform;

import com.google.inject.AbstractModule;

public class TestModule extends AbstractModule {

    private Platform testPlatform;

    public TestModule(Platform testPlatform) {
        this.testPlatform = testPlatform;
    }

    @Override
    protected void configure() {
        if (testPlatform != null) {
            bind(Platform.class).toInstance(testPlatform);
        }
    }
}
