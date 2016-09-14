package com.typeahead.reader;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;
import com.typeahead.writer.IndexWriter;
import com.typeahead.writer.IndexWriterUtil;

public class IndexReaderTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Test mostly related to {@link IndexReader#_readDataMapFile}, <br>
	 * which then can be extended to test other fields like {@link Index#fieldFSTMap}
	 * @throws IndexAlreadyExistException
	 * @throws IndexDoesNotExistException
	 * @throws IOException 
	 */
	@Test
	public void readingSegmentTest() throws IndexAlreadyExistException, IndexDoesNotExistException, IOException {
		
		String indexName = "_reader_segment_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriter writer = new IndexWriter(config);
		
		//making sure index does not exist already.
		try {
			reader.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
		reader.createIndex();
		
		reader.setMergeFactor(3);
		
		TestSet testSet = TestUtil.getTestSet(1);
		
		//Loading test data
		for(Document d: testSet.getDocuments()) {
			writer.addDocument(d);
		}
		writer.getMergePolicy().flushIndex();
		
		reader.close();
		
		reader.openIndex();
		Assert.assertEquals(testSet.getDocuments().size(), config.getIndex().getDataMap().size());
		
		//Cleaning the test index.
		try {
			reader.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
	}
	
	
	@Test
	public void createIndexTest() throws IndexDoesNotExistException, IOException {
		String indexName = "_create_test";
		
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());

		//TEST 1: create test
		try {
			reader.createIndex();
			Assert.assertTrue(writerUtil.doesIndexExistance());
		} catch (IndexAlreadyExistException e) {
			
		}finally{
			reader.deleteIndex();
		}
	}
	
	public void openIndexTest() throws IndexDoesNotExistException {
		String indexName = "_open_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());

		//TEST 1: open test
		//TODO: Need to think of test case for IndexReader#openIndex() method
		
		//TEST 2: Exception test
		try {
			reader.openIndex();
			exception.expect(IndexDoesNotExistException.class);
		    exception.expectMessage(indexName);
			Assert.assertTrue(writerUtil.doesIndexExistance());
		} catch (IndexDoesNotExistException e) {}
		finally{
			reader.deleteIndex();
		}
	}
	
	
	@Test
	public void deleteIndexTest() throws IOException {
		
		//TEST 1: Exception test
		try {
			IndexConfig config_test = new IndexConfig("_dummy_index");
			IndexReader reader_test = new IndexReader(config_test);
			reader_test.deleteIndex();
		} catch (IndexDoesNotExistException e) {
			Assert.assertTrue("Index: _dummy_index does not exist.".equals(e.getMessage()));
		}
		
		String indexName = "_del_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());
				
		//TEST 2: deletion test
		try {
			reader.createIndex();
		} catch (IndexAlreadyExistException e) {
			
		}
		
		try {
			reader.deleteIndex();
			Assert.assertFalse(writerUtil.doesIndexExistance());
		} catch (IndexDoesNotExistException e) {}
		
	}
}
