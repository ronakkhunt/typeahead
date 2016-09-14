package com.typeahead.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.typeahead.exceptions.DocumentAlreadyExistException;
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
		
		this.name = name;
		dataDirectory = _getDataDirectoryPath();
	}
	
	/**
	 * Returns list for mapped field(s), specified in {@link Index#mapping}
	 * @return
	 */
	public List<String> getMappedField() {
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
	 * Set merge factor in {@link Index#metadata} using <b>mergeFactor</b> as key.
	 * @param value
	 */
	public void setMergeFactor(int value) {
		this.metadata.put("mergeFactor", value);
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
	
	/**********************************************************************************
	 ****************************    GETTERS AND SETTERS    ***************************
	 **********************************************************************************/
	
	/**
	 * Return base path for {@link Index} directory.
	 * @return
	 */
	public String getIndexDirectoryPath() {
		return this.getDataDirectory() + this.getName();
	}
	
	/**
	 * Set version in {@link Index#metadata} using <b>version</b> as key.
	 * @param newSegmentVersion
	 */
	public void setVersion(int newSegmentVersion) {
		this.metadata.put("version", newSegmentVersion);
	}
	
	/**
	 * Returns the current segment version from {@link Index#metadata}, using <b>version</b> as key.
	 * @return
	 */
	public int getVersion() {
		return (Integer)this.metadata.get("version");
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
	
	public void setMapping(Map<String, String> mapping) {
		this.mapping = mapping;
		
		for(String field: getMappedField()){
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

}
