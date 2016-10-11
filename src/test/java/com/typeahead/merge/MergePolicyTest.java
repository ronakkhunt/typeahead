package com.typeahead.merge;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
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
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
		
		writer.createIndex();
		reader.setMergeFactor(3);
		
		TestSet testSet = TestUtil.getTestSet(1);
		
		List<Document> testDocuments = testSet.getDocuments();
		testDocuments.addAll(TestUtil.getTestDocuments());
		
		//Loading test data
		for(Document d: testDocuments) {
			writer.addDocument(d);
		}
		
		Assert.assertEquals(4, writer.getIndexConfig().getIndex().getVersion());
		
		//Cleaning the test index.
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {}
	}
	
	@Test
	public void getSegmentNumberTest() {
		Assert.assertEquals("2.5", MergePolicy.getSegmentNumber(3, 10, 530, 475));
		Assert.assertEquals(null, MergePolicy.getSegmentNumber(3, 10, 528, 523));
		
		Assert.assertEquals("3.1", MergePolicy.getSegmentNumber(3, 10, 1000, 1000));
		Assert.assertEquals(null, MergePolicy.getSegmentNumber(3, 11, 1000, 1000));
		
		Assert.assertEquals("3.1", MergePolicy.getSegmentNumber(4, 10, 2412, 145));
		
		Assert.assertEquals("4.7", MergePolicy.getSegmentNumber(4, 10, 190320, 65535));
		Assert.assertEquals("1.2", MergePolicy.getSegmentNumber(4, 10, 190320, 190320-1));
	}
}
