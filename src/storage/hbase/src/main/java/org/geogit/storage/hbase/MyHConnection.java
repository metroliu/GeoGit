package org.geogit.storage.hbase;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HServerAddress;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.ServerCallable;
import org.apache.hadoop.hbase.client.coprocessor.Batch.Call;
import org.apache.hadoop.hbase.client.coprocessor.Batch.Callback;
import org.apache.hadoop.hbase.ipc.CoprocessorProtocol;
import org.apache.hadoop.hbase.ipc.HMasterInterface;
import org.apache.hadoop.hbase.ipc.HRegionInterface;
import org.apache.hadoop.hbase.zookeeper.ZooKeeperWatcher;

public class MyHConnection implements HConnection {

    @Override
    public void abort(String arg0, Throwable arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isAborted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void close() throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearCaches(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearRegionCache() {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearRegionCache(byte[] arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteCachedRegionLocation(HRegionLocation arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public Configuration getConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCurrentNrHRS() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public HRegionInterface getHRegionConnection(HServerAddress arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HRegionInterface getHRegionConnection(String arg0, int arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HRegionInterface getHRegionConnection(HServerAddress arg0, boolean arg1)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HRegionInterface getHRegionConnection(String arg0, int arg1, boolean arg2)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableDescriptor getHTableDescriptor(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableDescriptor[] getHTableDescriptors(List<String> arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HMasterInterface getMaster() throws MasterNotRunningException,
            ZooKeeperConnectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getRegionCachePrefetch(byte[] arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public HRegionLocation getRegionLocation(byte[] arg0, byte[] arg1, boolean arg2)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T getRegionServerWithRetries(ServerCallable<T> arg0) throws IOException,
            RuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T getRegionServerWithoutRetries(ServerCallable<T> arg0) throws IOException,
            RuntimeException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableInterface getTable(String arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableInterface getTable(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableInterface getTable(String arg0, ExecutorService arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HTableInterface getTable(byte[] arg0, ExecutorService arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getTableNames() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ZooKeeperWatcher getZooKeeperWatcher() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isMasterRunning() throws MasterNotRunningException, ZooKeeperConnectionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTableAvailable(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTableDisabled(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isTableEnabled(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public HTableDescriptor[] listTables() throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HRegionLocation locateRegion(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HRegionLocation locateRegion(byte[] arg0, byte[] arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HRegionLocation> locateRegions(byte[] arg0) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<HRegionLocation> locateRegions(byte[] arg0, boolean arg1, boolean arg2)
            throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void prewarmRegionCache(byte[] arg0, Map<HRegionInfo, HServerAddress> arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void processBatch(List<? extends Row> arg0, byte[] arg1, ExecutorService arg2,
            Object[] arg3) throws IOException, InterruptedException {
        // TODO Auto-generated method stub

    }

    @Override
    public <R> void processBatchCallback(List<? extends Row> arg0, byte[] arg1,
            ExecutorService arg2, Object[] arg3, Callback<R> arg4) throws IOException,
            InterruptedException {
        // TODO Auto-generated method stub

    }

    @Override
    public <T extends CoprocessorProtocol, R> void processExecs(Class<T> arg0, List<byte[]> arg1,
            byte[] arg2, ExecutorService arg3, Call<T, R> arg4, Callback<R> arg5)
            throws IOException, Throwable {
        // TODO Auto-generated method stub

    }

    @Override
    public HRegionLocation relocateRegion(byte[] arg0, byte[] arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRegionCachePrefetch(byte[] arg0, boolean arg1) {
        // TODO Auto-generated method stub

    }

}
