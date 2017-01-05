package com.typeahead.index;

import java.util.HashMap;
import java.util.Map;
/**
 * Document class to hold data of any object into key-value manner.
 * @author ronakkhunt
 *
 */
public class Document {
	
	/**
	 * It is a map of OGNLs in any entity to be indexed.
	 */
	private Map<String, String> entity;
	private String id;
	private Long sequenceId;
	private boolean isDeleted = false;

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
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Map<String, String> getEntity() {
		return entity;
	}
	
	public void setEntity(Map<String, String> entity) {
		this.entity = entity;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	
}
