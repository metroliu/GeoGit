package org.geogit.storage.intergration.hbase;

import org.geogit.storage.ObjectDatabase;
import org.geogit.storage.hbase.HBaseObjectDatabase;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class HbaseTestStorageModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub
		// bind(HConnection.class).in(Scopes.SINGLETON);
		 bind(ObjectDatabase.class).to(HBaseObjectDatabase.class).in(
	                Scopes.SINGLETON);
	}

}
