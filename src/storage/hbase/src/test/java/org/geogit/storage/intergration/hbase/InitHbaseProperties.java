package org.geogit.storage.intergration.hbase;

import org.geogit.test.integration.OnlineTestProperties;

public class InitHbaseProperties extends OnlineTestProperties {

	public InitHbaseProperties() {
		super(".geogit-hbase-tests.properties", "hbase.uri", "",
                "hbase.database", "geogit");
	}

}
