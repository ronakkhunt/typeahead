package com.typeahead.writer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.typeahead.config.IndexConfig;
import com.typeahead.constants.FileExtension;
import com.typeahead.constants.FileName;
import com.typeahead.exceptions.DocumentAlreadyExistException;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
import com.typeahead.index.IndexState;
import com.typeahead.index.services.IndexAddService;
import com.typeahead.index.services.IndexDeleteService;
import com.typeahead.merge.MergePolicy;
import com.typeahead.reader.services.IndexReaderService;
import com.typeahead.utils.FileUtil;
import com.typeahead.writer.services.IndexWriterService;


/**
 * IndexWriter class is used to write index to the disk.
 * @author ronakkhunt
 *
 */
public class IndexWriter {

	public static final Logger logger = LoggerFactory.getLogger(IndexWriter.class);
	
	/**
	 * Merge policy, which is used to merge the segment of index.
	 */
	MergePolicy mergePolicy;
	
	IndexWriterService writerService;
	IndexWriterUtil writerUtil;
	IndexConfig indexConfig;
	
	ObjectMapper mapper;
	public IndexWriter(IndexConfig config){
		
		mapper = new ObjectMapper();
		
		writerService = new IndexWriterService();
		indexConfig = config;
		mergePolicy = new MergePolicy(this);
		writerUtil = new IndexWriterUtil(indexConfig.getIndex());
	}
	
	/**
	 * Create new {@link Index}.
	 * @param indexName
	 * @return
	 * @throws IndexAlreadyExistException
	 * @throws IOException 
	 */
	public IndexWriter createIndex() throws IndexAlreadyExistException, IOException {

		writerUtil.createIndexFiles();
		
		/**
		 * Whenever the Index is getting created for the first time.
		 * Writing the empty index into file. to avoid error occuring
		 * while reading empty file in IndexReaderServices#read
		 * 
		 * NOTE: this may be removed later, when we have different method.
		 * 
		 * ERROR:
		 * 	com.fasterxml.jackson.databind.JsonMappingException: No content to map due to end-of-input
 			at [Source: ./_create_or_open_test/metadata.metadata; line: 1, column: 0]
 				at com.fasterxml.jackson.databind.JsonMappingException.from(JsonMappingException.java:261)
				at com.fasterxml.jackson.databind.ObjectMapper._initForReading(ObjectMapper.java:3829)
				at com.fasterxml.jackson.databind.ObjectMapper._readMapAndClose(ObjectMapper.java:3774)
				at com.fasterxml.jackson.databind.ObjectMapper.readValue(ObjectMapper.java:2731)
				at com.typeahead.reader.services.IndexReaderService.read(IndexReaderService.java:27)
 				...
		 *  
		 */
		writeIndex();
		
		return this;
	}
	
	/**
	 * Delete {@link Index}
	 * @param indexName
	 * @throws IndexDoesNotExistException
	 */
	public void deleteIndex() throws IndexDoesNotExistException {
		
		writerUtil.deleteIndexFiles();
	}
	
	/**
	 * Method to remove any {@link Document} from {@link Index}.
	 * @param documentId
	 * @throws IOException 
	 */
	public void deleteDocument(String documentId) throws IOException {
		IndexDeleteService indexDeleteService = new IndexDeleteService();
		
		Index index = indexConfig.getIndex();
		
		Map<String, Document> dataMap = index.getDataMap();
		
		//deleting data from data map
		//if documentId does not exist in the Map, then it will return null.
		Document document = dataMap.remove(documentId);
		
		if( document != null) {
			//delete in-memory document.
			indexDeleteService.deleteDocument(index, document, documentId);
			
			//Mark document deleted in segment.
			markDocumentDeleted(document);
		}
	}
	
