package com.typeahead.writer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.typeahead.async.AsyncTaskExecutor;
import com.typeahead.async.FlushDocumentAsync;
import com.typeahead.config.IndexConfig;
import com.typeahead.constants.FileExtension;
import com.typeahead.exceptions.DocumentAlreadyExistException;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Document;
import com.typeahead.index.Index;
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
	
	/**
	 * Merge policy, which is used to merge the segment of index.
	 */
	MergePolicy mergePolicy;
	
	IndexWriterService writerService;
	IndexWriterUtil writerUtil;
	IndexConfig indexConfig;
	public IndexWriter(IndexConfig config){
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
	 */
	public void deleteDocument(String documentId) {
		IndexDeleteService indexDeleteService = new IndexDeleteService();
		
		Index index = indexConfig.getIndex();
		
		Map<String, Document> dataMap = index.getDataMap();
		
		//deleting data from data map
		Document document = dataMap.remove(documentId);
		
		if( document != null) {
			indexDeleteService.deleteDocument(index, document, documentId);
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
		Map<String, Document> inMemoryDataMap = index.getDataMap();
		
		//setting global document sequence number to document being added.
		Long documentSequenceNumber = index.getDocumentSequenceNumber();
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
				
				//incrementing global document sequence number
				index.setDocumentSequenceNumber(documentSequenceNumber + 1);
				
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
	 * Used before closing the index, to write remaining data onto files.
	 * It will use last version + 1 to create last segment
	 */
	public void flushIndex() {
		flushIndex(indexConfig.getIndex().getVersion() + 1);
	}
	
	/**
	 * Flushes/Writes data onto disk, creating file with given version.
	 * @param newSegmentVersion
	 */
	public void flushIndex(int newSegmentVersion) {
		indexConfig.getIndex().setVersion(newSegmentVersion);
		writeIndex();
		cleanDataMapDocumentFiles();
	}
	
	public void writeIndex() {
		Index index = indexConfig.getIndex();
		
		File indexDataMap = writerUtil.getDataMapFile();
		File fieldFSTMap = writerUtil.getFieldFSTMapFile();
		File mapping = writerUtil.getMappingFile();
		File metadata = writerUtil.getMetadataFile();
		
		writerService.write(indexDataMap, getDataMapDocumentsToFlush());
		writerService.write(fieldFSTMap, index.getFieldFSTMap());
		writerService.write(mapping, index.getMapping());
		writerService.write(metadata, index.getMetadata());
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
		
		inMemoryDataMapResult.putAll(inMemoryDataMap);
		
		//Once these document are written into segment. we need to clear this Map
		inMemoryDataMap.clear();
		
		return inMemoryDataMapResult;
				
		
	}

	/**
	 * Merge all types of segments related to {@link Index} like <br>
	 * {@link Index#getDataMap()}, {@link Index#getDataMap()}, {@link Index#getMapping()} etc.
	 * @param index
	 * @param startSegmentNumber
	 */
	public void mergeIndexData(Index index, int startSegmentNumber) {
		int mergeFactor = mergePolicy.getMergeFactor();
		mergeDataMapFile(index, startSegmentNumber, mergeFactor);
//		for(int i = 0; i < mergeFactor; i++) {
//			read file segment with version startSegmentNumber and put all data in single object like HashMap
			
//			startSegmentNumber -= 1;
//		}
	}
	
	/**
	 * Merge segments related to {@link Index#getDataMap()}
	 * @param index
	 * @param startSegmentNumber
	 * @param mergeFactor
	 */
	@SuppressWarnings("unchecked")
	private void mergeDataMapFile(Index index, int startSegmentNumber, int mergeFactor) {
		int temp = startSegmentNumber;

		IndexReaderService readerService = new IndexReaderService();
		
		Map<String, Document> mergedMap = new HashMap<String, Document>();
		
		for(int i = 0; i < mergeFactor; i++) {
			File indexDataMap = writerUtil.getDataMapFile(startSegmentNumber);
			mergedMap.putAll( readerService.read(indexDataMap, HashMap.class) );
			
			//Once segment is merged, removing that segment from directory.
			//TODO: However this operation can be skipped to save time.
			FileUtil.deleteDirectoryRecursively(indexDataMap);
			startSegmentNumber--;
		}
		
		int newSegmentVersion =  temp / mergeFactor;
		
		File mergedSegmentFile = writerUtil.getDataMapFile(newSegmentVersion);
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
		FileUtil.getAllFilesEndingWith(indexConfig.getIndex().getIndexDirectoryPath(),
				FileExtension.DATA_MAP_DOCUMENT.getExtension());
	}
	
	public IndexConfig getIndexConfig() {
		return indexConfig;
	}
	
	public MergePolicy getMergePolicy() {
		return mergePolicy;
	}
}
