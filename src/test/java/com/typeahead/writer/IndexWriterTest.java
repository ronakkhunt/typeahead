package com.typeahead.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;

public class IndexWriterTest {
	
	@Test
	public void createIndexTest() throws IndexDoesNotExistException, IOException {
		String indexName = "_create_test";
		
		IndexConfig config = new IndexConfig(indexName);
		IndexWriter writer = new IndexWriter(config);
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());

		//TEST 1: create test
		try {
			writer.createIndex();
			Assert.assertTrue(writerUtil.doesIndexExist());
		} catch (IndexAlreadyExistException e) {
			
		}finally{
			writer.deleteIndex();
		}
	}
	
	@Test
	public void deleteIndexTest() throws IOException {
		
		//TEST 1: Exception test
		try {
			IndexConfig config_test = new IndexConfig("_dummy_index");
			IndexWriter writer_test = new IndexWriter(config_test);
			writer_test.deleteIndex();
		} catch (IndexDoesNotExistException e) {
			Assert.assertTrue("Index: _dummy_index does not exist.".equals(e.getMessage()));
		}
		
		String indexName = "_del_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexWriter writer = new IndexWriter(config);
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());
				
		//TEST 2: deletion test
		try {
			writer.createIndex();
		} catch (IndexAlreadyExistException e) {
			
		}
		
		try {
			writer.deleteIndex();
			Assert.assertFalse(writerUtil.doesIndexExist());
		} catch (IndexDoesNotExistException e) {}
		
	}
	
	@Test
	public void flushDocumentTest() throws IOException, IndexDoesNotExistException, IndexAlreadyExistException {
		String indexName = "_flush_document";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriter writer = new IndexWriter(config);
		
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("type", "String");
		reader.setMapping(mapping);
		
		//making sure to delete index
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
		writer.createIndex();
		TestSet testSet = TestUtil.getTestSet(1);
		
		List<Document> testList = testSet.getDocuments().subList(0, 5);
		
		for(Document doc: testList) {
			writer.addDocument(doc);
		}
		
		config = new IndexConfig(indexName);
		reader = new IndexReader(config);
		
		reader.openIndex();

		Assert.assertEquals(testList.size(), config.getIndex().getDataMap().size());
		
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
	}
}
