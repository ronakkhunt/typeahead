package com.typeahead.reader;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;
import com.typeahead.writer.IndexWriterUtil;

public class IndexReaderTest {
	
	IndexReader	reader;
	
	public IndexReaderTest() {
		reader = new IndexReader();
	}
	
	/**
	 * Test mostly related to {@link IndexReader#_readDataMapFile}, <br>
	 * which then can be extended to test other fields like {@link Index#fieldFSTMap}
	 * @throws IndexAlreadyExistException
	 * @throws IndexDoesNotExistException
	 */
	@Test
	public void readingSegmentTest() throws IndexAlreadyExistException, IndexDoesNotExistException {
		String indexName = "_reader_segment_test";
		IndexReader reader = new IndexReader();
		
		//making sure index does not exist already.
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExistException e) {}
		
		Index test = reader.createIndex(indexName);
		test.setMergeFactor(3);
		
		TestSet testSet = TestUtil.getTestSet(1);
		
		//Loading test data
		for(Document d: testSet.getDocuments()) {
			test.add(d);
		}
		test.getMergePolicy().flushIndex();
		
		
		test = null;
		
		test = reader.openIndex(indexName);
		Assert.assertEquals(testSet.getDocuments().size(), test.getDataMap().size());
		
		//Cleaning the test index.
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExistException e) {}
	}
	
	
	@Test
	public void createIndexTest() throws IndexDoesNotExistException {
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
		} catch (IndexDoesNotExistException e) {
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
		} catch (IndexDoesNotExistException e) {}
		
	}
}
