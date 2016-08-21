package com.typeahead.index.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExist;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.reader.IndexReader;
import com.typeahead.writer.IndexWriterUtil;

public class IndexSearchServiceTest {
	
	IndexWriterUtil writerUtil;
	IndexReader reader;
	IndexSearchService searchService;
	Index index;
	
	public IndexSearchServiceTest() {
		String indexName = "_test_search";
		reader = new IndexReader();
		searchService = new IndexSearchService();
		
		//making sure index does not exist.
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExist e1) {}
		
		try {
			this.index = reader.createIndex(indexName);
		} catch (IndexAlreadyExistException e) {}
		
		try {
			reader.deleteIndex(indexName);
		} catch (IndexDoesNotExist e1) {}
		
		//set mapping
		Map<String, String> mapping = new HashMap<String, String>();
		mapping.put("data", "String");
		index.setMapping(mapping);

	}
	
	@Test
	public void searchTest() {
		
		//Index/Add Documents 
		List<Document> testDocument = _getTestDocuments();
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
	
	
	private List<Document> _getTestDocuments()
	{
		List<Document> list = new ArrayList<Document>();
		
		Document u1 = new Document("u1");
		u1.set("data", "Adam D’Angelo");
		u1.set("type", "user");
		u1.set("score", "1.0");
		list.add(u1);
		
		Document u2 = new Document("u2");
		u2.set("data", "Adam Black");
		u2.set("type", "user");
		u2.set("score", "1.0");
		list.add(u2);
		
		Document t1 = new Document("t1");
		t1.set("data", "Adam D’Angelo");
		t1.set("type", "topic");
		t1.set("score", "0.8");
		list.add(t1);
		
		Document q1 = new Document("q1");
		q1.set("data", "What does Adam D’Angelo do at Quora?");
		q1.set("type", "question");
		q1.set("score", "0.5");
		list.add(q1);
		
		Document q2 = new Document("q2");
		q2.set("data", "How did Adam D’Angelo learn programming?");
		q2.set("type", "question");
		q2.set("score", "0.5");
		list.add(q2);
		
		return list;
	}
}
