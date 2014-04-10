package org.geogit.storage.hbase;

import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.geogit.api.ObjectId;
import org.geogit.api.RevCommit;
import org.geogit.api.RevFeature;
import org.geogit.api.RevFeatureType;
import org.geogit.api.RevObject;
import org.geogit.api.RevTag;
import org.geogit.api.RevTree;
import org.geogit.repository.RepositoryConnectionException;
import org.geogit.storage.BulkOpListener;
import org.geogit.storage.ConfigDatabase;
import org.geogit.storage.ObjectDatabase;
import org.geogit.storage.ObjectInserter;
import org.geogit.storage.ObjectSerializingFactory;
import org.geogit.storage.datastream.DataStreamSerializationFactory;

import com.google.inject.Inject;

public class HBaseObjectDatabase implements ObjectDatabase {
    
    
    private final HConnectionManager manager;
    
    protected final ConfigDatabase config;
    
    /*
     * @admin: To administer HBase, create and drop tables, list and alter tables, use HBaseAdmin.
     * @hbConfig: Provides access to configuration parameters
     */
    private HBaseAdmin admin = null;
    
    private Configuration hbConfig = null;
    
    /* from mongodb */
    /*
     * private MongoClient client = null; protected DB db = null; protected DBCollection collection
     * = null;
     */
    
    protected ObjectSerializingFactory serializers = new DataStreamSerializationFactory();

    private String collectionName;

    @Inject
    public HBaseObjectDatabase(ConfigDatabase config, HConnectionManager manager) {
        this(config, manager, "objects");
    }

    HBaseObjectDatabase(ConfigDatabase config, HConnectionManager manager, String collectionName) {
        this.config = config;
        this.manager = manager;
        this.collectionName = collectionName;
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub

    }

    @Override
    public void configure() throws RepositoryConnectionException {
        // TODO Auto-generated method stub

    }

    @Override
    public void checkConfig() throws RepositoryConnectionException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isOpen() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exists(ObjectId id) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<ObjectId> lookUp(String partialId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevObject get(ObjectId id) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends RevObject> T get(ObjectId id, Class<T> type) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevObject getIfPresent(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends RevObject> T getIfPresent(ObjectId id, Class<T> type)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevTree getTree(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevFeature getFeature(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevFeatureType getFeatureType(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevCommit getCommit(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RevTag getTag(ObjectId id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean put(RevObject object) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ObjectInserter newObjectInserter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean delete(ObjectId objectId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Iterator<RevObject> getAll(Iterable<ObjectId> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<RevObject> getAll(Iterable<ObjectId> ids, BulkOpListener listener) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putAll(Iterator<? extends RevObject> objects) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putAll(Iterator<? extends RevObject> objects, BulkOpListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public long deleteAll(Iterator<ObjectId> ids) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long deleteAll(Iterator<ObjectId> ids, BulkOpListener listener) {
        // TODO Auto-generated method stub
        return 0;
    }

}