	/**
	 * Method to add {@link Document}, which contains data, into Index to make it Search-able.
	 * @param document
	 */
	public void addDocument(Document document) {
		
		IndexAddService indexAddService = new IndexAddService();
		String id = document.getId();
		
		Index index = indexConfig.getIndex();
		
		Map<String, Document> dataMap = index.getDataMap();
		Map<String, Document> inMemoryDataMap = index.getInMemoryDataMap();
		
		//setting global document sequence number to document being added.
		long documentSequenceNumber = index.getAndIncrementDocumentSequenceNumber();
		document.setSequenceId(documentSequenceNumber);
		
		try {
			if(!dataMap.containsKey(id)) {
				//put data into data map
				dataMap.put(id, document);
				
				//put data into temporary in-memory data map.
				inMemoryDataMap.put(id, document);
				
				//index data into fst
				indexAddService.indexDocument(index, document, id);
				
				/**
				 * Writing each Document to disk to avoid data loss, incase of crash.
				 */
				flushDocument(document);
				
				mergePolicy.ensurePolicy();
				
				
			}else{
				throw new DocumentAlreadyExistException("Document with id: "+id+" already Exists");
			}
		}catch(DocumentAlreadyExistException e) {
			//TODO: should LOG something here.
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Marks the document as deleted. 
	 * It also requires to write the {@link Document#getId()} into {@link FileName#DATA_MAP_DELETE}<br>
	 * file. it will append the Document#id to the file.
	 * <br>
	 * This file {@link FileName#DATA_MAP_DELETE} is located inside the segment directory of segment<br>
	 * in which given document lie.
	 * @param document
	 * @throws IOException
	 */
	private void markDocumentDeleted(Document document) throws IOException {
		Index index = indexConfig.getIndex();
		
		//Set the deleted flag true.
		document.setDeleted(true);
		
		//get segment number from Document sequenceID.
		String segmentIdString = MergePolicy.getSegmentNumber(mergePolicy.getMaxMergeLevel(), mergePolicy.getMergeFactor(),
				index.getTotalDocumentCount(), document.getSequenceId());
		
		//if segmentIdString is null then document is not merged yet.
		if(segmentIdString == null) {
			//if document is not merged yet, then we need to remove file created by 
			//IndexWriter#flushDocument()
			File docFile = writerUtil.getDocumentFile(document.getId());
			docFile.delete();
		}else{
			//get file object pointing .del file
			String fileName = index.getIndexDirectoryPath() + "/" + segmentIdString + 
					"/" + FileName.DATA_MAP_DELETE.getName();
			
			File file = new File(fileName);
			
			//check if file exist.
			if(!file.exists()) {
				//if file does not exits, create new file.
				file.createNewFile();
			}
			
			//append documentId to the file.
			writerService.append(file, document.getId() + ",");
		}
	}
	
	
	/**
	 * Writes given {@link Document} onto the disk with {@link FileExtension#DATA_MAP_DOCUMENT} extension.
	 * @param document
	 */
	private void flushDocument(Document document) {
		File docFile = writerUtil.getDocumentFile(document.getId());
		//TODO: this task can be done Asynchronously as following later.
//		AsyncTaskExecutor.submit(new FlushDocumentAsync(docFile, document, writerService));
		writerService.write(docFile, document);
	}
	
	
	/**
	 * Flushes/Writes data onto disk, creating file with given version.
	 * @param newSegmentVersion
	 */
	public void flushIndex(int newSegmentVersion, int mergeLevel) {
		indexConfig.getIndex().setVersion(newSegmentVersion);
		writeIndex(mergeLevel);
		cleanDataMapDocumentFiles();
	}
	/**
	 * Writes an index assuming mergeLevel=1.
	 */
	public void writeIndex() {
		writeIndex(1);
	}
	public void writeIndex(int mergeLevel) {
		Index index = indexConfig.getIndex();
		
		File indexDataMap = writerUtil.getDataMapFile(mergeLevel);
		File fieldFSTMap = writerUtil.getFieldFSTMapFile();
		File mapping = writerUtil.getMappingFile();
		File metadata = writerUtil.getMetadataFile();
		
		writerService.write(indexDataMap, getDataMapDocumentsToFlush());
		
		writerService.write(fieldFSTMap, index.getFieldFSTMap());
		
		writerService.write(mapping, index.getMapping());
		writerService.write(metadata, index.getMetadata());
	}
	
	@SuppressWarnings({ "unchecked" })
	/**
	 * Simply creates deep copy of given HashMap by stringifying map and again converting it back
	 * to Map.
	 * @param inputMap
	 * @return
	 */
	private Map<String, Map<String, Map<Character, IndexState>>> getCopyOfFieldFSTMap1(
			Map<String, Map<String, Map<Character, IndexState>>> inputMap) {
		Map<String, Map<String, Map<Character, IndexState>>> outputMap = null;
		
		try {
			String stringMap = mapper.writeValueAsString(inputMap);
			 outputMap = mapper.readValue(stringMap, HashMap.class);
			
		} catch (JsonProcessingException e) {
			logger.error(e.toString());
		} catch (IOException e) {
			logger.error(e.toString());
		}
		return outputMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Document> getCopyOfDataMap1(Map<String, Document> inputMap) {
		
		Map<String, Document> outputMap = null;
		
		try {
			String stringMap = mapper.writeValueAsString(inputMap);
			outputMap = mapper.readValue(stringMap, HashMap.class);
			
		} catch (JsonProcessingException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return outputMap;
	}
	
	/**
	 * Returns in-memory document that has not been flushed into segment yet,
	 * however it has been written onto disk by {@link IndexWriter#flushDocument()}
	 * @return
	 */
	private Map<String, Document> getDataMapDocumentsToFlush() {
		
		//Maintaining the copy of Index#inMemoryDataMap to return result before clearing Map
		Map<String, Document> inMemoryDataMapResult = new HashMap<String, Document>();
		
		Map<String, Document> inMemoryDataMap = indexConfig.getIndex().getInMemoryDataMap();
		
		if(inMemoryDataMap != null) {
			inMemoryDataMapResult.putAll(inMemoryDataMap);
		
		//Once these document are written into segment. we need to clear this Map
		inMemoryDataMap.clear();
		}
		return inMemoryDataMapResult;
				
		
	}

	/**
	 * Merge all types of segments related to {@link Index} like <br>
	 * {@link Index#getDataMap()}, {@link Index#getMapping()} etc.
	 * @param index
	 * @param startSegmentNumber
	 * @param mergeLevel 
	 */
	public void mergeIndexData(int startSegmentNumber, int mergeLevel) {
		int mergeFactor = mergePolicy.getMergeFactor();
		mergeDataMapFile(startSegmentNumber, mergeFactor, mergeLevel);
//		for(int i = 0; i < mergeFactor; i++) {
//			read file segment with version startSegmentNumber and put all data in single object like HashMap
//			startSegmentNumber -= 1;
//		}
	}
	
	/**
	 * Merge segments related to {@link Index#getDataMap()}
	 * @param startSegmentNumber
	 * @param mergeFactor
	 * @param mergeLevel 
	 */
	@SuppressWarnings("unchecked")
	private void mergeDataMapFile(int startSegmentNumber, int mergeFactor, int mergeLevel) {
		int temp = startSegmentNumber;

		IndexReaderService readerService = new IndexReaderService();
		
		Map<String, Document> mergedMap = new HashMap<String, Document>();
		
		for(int i = 0; i < mergeFactor; i++) {
			File indexDataMap = writerUtil.getDataMapFile(startSegmentNumber, mergeLevel);
			mergedMap.putAll( readerService.read(indexDataMap, HashMap.class) );
			
			/**
			 * TODO: However this operation can be skipped to save time.
			 * 
			 * Once segment is merged, removing that segment directory.
			 * `indexDataMap` above points to the segment file, indexDataMap.getParentFile()
			 * therefore will return the segment directory containing this file.
			 */
			FileUtil.deleteDirectoryRecursively(indexDataMap.getParentFile());
			startSegmentNumber--;
		}
		
		int newSegmentVersion =  temp / mergeFactor;
		
		File mergedSegmentFile = writerUtil.getDataMapFile(newSegmentVersion, mergeLevel + 1);
		writerService.write(mergedSegmentFile, mergedMap);
		
	}
	
	/**
	 * Remove all file with extension {@link FileExtension#DATA_MAP_DOCUMENT} created in<br>
	 * {@link IndexWriter#flushDocument()}
	 * 
	 * These file represent the individual {@link Document}, which has not been merged yet.
	 * It needs to be remove when merging occur.
	 * 
	 */
	private void cleanDataMapDocumentFiles() {
		
		File[] files = FileUtil.getAllFilesEndingWith(indexConfig.getIndex().getIndexDirectoryPath(),
				FileExtension.DATA_MAP_DOCUMENT.getExtension());
		
		for(File file: files) {
			file.delete();
		}
	}
	
	public IndexConfig getIndexConfig() {
		return indexConfig;
	}
	
	public MergePolicy getMergePolicy() {
		return mergePolicy;
	}
}
