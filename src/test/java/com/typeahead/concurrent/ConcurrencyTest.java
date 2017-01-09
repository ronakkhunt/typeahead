package com.typeahead.concurrent;

import com.typeahead.async.AsyncTaskExecutor;
import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.IndexState;
import com.typeahead.reader.IndexReader;
import com.typeahead.writer.IndexWriter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;


public class ConcurrencyTest {
    public static int numberOfDocumentsInSet = 8;
    Logger logger;

    ConcurrencyTest() {
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
            for (int j = 0; j < numberOfDocumentsInSet; j++) {
//			System.out.println("Starting:" + Thread.currentThread().getName());
                String idd = System.nanoTime() + "::" +
                        random.nextLong();

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
//			System.out.println("finished:" + Thread.currentThread().getName());
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

        int threadCount = 100;

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

        Map<String, Map<Character, IndexState>> fieldMap =
                config.getIndex().getFieldFSTMap("data");

        Set<String> ansList = new HashSet<String>();

        for (String key : fieldMap.keySet()) {
            Map<Character, IndexState> nextMap = fieldMap.get(key);
            for (Character cha : nextMap.keySet()) {
//				ansList.addAll(nextMap.get(cha).getOutput());
            }
        }

//		System.out.println(config.getIndex().getFieldFSTMap("data"));
        System.out.println(ansList.size());
        System.out.println(config.getIndex().getDataMap().size());
        List<Long> numLisr = new ArrayList<Long>();
        for (String key : config.getIndex().getDataMap().keySet()) {
//			System.out.print(config.getIndex().getDataMap().get(key).getId() + "<-->");
//			System.out.println(config.getIndex().getDataMap().get(key).getSequenceId());
            numLisr.add(config.getIndex().getDataMap().get(key).getSequenceId());
        }
        Collections.sort(numLisr);
        for (Long n : numLisr) {
//			System.out.println(n);
        }

        try {
            writer.deleteIndex();
        } catch (IndexDoesNotExistException e1) {
            logger.error("Exception : " + e1.toString());
        }

    }
}
