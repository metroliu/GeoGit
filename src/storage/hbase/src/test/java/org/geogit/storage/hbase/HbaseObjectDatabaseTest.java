package org.geogit.storage.hbase;


import org.geogit.api.RevObject;
import org.geogit.api.RevTree;
import org.geogit.storage.ObjectDatabase;
import org.geogit.test.integration.RepositoryTestCase;
import org.junit.Test;
public class HbaseObjectDatabaseTest extends RepositoryTestCase{
	
	@Override
	protected void setUpInternal() throws Exception {
		// TODO Auto-generated method stub

	}
	   
    @Test
    public void testMultipleInstances() {
        ObjectDatabase db1 = geogit.getRepository().getObjectDatabase();
        RevObject obj = RevTree.EMPTY.builder(db1).build();
        assertTrue(db1.put(obj));
        assertFalse(db1.put(obj));
        RevObject revObject = db1.get(obj.getId());
        assertEquals(obj, revObject);
        db1.close();
    }
    
    public void testDe() {
        ObjectDatabase db1 = geogit.getRepository().getObjectDatabase();
        RevObject obj = RevTree.EMPTY.builder(db1).build();
        assertTrue(db1.put(obj));
        assertFalse(db1.put(obj));
        RevObject revObject = db1.get(obj.getId());
        assertEquals(obj, revObject);
        db1.close();
    }
}
