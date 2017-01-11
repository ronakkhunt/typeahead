package com.typeahead.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.typeahead.writer.IndexWriter;

/**
 * This class will be used to reference any index and store metadata about the index.
 * @author ronakkhunt
 *
 */
public class Index {
	
	private String name;
	
	/**
	 * Global sequence for {@link Document}.
	 */
	volatile private Long currentDocumentNumber;
	
	
	/**
	 * Map to store metadata about the files stored on the Disk
	 */
	private Map<String, Object> metadata;
	
	/**
	 * Base Path to store all the index data.
	 */
	private String dataDirectory;
	
	/**
	 * FST Search map for each mapped field as per {@link Index#mapping}
	 * <b>Key</b> here is name of field (ognl) and <b>Value</b> is FST map for that field.
	 */
	private Map<String, Map<String, Map<Character, IndexState>>> fieldFSTMap;
	
	/**
	 * key-value pair of indexed data.<br>
	 * <b>Key</b> here is {@link Document#id} and
	 * <b>Value</b> is {@link Document} itself.
	 * 
	 */
	private Map<String, Document> dataMap;
	
	/**
	 * Temporary map which store document, which has not been flushed onto the disk, <br>
	 * however it has been written onto disk by {@link IndexWriter#flushDocument()}
	 */
	private Map<String, Document> inMemoryDataMap;
	
	/**
	 * key-value pair to specify field to be searched.
	 * <b>Key</b> is name of the field.
	 * <b>Value</b> is of no use as of now.
	 */
	private Map<String, String> mapping;
	
	public Index(String name){
		
		fieldFSTMap = new HashMap<String, Map<String,Map<Character,IndexState>>>();
		
		dataMap = new HashMap<String, Document>();
		inMemoryDataMap = new HashMap<String, Document>();
		mapping = new HashMap<String, String>();
		
		metadata = new HashMap<String, Object>();
		metadata.put("maxMergeLevel", 3);
		metadata.put("mergeFactor", 100);
		metadata.put("version", 1);
		
		this.currentDocumentNumber = new Long(1);
		
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
	 * Returns total numbers of document in the {@link Index}.
	 * @return
	 */
	public int getTotalDocumentCount() {
		return dataMap.size();
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
	
	/**
	 * Returns global current document sequence number and increment it by one.
	 * As of now, only one thread, at a time, will be able to access this method which will ensure
	 * properly increment sequence in multi-threaded environment. 
	 * @return
	 */
	public long getAndIncrementDocumentSequenceNumber() {
		synchronized (currentDocumentNumber) {
			System.out.println(currentDocumentNumber+"--"+Thread.currentThread().getName());
			return this.currentDocumentNumber++;
		}
	}
	
	/**********************************************************************************
	 ****************************    GETTERS AND SETTERS    ***************************
	 **********************************************************************************/
	/**
	 * This method is not useful, it is being user for concurrency testing purpose only, and can
	 * be removed in future.
	 * @return
	 */
	@Deprecated
	public long getDocumentSequenceNumber() {
		return this.currentDocumentNumber;
	}
	
	public Map<String, Document> getInMemoryDataMap() {
		return inMemoryDataMap;
	}
	
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
	
	/**
	 * Add mapping for given field to {@link Index#mapping} Map.
	 * 
	 * NOTE: 
	 * this method accepts mapping for new field(s) only. any field which already exists<br/>
	 * in the mapping will not be changed.
	 * @param fieldName
	 */
	public void addFieldMapping(String fieldName) {
		Map<String, String> mapping = this.mapping;
		if(!mapping.containsKey(fieldName)){
			this.mapping.put(fieldName, "String");
			//creating a new FST map for added field
			fieldFSTMap.put(fieldName, new HashMap<String, Map<Character,IndexState>>());
		}else{
			//Ignore the mapping.
		}
			
	}
		
	/**
	 * NOTE: making this method private. as of now. Nobody should be able to set mapping directly.
	 * 
	 * @param mapping
	 */
	private void setMapping(Map<String, String> mapping) {
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
