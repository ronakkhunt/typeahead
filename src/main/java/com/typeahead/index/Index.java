package com.typeahead.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.typeahead.index.services.IndexAddService;
import com.typeahead.index.services.IndexDeleteService;
import com.typeahead.index.services.IndexSearchService;
import com.typeahead.merge.MergePolicy;

/**
 * This class will be used to reference any index and store metadata about the index.
 * @author ronakkhunt
 *
 */
public class Index {
	
	String name;
	
	/**
	 * 
	 */
	MergePolicy mergePolicy;
	
	/**
	 * Map to store metadata about the files stored on the Disk
	 */
	Map<String, Object> metadata;
	
	/**
	 * Base Path to store all the index data.
	 */
	String dataDirectory;
	
	/**
	 * FST Search map for each mapped field as per {@link Index#mapping}
	 * <b>Key</b> here is name of field (ognl) and <b>Value</b> is FST map for that field.
	 */
	Map<String, Map<String, Map<Character, IndexState>>> fieldFSTMap;
	
	/**
	 * key-value pair of indexed data.<br>
	 * <b>Key</b> here is {@link Document#id} and
	 * <b>Value</b> is {@link Document} itself.
	 * 
	 */
	Map<String, Document> dataMap;
	
	/**
	 * key-value pair to specify field to be searched.
	 * <b>Key</b> is name of the field.
	 * <b>Value</b> is of no use as of now.
	 */
	Map<String, String> mapping;
	
	public Index(String name){
		
		fieldFSTMap = new HashMap<String, Map<String,Map<Character,IndexState>>>();
		
		dataMap = new HashMap<String, Document>();
		mapping = new HashMap<String, String>();
		
		metadata = new HashMap<String, Object>();
		metadata.put("maxDocCount", 1000);
		metadata.put("mergeFactor", 10);
		metadata.put("version", 1);
		
		mergePolicy = new MergePolicy(this);
		
		this.name = name;
		dataDirectory = _getDataDirectoryPath();
	}

	/**
	 * Method to add {@link Document}, which contains data, into Index to make it Search-able.
	 * @param document
	 */
	public void add(Document document) {
		
		IndexAddService indexAddService = new IndexAddService();
		String id = document.getId();
		if(!dataMap.containsKey(id)){
			//put data into data map
			dataMap.put(id, document);
			
			//index data into fst
			indexAddService.indexDocument(this, document, id);
			mergePolicy.ensurePolicy();
		}else{
			//TODO: throw a "DocumentAlreadyExist" exception
		}
	}
	
	/**
	 * Method to remove any {@link Document} from index.
	 * @param documentId
	 */
	public void delete(String documentId) {
		IndexDeleteService indexDeleteService = new IndexDeleteService();
		
		//deleting data from data map
		Document document = dataMap.remove(documentId);
		
		if( document != null) {
			indexDeleteService.deleteDocument(this, document, documentId);
		}
	}
	
	/**
	 * This method will return result in Stringified JSON.
	 * @param queryString
	 * @return
	 */
	public List<Document> search(String field, String queryString) {
		IndexSearchService searchService = new IndexSearchService();
		return searchService.searchDocuments(this, field, queryString);
	}
	
	public List<String> _getMappedField() {
		List<String> list = new ArrayList<String>();
		list.addAll(mapping.keySet());
		return list;
		
	}
	
	/**
	 * Return the FST map for given field.
	 * @param field
	 * @return
	 */
	public Map<String, Map<Character, IndexState>> getFieldFSTMap(String field) {
		return this.fieldFSTMap.get(field);
	}
	
	public void recoverMapping(Map<String, String> mapping) {
		this.mapping = mapping;
	}
	
	/**
	 * This method return the string, which is used as first level key in FST maps of each field.
	 * @return
	 */
	public String getRootString() {
		return "|";
	}
	
	private String _getDataDirectoryPath() {
		dataDirectory = "./";
		return dataDirectory;
	}
	
	/**********************************************************
	 **************    GETTERS AND SETTERS    *****************
	 **********************************************************/

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
	
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
		
		for(String field: _getMappedField()){
			//TODO: Need to decide whether to check for contains key
			fieldFSTMap.put(field, new HashMap<String, Map<Character,IndexState>>());
		}
		
	}

	public String getDataDirectory() {
		return dataDirectory;
	}

	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

	public MergePolicy getMergePolicy() {
		return mergePolicy;
	}
	
	
	
}
