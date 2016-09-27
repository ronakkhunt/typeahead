package com.typeahead.index;

import java.util.HashMap;
import java.util.Map;
/**
 * Document class to hold data of any object into key-value manner.
 * @author ronakkhunt
 *
 */
public class Document {
	
	Map<String, String> entity;
	String id;
	Long sequenceId;

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
	
	public void put(String key, String value){
		entity.put(key, value);
	}
	
	
	public Long getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(Long sequenceId) {
		this.sequenceId = sequenceId;
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
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("2", Long.valueOf("1"));
		
		int a = (Integer)map.get("2");
		
	}
	
}
