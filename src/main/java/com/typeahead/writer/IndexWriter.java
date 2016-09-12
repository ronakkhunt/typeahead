package com.typeahead.writer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.typeahead.config.IndexConfig;
import com.typeahead.exceptions.DocumentAlreadyExistException;
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
	IndexConfig indexConfig;
	public IndexWriter(IndexConfig config){
		writerService = new IndexWriterService();
		indexConfig = config;
		mergePolicy = new MergePolicy(this);
	}
	
	/**
	 * Method to remove any {@link Document} from index.
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
		
		try {
			if(!dataMap.containsKey(id)) {
				//put data into data map
				dataMap.put(id, document);
				
				//index data into fst
				indexAddService.indexDocument(index, document, id);
				
				mergePolicy.ensurePolicy();
			}else{
				throw new DocumentAlreadyExistException("Document with id: "+id+" already Exists");
			}
		}catch(DocumentAlreadyExistException e) {
			//TODO: should LOG something here.
			System.out.println("Document already exist");
		}
	}
	
	public void writeIndex() {
		Index index = indexConfig.getIndex();
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
		
		File indexDataMap = writerUtil.getDataMapFile();
		File fieldFSTMap = writerUtil.getFieldFSTMapFile();
		File mapping = writerUtil.getMappingFile();
		File metadata = writerUtil.getMetadataFile();
		
		writerService.write(indexDataMap, index.getDataMap());
		writerService.write(fieldFSTMap, index.getFieldFSTMap());
		writerService.write(mapping, index.getMapping());
		writerService.write(metadata, index.getMetadata());
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
		
		IndexWriterUtil writerUtil = new IndexWriterUtil(index);
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
	
	public IndexConfig getIndexConfig() {
		return indexConfig;
	}
	
	public MergePolicy getMergePolicy() {
		return mergePolicy;
	}
}
