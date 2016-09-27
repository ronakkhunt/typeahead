package com.typeahead.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.typeahead.index.Document;

public class TestUtil {
	
	public static TestSet getTestSet(int id) {
		if(id == 1) {
			return getTestSet1();
		}
		return null;
	}
	
	private static TestSet getTestSet1() {
		TestSet set = new TestSet();
		List<Document> documents = new ArrayList<Document>();
		
		Document d1 = new Document("uf14422");
		d1.put("data", "HENRY THE SIXTH");
		d1.put("type", "user");
		d1.put("score", "5.89");
		documents.add(d1);
		
		Document d2 = new Document("bf3f6b7");
		d2.put("data", "(KING HENRY VI:)");
		d2.put("type", "board");
		d2.put("score", "88.04");
		documents.add(d2);
		
		Document d3 = new Document("u87c7f7");
		d3.put("data", "DUKE OF GLOUCESTER");
		d3.put("type", "user");
		d3.put("score", "7.63");
		documents.add(d3);
				
		Document d4 = new Document("q299b49");
		d4.put("data", "uncle to the King, and Protector. (GLOUCESTER:)");
		d4.put("type", "question");
		d4.put("score", "16.71");
		documents.add(d4);
				
		Document d5 = new Document("u419258");
		d5.put("data", "DUKE OF BEDFORD");
		d5.put("type", "user");
		d5.put("score", "32.61");
		documents.add(d5);
		    
		Document d6 = new Document("qba6ef5");
		d6.put("data", "uncle to the King, and Regent of France. (BEDFORD:)");
		d6.put("type", "question");
		d6.put("score", "77.33");
		documents.add(d6);
				   
		Document d7 = new Document("u24e89e");
		d7.put("data", "THOMAS BEAUFORT");
		d7.put("type", "user");
		d7.put("score", "4.40");
		documents.add(d7);
				
		Document d8 = new Document("q5a45f1");
		d8.put("data", "Duke of Exeter, great-uncle to the King. (EXETER:)");
		d8.put("type", "user");
		d8.put("score", "90.20");
		documents.add(d8);
		
		
		Map<String, List<String>> queries = new HashMap<String, List<String>>();
		
		List<String> ans_q1 = new ArrayList<String>();
		ans_q1.add("q5a45f1");
		queries.put("great-uncle to the King", ans_q1);
		
		List<String> ans_q2 = new ArrayList<String>();
		ans_q2.add("u419258");
		queries.put("bedford", ans_q2);
		
		set.setDocuments(documents);
		set.setQueries(queries);
		return set;
		
	}

	public static List<Document> getTestDocuments()
	{
		List<Document> list = new ArrayList<Document>();
		
		Document u1 = new Document("u1");
		u1.put("data", "Adam D’Angelo");
		u1.put("type", "user");
		u1.put("score", "1.0");
		list.add(u1);
		
		Document u2 = new Document("u2");
		u2.put("data", "Adam Black");
		u2.put("type", "user");
		u2.put("score", "1.0");
		list.add(u2);
		
		Document t1 = new Document("t1");
		t1.put("data", "Adam D’Angelo");
		t1.put("type", "topic");
		t1.put("score", "0.8");
		list.add(t1);
		
		Document q1 = new Document("q1");
		q1.put("data", "What does Adam D’Angelo do at Quora?");
		q1.put("type", "question");
		q1.put("score", "0.5");
		list.add(q1);
		
		Document q2 = new Document("q2");
		q2.put("data", "How did Adam D’Angelo learn programming?");
		q2.put("type", "question");
		q2.put("score", "0.5");
		list.add(q2);
		
		return list;
	}
}
