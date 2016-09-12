package com.typeahead.merge;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;
import com.typeahead.writer.IndexWriter;

public class MergePolicyTest {
	/**
	 * TODO: This test case is not proper and needs to be completed when implementation
	 * of {@link MergePolicy} is completed.
	 * @throws IndexAlreadyExistException
	 * @throws IOException 
	 */
	@Test
	public void ensurePolicyTest() throws IndexAlreadyExistException, IOException {
		String indexName = "_merge_test";
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
		
		Assert.assertEquals(2, writer.getIndexConfig().getIndex().getVersion());
		
		writer.getMergePolicy().flushIndex();
		
		//Cleaning the test index.
		try {
			reader.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
	}
}
