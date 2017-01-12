package com.typeahead.writer;

import com.typeahead.config.IndexConfig;
import com.typeahead.constants.FileName;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.merge.MergePolicy;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestSet;
import com.typeahead.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class IndexWriterTest {

    public static final Logger logger = LoggerFactory.getLogger(IndexWriterTest.class);

	@Test
	public void deleteDocumentTest() throws IndexAlreadyExistException, IOException, IndexDoesNotExistException {
		String indexName = "_delete_doc_test";
		
		IndexConfig config = new IndexConfig(indexName);
		IndexWriter writer = new IndexWriter(config);
		Index index = config.getIndex();
		config.getIndex().setMergeFactor(3);
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(config.getIndex());
		
		//deleting the Index
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {
			logger.error(e.toString());
		}
		
		writer.createIndex();
		
		TestSet testSet = TestUtil.getTestSet(1);
		
		//this test set contains total 8 Documents.
		List<Document> testList = testSet.getDocuments();
		
		for(Document doc: testList) {
			writer.addDocument(doc);
		}
		
		//Test 1: deleting un-merged document.
		
		//getting last document for test.
		Document testDoc = testList.get(7);
		
		writer.deleteDocument(testDoc.getId());
		
		File delDocFile = writerUtil.getDocumentFile(testDoc.getId());
		
		//deleted document file should not be on disk.
		Assert.assertEquals(delDocFile.exists(), false);
		
		//Test 2: 
		
		//getting 2nd document which is merged already, for test.
		testDoc = testList.get(1);
		
		writer.deleteDocument(testDoc.getId());
		
		String segmentIdString = MergePolicy.getSegmentNumber(writer.getMergePolicy().getMaxMergeLevel(),
				writer.getMergePolicy().getMergeFactor(), 
				index.getTotalDocumentCount(), 
				testDoc.getSequenceId());
		
		String fileName = index.getIndexDirectoryPath() + "/" + segmentIdString + 
				"/" + FileName.DATA_MAP_DELETE.getName();
		
		File file = new File(fileName);
		Assert.assertEquals(file.exists(), true);
		
		//creating new Index.
		config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		writer = new IndexWriter(config);
		
		//opening the same Index again.
		reader.openIndex();
		
		//NOTE: This is a test which is more suitable to IndexReader#openIndex(), but to test
		//completed flow of IndexWriter#deleteDocument(), it is being included here.
		Assert.assertEquals(config.getIndex().getDataMap().get(testDoc.getId()), null);
		
		//deleting the Index
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {
            logger.error(e.toString());
        }
		
	}
	
	
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
            logger.error(e.toString());
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
            logger.error(e.toString());
		}
		
		try {
			writer.deleteIndex();
			Assert.assertFalse(writerUtil.doesIndexExist());
		} catch (IndexDoesNotExistException e) {
            logger.error(e.toString());
        }
		
	}
	
	@Test
	public void flushDocumentTest() throws IOException, IndexDoesNotExistException, IndexAlreadyExistException {
		String indexName = "_flush_document";
		IndexConfig config = new IndexConfig(indexName);
		IndexReader reader = new IndexReader(config);
		IndexWriter writer = new IndexWriter(config);
		
		//adding search field
		config.addSearchField("type");
		
		//making sure to delete index
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e) {
            logger.error(e.toString());
        }
		
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
		} catch (IndexDoesNotExistException e) {
            logger.error(e.toString());
        }
		
	}
}
