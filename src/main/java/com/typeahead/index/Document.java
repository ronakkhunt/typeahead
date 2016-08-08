package com.typeahead.index;

import java.util.HashMap;
import java.util.Map;

public class Document {
	
	Map<String, String> entity;
	String id;

	public Document() {
		entity = new HashMap<String, String>();
		//TODO: if id is not provided, need to generate automated id.
	}
	
	public Document(String id) {
		entity = new HashMap<String, String>();
		this.id = id;
	}
	
	
	public String get(String key){
		return entity.get(key);
	}
	
	public void set(String key, String value){
		entity.put(key, value);
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getEntity() {
		return entity;
	}

	public void setEntity(Map<String, String> entity) {
		this.entity = entity;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
