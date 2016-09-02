package com.typeahead.index.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.IndexReader;
import com.typeahead.util.TestUtil;
import com.typeahead.writer.IndexWriterUtil;

public class IndexSearchServiceTest {
	
	IndexWriterUtil writerUtil;
	IndexReader reader;
	IndexSearchService searchService;
	Index index;
	
	public IndexSearchServiceTest() throws IOException {
		String indexName = "_test_search";
		reader = new IndexReader();
		searchService = new IndexSearchService();
		
		//making sure index does not exist.
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExistException e1) {}
		
		try {
			this.index = reader.createIndex(indexName);
		} catch (IndexAlreadyExistException e) {}
		
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExistException e1) {}
		
		//set mapping
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("data", "String");
		index.setMapping(mapping);

	}
	
	@Test
	public void searchTest() {
		
		//Index/Add Documents 
		List<Document> testDocument = TestUtil.getTestDocuments();
		for(Document doc: testDocument) {
			index.add(doc);
		}
		
		List<String> result = searchService.searchIDs(index, "data", "Adam");
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
	}
	
}
