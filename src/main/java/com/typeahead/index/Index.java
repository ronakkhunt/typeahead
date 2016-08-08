package com.typeahead.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.typeahead.index.services.IndexAddService;

/**
 * This class will be user to reference any index and store metadata about the index.
 * @author ronakkhunt
 *
 */
public class Index {
	
	IndexAddService indexAddService;
	
	String name;
	
	String dataDirectory;
	
	Map<String, Map<String, Map<Character, IndexState>>> fieldFSTMap;
	
	Map<String, Document> dataMap;
	
	Map<String, String> mapping;
	
	public Index(String name){
		indexAddService = new IndexAddService();
		fieldFSTMap = new HashMap<String, Map<String,Map<Character,IndexState>>>();
		
		dataMap = new HashMap<String, Document>();
		mapping = new HashMap<String, String>();
		this.name = name;
		dataDirectory = "/usr/local/typeahead/";
	}
	
	public void add(Document document) {
		String id = document.getId();
		if(!dataMap.containsKey(id)){
			//put data into data map
			dataMap.put(id, document);
			
			//index data into fst
			indexAddService.indexDocument(this, document, id);
			
		}else{
			//TODO: throw a "DocumentAlreadyExist" exception
		}
	}
	
	public void delete(String documentId) {
		//deleting data from data map
		dataMap.remove(documentId);
	}
	
	/**
	 * This method will return result in Stringified JSON.
	 * @param queryString
	 * @return
	 */
	public String search(String queryString) {
		//as of now
		//this method will return ids/outputs from the fst and then,
		
		//for each ids/output, it will return data form dataMap
		return null;
	}
	
	public List<String> _getMappedField() {
		List<String> list = new ArrayList<String>();
		list.addAll(mapping.keySet());
		return list;
		
	}
	
	public Map<String, Map<Character, IndexState>> getFieldFSTMap(String field) {
		return this.fieldFSTMap.get(field);
	}
	
	/**
	 * This method return the string, which is used as first level key in FST maps of each field.
	 * @return
	 */
	public String getRootString() {
		return "|";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, Map<String, Map<Character, IndexState>>> getFieldFSTMap() {
		return fieldFSTMap;
	}

	public void setFieldFSTMap(
			Map<String, Map<String, Map<Character, IndexState>>> fieldFSTMap) {
		this.fieldFSTMap = fieldFSTMap;
	}

	public Map<String, Document> getDataMap() {
		return dataMap;
	}

	public void setDataMap(Map<String, Document> dataMap) {
		this.dataMap = dataMap;
	}

	public Map<String, String> getMapping() {
		return mapping;
	}
	public void recoverMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
		
		for(String field: _getMappedField()){
			//TODO: Need to decide whether to contains check for key
			fieldFSTMap.put(field, new HashMap<String, Map<Character,IndexState>>());
		}
		
	}

	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}
	
	
}
