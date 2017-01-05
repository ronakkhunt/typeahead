package com.typeahead.index.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestUtil;
import com.typeahead.writer.IndexWriter;
import com.typeahead.writer.IndexWriterUtil;

public class IndexSearchServiceTest {
	
	IndexWriterUtil writerUtil;
	IndexWriter writer;
	IndexReader reader;
	IndexSearchService searchService;
	IndexConfig config;
	
	public IndexSearchServiceTest() throws IOException {
		String indexName = "_test_search";
		
		config = new IndexConfig(indexName);
		
		reader = new IndexReader(config);
		writer = new IndexWriter(config);
		
		searchService = new IndexSearchService();
		
		//making sure index does not exist.
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e1) {}
		
		try {
			writer.createIndex();
		} catch (IndexAlreadyExistException e) {}
		
		//adding search Field
		config.addSearchField("data");
	}
	
	@Test
	public void searchTest() {
		
		//Index/Add Documents 
		List<Document> testDocument = TestUtil.getTestDocuments();
		for(Document doc: testDocument) {
			writer.addDocument(doc);
		}
		
		List<String> result = searchService.searchIDs(config.getIndex(), "data", "Adam");
		List<String> expected_result = new ArrayList<String>();
		expected_result.add("u2");
		expected_result.add("u1");
		expected_result.add("t1");
		expected_result.add("q2");
		expected_result.add("q1");
		
		String [] expected_result_array =  expected_result.toArray(new String[0]);
		String [] result_array =  result.toArray(new String[0]);
		
		Arrays.sort(result_array);
		Arrays.sort(expected_result_array);
		
		Assert.assertArrayEquals(expected_result_array, result_array);
		
		try {
			writer.deleteIndex();
		} catch (IndexDoesNotExistException e1) {}
	}
	
}
