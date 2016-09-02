package com.typeahead.merge;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;

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
		
		Assert.assertEquals(2, test.getVersion());
		
		test.getMergePolicy().flushIndex();
		
		//Cleaning the test index.
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExistException e) {}
	}
}
