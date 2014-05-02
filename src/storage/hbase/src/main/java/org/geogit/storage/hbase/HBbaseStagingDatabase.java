package org.geogit.storage.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.geogit.api.ObjectId;
import org.geogit.api.plumbing.merge.Conflict;
import org.geogit.repository.RepositoryConnectionException;
import org.geogit.storage.ConfigDatabase;
import org.geogit.storage.ForwardingStagingDatabase;
import org.geogit.storage.ObjectDatabase;
import org.geogit.storage.StagingDatabase;

import com.google.common.base.Optional;
import com.google.common.base.Suppliers;
import com.google.inject.Inject;

public class HBbaseStagingDatabase extends ForwardingStagingDatabase implements StagingDatabase {
    
    protected HTable conflicts;

    private ConfigDatabase config;

    @Inject
    public HBbaseStagingDatabase(final ConfigDatabase config, final ObjectDatabase repositoryDb) {
        super(Suppliers.ofInstance(repositoryDb), Suppliers.ofInstance(new HBaseObjectDatabase(
                config, "staging")));
        this.config = config;
    }

    @Override
    public void open() {
        super.open();
        
        String[] columnFamilies = {"path","ancestor","ours","theirs"};
        String conflictsTableName = config.get("hbase.database").get()+"-conflicts";
        try {
            conflicts = ((HBaseObjectDatabase) super.stagingDb).getCollection(conflictsTableName,columnFamilies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void configure() throws RepositoryConnectionException {
        RepositoryConnectionException.StorageType.STAGING.configure(config, "hbase", "0.1");
    }

    @Override
    public void checkConfig() throws RepositoryConnectionException {
        RepositoryConnectionException.StorageType.STAGING.verify(config, "hbase", "0.1");
    }

    @Override
    public void close() {
        super.close();
        try {
            conflicts.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conflicts = null;
    }

    @Override
    public Optional<Conflict> getConflict(@Nullable String namespace, String path) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }
        
        Get get = new Get(Bytes.toBytes(rowkey));
        get.addColumn(Bytes.toBytes("path"), Bytes.toBytes(""));
        Scan s = new Scan(get);
        ResultScanner scanner = null;
        
        try {
            scanner = conflicts.getScanner(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Result rr : scanner) {
            String getPath = new String(rr.getValue(Bytes.toBytes("path"), Bytes.toBytes("")));
            
            if( getPath.equals(path) ){
                ObjectId ancestor = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("ancestor"), Bytes.toBytes(""))) );
                ObjectId ours = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("ours"), Bytes.toBytes(""))) );
                ObjectId theirs = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("theirs"), Bytes.toBytes(""))) );
                return Optional.of(new Conflict(path, ancestor, ours, theirs));
            }
        }
        
        return Optional.absent();
    }

    @Override
    public boolean hasConflicts(String namespace) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }
        
        Get get = new Get(Bytes.toBytes(rowkey));
        Result result = null;
        
        try {
            result = conflicts.get(get);
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return !(result.isEmpty());
    }

    @Override
    public List<Conflict> getConflicts(@Nullable String namespace, @Nullable String pathFilter) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }
        
        Get get = new Get(Bytes.toBytes(rowkey));
        Scan s = new Scan(get);
        ResultScanner scanner = null;
        List<Conflict> results = new ArrayList<Conflict>();
        
        try {
            scanner = conflicts.getScanner(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for (Result rr : scanner) {
            String fullPath = new String(rr.getValue(Bytes.toBytes("path"), Bytes.toBytes("")));
            
            if( fullPath.contains(pathFilter) ){
                ObjectId ancestor = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("ancestor"), Bytes.toBytes(""))) );
                ObjectId ours = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("ours"), Bytes.toBytes(""))) );
                ObjectId theirs = ObjectId.valueOf( new String(rr.getValue(Bytes.toBytes("theirs"), Bytes.toBytes(""))) );
                results.add(new Conflict(fullPath, ancestor, ours, theirs));
            }
        }
        
        return results;
    }

    @Override
    public void addConflict(@Nullable String namespace, Conflict conflict) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }
        
        // delete if 'rowkey' already exists
        Delete del = new Delete(Bytes.toBytes(rowkey));
        try {
            conflicts.delete(del);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // set namespace as rowKey when put
        Put p = new Put(Bytes.toBytes(rowkey));
        // no qualifier
        p.add(Bytes.toBytes("path"), Bytes.toBytes(""), Bytes.toBytes(conflict.getPath()));
        p.add(Bytes.toBytes("ancestor"), Bytes.toBytes(""), Bytes.toBytes(conflict.getAncestor().toString()));
        p.add(Bytes.toBytes("ours"), Bytes.toBytes(""), Bytes.toBytes(conflict.getOurs().toString()));
        p.add(Bytes.toBytes("theirs"), Bytes.toBytes(""), Bytes.toBytes(conflict.getTheirs().toString()));
        
        try {
            conflicts.put(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeConflict(@Nullable String namespace, String path) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }

        Delete del = new Delete(Bytes.toBytes(rowkey));
        del.deleteColumn(Bytes.toBytes("path"), Bytes.toBytes(""));
        
        try {
            conflicts.delete(del);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeConflicts(@Nullable String namespace) {
        String rowkey = namespace;
        if( namespace == null ){
            rowkey = "0";
        }
        
        Delete del = new Delete(Bytes.toBytes(rowkey));
        
        try {
            conflicts.delete(del);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
