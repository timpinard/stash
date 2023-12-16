package com.timpinard.hibernate;

import org.junit.Test;

public class DataReaderIntegrationTest {

    @Test
    public void testReadDataNodes() {
        System.out.println("***** Creating DataReader *****");
        DataReader dataReader = new DataReader();
        System.out.println("***** Building Test Node and Children *****");
        DataNode dataNode = dataReader.buildTestNode("a", null, 5, 10);
        System.out.println("***** Saving Test Node and Children *****");
        dataReader.save(dataNode);
        System.out.println("***** Fetching Node and Children *****");
        DataNode retrievedNode = dataReader.getDataNode("a");
        System.out.println("***** Cloning Node and Children *****");
        DataNode clone = dataReader.clone(retrievedNode);
        System.out.println("***** Saving Node and Children *****");
        dataReader.save(clone);
        System.out.println("***** Deleting Node and Children *****");
        dataReader.delete(clone);
        System.out.println("***** Destroying DataReader and Session(s) *****");
        //dataReader.destroy();
    }

    @Test
    public void testCreateTestNodes() {
        DataReader dataReader = new DataReader();
        DataNode node = dataReader.buildTestNode("a", null, 5, 10);
        assert(node!=null);
    }
}