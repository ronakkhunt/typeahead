package com.typeahead.writer;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.reader.IndexReader;

public class IndexWriterTest {
	
	@Test
	public void createIndexTest() throws IndexDoesNotExistException, IOException {
		String indexName = "_create_test";
		
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
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
			IndexReader reader_test = new IndexReader(config_test);
			IndexWriter writer_test = new IndexWriter(config_test);
			writer_test.deleteIndex();
		} catch (IndexDoesNotExistException e) {
			Assert.assertTrue("Index: _dummy_index does not exist.".equals(e.getMessage()));
		}
		
		String indexName = "_del_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
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
}
