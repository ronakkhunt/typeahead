package com.typeahead.reader;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExist;
import com.typeahead.index.Index;
import com.typeahead.writer.IndexWriterUtil;

public class IndexReaderTest {
	
	IndexReader	reader;
	
	public IndexReaderTest() {
		reader = new IndexReader();
	}
	
	@Test
	public void createIndexTest() throws IndexDoesNotExist {
		String indexName = "_create_test";
		Index index = new Index(indexName);
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);

		//TEST 1: create test
		try {
			index = reader.createIndex(indexName);
			Assert.assertTrue(writerUtil.doesIndexExistance());
		} catch (IndexAlreadyExistException e) {
			
		}finally{
			reader.deleteIndex(indexName);
		}
	}
	
	@Test
	public void deleteIndexTest() {
		
		String indexName = "_del_test";
		Index index = new Index(indexName);
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		//TEST 1: Exception test
		try {
			reader.deleteIndex("_dummy_index");
		} catch (IndexDoesNotExist e) {
			Assert.assertTrue("Index: _dummy_index does not exist.".equals(e.getMessage()));
		}
				
		//TEST 2: deletion test
		try {
			reader.createIndex(indexName);
		} catch (IndexAlreadyExistException e) {
			
		}
		
		try {
			reader.deleteIndex(indexName);
			Assert.assertFalse(writerUtil.doesIndexExistance());
		} catch (IndexDoesNotExist e) {}
		
	}
}
