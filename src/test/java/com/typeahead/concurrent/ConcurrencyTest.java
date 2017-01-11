package com.typeahead.concurrent;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typeahead.async.AsyncTaskExecutor;
import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.reader.IndexReader;
import com.typeahead.writer.IndexWriter;


public class ConcurrencyTest {
    public static int numberOfDocumentsInSet = 8;
    Logger logger;

    public ConcurrencyTest() {
        logger = LoggerFactory.getLogger(getClass());
    }

    class Worker implements Runnable {
        IndexWriter writer;
        IndexReader reader;
        Random random = new Random();

        public Worker(IndexReader reader, IndexWriter writer) {
            this.writer = writer;
            this.reader = reader;
        }

        public void run() {

            String idd = System.nanoTime() + "::" + random.nextLong();

            Document d = new Document();
            String dummyData = "testronakkhunttojustforsaketestronakkhunttojustforsakete"
                    + "stronakkhunttojustforsaketestronakkhunttojustforsaketestronakkhun"
                    + "ttojustforsaketestronakkhunttojustforsaketestronakkhunttojustfors"
                    + "stronakkhunttojustforsaketestronakkhunttojustforsaketestronakkhun"
                    + "testronakkhunttojustforsaketestronakkhunttojustforsakete"
                    + "stronakkhunttojustforsaketestronakkhunttojustforsaketestronakkhun"
                    + "ttojustforsaketestronakkhunttojustforsaketestronakkhunttojustfors"
                    + "testronakkhunttojustforsaketestronakkhunttojustforsakete";
            d.put("data", dummyData);
            d.setId(idd);

            writer.addDocument(d);
        }
    }

    @Test
    public void concurrencyTest() throws IOException {
        String indexName = "_concurrent_write_test";
        IndexConfig config = new IndexConfig(indexName);
        IndexWriter writer = new IndexWriter(config);
        IndexReader reader = new IndexReader(config);


        //adding field to make it search-able.
        config.addSearchField("data");

        try {
            writer.deleteIndex();
        } catch (IndexDoesNotExistException e1) {
            logger.error("Exception : " + e1.toString());
        }

        reader.createOrOpenIndex();

        int threadCount = 800;

        for (int i = 0; i < threadCount; i++) {
            Worker w = new Worker(reader, writer);
            AsyncTaskExecutor.submit(w);
        }

//		try {
//			Thread.currentThread().join();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

        AsyncTaskExecutor.executor.shutdown();
        while (!AsyncTaskExecutor.executor.isTerminated()) ;

        int expectedDocumentCount = numberOfDocumentsInSet * threadCount;

        while(config.getIndex().getDocumentSequenceNumber() != 801);
        System.out.println(config.getIndex().getDataMap().size());
        
        try {
            writer.deleteIndex();
        } catch (IndexDoesNotExistException e1) {
            logger.error("Exception : " + e1.toString());
        }

    }
}
