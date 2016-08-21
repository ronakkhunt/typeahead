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
		d1.set("data", "HENRY THE SIXTH");
		d1.set("type", "user");
		d1.set("score", "5.89");
		documents.add(d1);
		
		Document d2 = new Document("bf3f6b7");
		d2.set("data", "(KING HENRY VI:)");
		d2.set("type", "board");
		d2.set("score", "88.04");
		documents.add(d2);
		
		Document d3 = new Document("u87c7f7");
		d3.set("data", "DUKE OF GLOUCESTER");
		d3.set("type", "user");
		d3.set("score", "7.63");
		documents.add(d3);
				
		Document d4 = new Document("q299b49");
		d4.set("data", "uncle to the King, and Protector. (GLOUCESTER:)");
		d4.set("type", "question");
		d4.set("score", "16.71");
		documents.add(d4);
				
		Document d5 = new Document("u419258");
		d5.set("data", "DUKE OF BEDFORD");
		d5.set("type", "user");
		d5.set("score", "32.61");
		documents.add(d5);
		    
		Document d6 = new Document("qba6ef5");
		d6.set("data", "uncle to the King, and Regent of France. (BEDFORD:)");
		d6.set("type", "question");
		d6.set("score", "77.33");
		documents.add(d6);
				   
		Document d7 = new Document("u24e89e");
		d7.set("data", "THOMAS BEAUFORT");
		d7.set("type", "user");
		d7.set("score", "4.40");
		documents.add(d7);
				
		Document d8 = new Document("q5a45f1");
		d8.set("data", "Duke of Exeter, great-uncle to the King. (EXETER:)");
		d8.set("type", "user");
		d8.set("score", "90.20");
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
