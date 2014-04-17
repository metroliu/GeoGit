package org.geogit.storage.hbase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.util.Bytes;
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
import org.geogit.storage.ObjectWriter;
import org.geogit.storage.datastream.DataStreamSerializationFactory;

import com.google.common.base.Functions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.inject.Inject;

/**
 * An Object database that uses a HBase server for persistence.
 * 
 * @see https://hbase.apache.org
 */

public class HBaseObjectDatabase implements ObjectDatabase {
    

    // private final HConnectionManager manager;
    
    // private HConnection connection;
    
    protected ConfigDatabase config;

    private HBaseAdmin client = null;
    
    protected HTable table;
    
    protected ObjectSerializingFactory serializers = new DataStreamSerializationFactory();

    private String collectionName;

    
    @Inject
    public HBaseObjectDatabase(ConfigDatabase config) {
        this(config, "objects");
    }

    HBaseObjectDatabase(ConfigDatabase config, String collectionName) {
        this.config = config;
        this.collectionName = collectionName;
    }
    
    private RevObject fromBytes(ObjectId id, byte[] buffer) {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
        RevObject result = serializers.createObjectReader().read(id, byteStream);
        return result;
    }

    private byte[] toBytes(RevObject object) {
        ObjectWriter<RevObject> writer = serializers.createObjectWriter(object.getType());
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            writer.write(object, byteStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteStream.toByteArray();
    }
    
    protected String getCollectionName() {
        return collectionName;
    }
    
    @Override
    public void open() {
        if (client != null) {
            return;
        }
        
        // String uri = config.get("hbase.uri").get();
        String database = config.get("hbase.database").get();
        Configuration hbConfig = HBaseConfiguration.create();
        // hbConfig.set("someValue", uri);
        // hbConfig.set("someValue", database);
        
        try {
            // connection = HConnectionManager.createConnection(hbConfig);
            client = new HBaseAdmin(hbConfig);
        } catch (ZooKeeperConnectionException e) {
            e.printStackTrace();
        } catch (MasterNotRunningException e){
            e.printStackTrace();
        }
        
        /*
         * in hbase, we can't create multiples databases inside the same cluster,
         * so use prefixes for table names to separate a set of tables from another set
         * other relevant three tables: 'geogit-conflicts', 'geogit-graph', 'geogit-staging'
         */
        String objectsTableName = database+"-objects";
        
        try{
            
            if (client.tableExists(objectsTableName)) {
                // System.out.println(" table 'geogit-objects' already ");
            } else {
                HTableDescriptor tableDesc = new HTableDescriptor(objectsTableName);
                tableDesc.addFamily(new HColumnDescriptor("serialized_object"));
                client.createTable(tableDesc);
            }
            
            table = new HTable(hbConfig, Bytes.toBytes(objectsTableName));
            
        } catch( IOException e ){
            e.printStackTrace();
        }
    }
    
    @Override
    public synchronized boolean isOpen() {
        return client != null;
    }

    @Override
    public void configure() throws RepositoryConnectionException {
        RepositoryConnectionException.StorageType.OBJECT.configure(config, "hbase", "0.1");
        String uri = config.get("hbase.uri").or(config.getGlobal("hbase.uri"))
                .or("hbase://localhost:2181/");
        String database = config.get("hbase.database").or(config.getGlobal("hbase.database"))
                .or("geogit");
        config.put("hbase.uri", uri);
        config.put("hbase.database", database);
    }

    @Override
    public void checkConfig() throws RepositoryConnectionException {
        RepositoryConnectionException.StorageType.OBJECT.verify(config, "hbase", "0.1");
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        client = null;
    }

    @Override
    public boolean exists(ObjectId id) {
        Get get = new Get(Bytes.toBytes(id.toString()));
        Result result = null;
        
        try {
            result = table.get(get);
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return !(result.isEmpty());
    }

    @SuppressWarnings("finally")
    @Override
    public List<ObjectId> lookUp(String partialId) {
        if (partialId.matches("[a-fA-F0-9]+")) {
            
            Scan s = new Scan();
            Filter f = new RowFilter(CompareOp.EQUAL, new SubstringComparator(partialId));
            s.setFilter(f);
            ResultScanner rs = null;
            List<ObjectId> ids = new ArrayList<ObjectId>();
            
            try {
                rs = table.getScanner(s);
                
                for(Result r : rs){
                    String rowKey = Bytes.toString(r.getRow());
                    ids.add(ObjectId.valueOf(rowKey));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally{
                rs.close();
                return ids;
            }
            
        } else {
            throw new IllegalArgumentException(
                    "Prefix query must be done with hexadecimal values only");
        }
    }

    @Override
    public RevObject get(ObjectId id) throws IllegalArgumentException {
        RevObject result = getIfPresent(id);
        if (result != null) {
            return result;
        } else {
            throw new NoSuchElementException("No object with id: " + id);
        }
    }

    @Override
    public <T extends RevObject> T get(ObjectId id, Class<T> clazz) {
        return clazz.cast(get(id));
    }

    @Override
    public RevObject getIfPresent(ObjectId id) {
        
        Get get = new Get(Bytes.toBytes(id.toString()));
        Scan s = new Scan(get);
        ResultScanner scanner = null;
        
        try {
            scanner = table.getScanner(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Result rr : scanner) {
            return fromBytes(id, rr.getValue(Bytes.toBytes("serialized_object"),
                    Bytes.toBytes(""))); // no qualifier 
        }
        return null;
    }

    @Override
    public <T extends RevObject> T getIfPresent(ObjectId id, Class<T> clazz)
            throws IllegalArgumentException {
        return clazz.cast(getIfPresent(id));
    }

    @Override
    public RevTree getTree(ObjectId id) {
        return get(id, RevTree.class);
    }

    @Override
    public RevFeature getFeature(ObjectId id) {
        return get(id, RevFeature.class);
    }

    @Override
    public RevFeatureType getFeatureType(ObjectId id) {
        return get(id, RevFeatureType.class);
    }

    @Override
    public RevCommit getCommit(ObjectId id) {
        return get(id, RevCommit.class);
    }

    @Override
    public RevTag getTag(ObjectId id) {
        return get(id, RevTag.class);
    }

    @Override
    public boolean put(final RevObject object) {
        // set oid as rowKey when put
        Put p = new Put(Bytes.toBytes(object.getId().toString()));
        // no qualifier
        p.add(Bytes.toBytes("serialized_object"), Bytes.toBytes(""), toBytes(object));
        
        try {
            table.put(p);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public ObjectInserter newObjectInserter() {
        return new ObjectInserter(this);
    }

    @Override
    public boolean delete(ObjectId objectId) {
        Delete del = new Delete(Bytes.toBytes(objectId.toString()));
        
        try {
            table.delete(del);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    private long deleteChunk(List<ObjectId> ids) {
        List<String> idStrings = Lists.transform(ids, Functions.toStringFunction());
        List<Delete> list = new ArrayList<Delete>();
        
        for (String id : idStrings) {  
            Delete del = new Delete(Bytes.toBytes(id));  
            list.add(del);  
        }  
        
        try {
            table.delete(list);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return list.size();
    }

    @Override
    public Iterator<RevObject> getAll(Iterable<ObjectId> ids) {
        return getAll(ids, BulkOpListener.NOOP_LISTENER);
    }

    @Override
    public Iterator<RevObject> getAll(final Iterable<ObjectId> ids, final BulkOpListener listener) {
        
        return new AbstractIterator<RevObject>() {
            final Iterator<ObjectId> queryIds = ids.iterator();

            @Override
            protected RevObject computeNext() {
                RevObject obj = null;
                while (obj == null) {
                    if (!queryIds.hasNext()) {
                        return endOfData();
                    }
                    ObjectId id = queryIds.next();
                    obj = getIfPresent(id);
                    if (obj == null) {
                        listener.notFound(id);
                    } else {
                        listener.found(obj.getId(), null);
                    }
                }
                return obj == null ? endOfData() : obj;
            }
        };
    }

    @Override
    public void putAll(Iterator<? extends RevObject> objects) {
        putAll(objects, BulkOpListener.NOOP_LISTENER);
    }

    @Override
    public void putAll(Iterator<? extends RevObject> objects, BulkOpListener listener) {
        while (objects.hasNext()) {
            RevObject object = objects.next();
            boolean put = put(object);
            if (put) {
                listener.inserted(object.getId(), null);
            } else {
                listener.found(object.getId(), null);
            }
        }
    }

    @Override
    public long deleteAll(Iterator<ObjectId> ids) {
        return deleteAll(ids, BulkOpListener.NOOP_LISTENER);
    }

    @Override
    public long deleteAll(Iterator<ObjectId> ids, BulkOpListener listener) {
        Iterator<List<ObjectId>> chunks = Iterators.partition(ids, 500);
        long count = 0;
        while (chunks.hasNext()) {
            count += deleteChunk(chunks.next());
        }
        return count;
    }
}