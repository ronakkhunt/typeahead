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
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
		writer.createIndex();
		
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
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
	}
	
	@Test
	public void createOrOpenIndexTest() throws IOException {
		String indexName = "_create_or_open_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriter writer = new IndexWriter(config);
		
		//TEST 1: creating the Index
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
		reader.createOrOpenIndex();
		
		//TEST 2: opening the Index
		reader = new IndexReader(new IndexConfig(indexName));
		
		reader.createOrOpenIndex();
		
		//clean up
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
	}
	
	@Test
	public void openIndexTest() {
		String indexName = "_open_test";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriter writer = new IndexWriter(config);
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());

		//TEST 1: open test
		//TODO: Need to think of test case for IndexReader#openIndex() method
		
		//TEST 2: Exception test
		try {
			reader.openIndex();
			exception.expect(IndexDoesNotExistException.class);
		    exception.expectMessage(indexName);
			Assert.assertTrue(writerUtil.doesIndexExist());
		} catch (IndexDoesNotExistException e) {}
		
		
		//clean up
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
	}
	
}
