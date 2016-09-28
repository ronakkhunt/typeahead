package com.typeahead.writer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.typeahead.constants.FileExtension;
import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.exceptions.IndexDoesNotExistException;
import com.typeahead.index.Index;
import com.typeahead.utils.FileUtil;
/**
 * Util class for {@Link Index} to create/read/maintain files related to index.
 * @author ronakkhunt
 *
 */
public class IndexWriterUtil {
	
	Index index;
	
	public IndexWriterUtil(Index index) {
		this.index = index;
	}
	
	/**
	 * Check whether {@link Index} exist or not.
	 * It check for existence of data directory at expected path/location.
	 * @return
	 */
	public boolean doesIndexExist() {
		String indexPath = index.getDataDirectory() + index.getName();
		
		File indexDirectory = new File(indexPath);
		if(indexDirectory.exists() && indexDirectory.isDirectory() ) { 
			return true;
		}
		return false;
	}
	
	/**
	 * Created required directory structure and initial files for {@link Index}. 
	 * @throws IndexAlreadyExistException
	 * @throws IOException
	 */
	public void createIndexFiles() throws IndexAlreadyExistException, IOException {
		String rootPath = index.getDataDirectory() + index.getName();

		if(doesIndexExist()) {
			throw new IndexAlreadyExistException("Index: "+index.getName()+" Already Exist");
		}
		
		//Creating required directories recursively.
		File rootDirectory = new File(rootPath);
		rootDirectory.mkdirs();
		
		try {
			_createIndexFiles(rootPath);
		} catch (IOException e) {
			throw e;
		}
		
	}
	
	/**
	 * Helper method to create initial files for {@link Index}
	 * @param rootPath
	 * @throws IOException
	 */
	private void _createIndexFiles(String rootPath) throws IOException {
		
		//TODO: Not sure about whether to create file here. As of now ObjectMapper of jackson
		//      taking care of exitance of file.
		List<File> filesList = new ArrayList<File>();
		filesList.add(getDataMapFile());
		filesList.add(getFieldFSTMapFile());
		filesList.add(getMappingFile());
		filesList.add(getMetadataFile());
		
		for(File f: filesList){
			f.createNewFile();
		}
	}
	
	/**
	 * Helper methos to clean all the {@link Index} related file.
	 * @throws IndexDoesNotExistException
	 */
	public void deleteIndexFiles() throws IndexDoesNotExistException {
		String indexPath = index.getDataDirectory() + index.getName();
		
		if(!doesIndexExist()) {
			throw new IndexDoesNotExistException("Index: "+index.getName()+" does not exist.");
		}
		
		File indexDirectory = new File(indexPath);
		_deleteDirectoryRecursively(indexDirectory);
		
	}
	
	/**
	 * Return the {@link File} object for path created using prefix+name.<br>
	 * @param prefix
	 * @param name
	 * @return
	 */
	private File getFile(String prefix, String name){
		return new File(prefix+name);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#dataMap}
	 * @return
	 */
	public File getDataMapFile() {
		int version = (Integer)index.getMetadata().get("version");
		return getDataMapFile(version);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#dataMap}
	 * @param version
	 * @return
	 */
	public File getDataMapFile(int version) {
		String fileName = "/dataMap_" + version + FileExtension.DATA_MAP.getExtension();
		return getFile(index.getIndexDirectoryPath(), fileName);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#fieldFSTMap}
	 * @param index
	 * @return
	 */
	public File getFieldFSTMapFile() {
		String fileName = "/fieldFSTMap"+FileExtension.FIELD_FST_MAP.getExtension();
		return getFile(index.getIndexDirectoryPath(), fileName);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#mapping}
	 * @param index
	 * @return
	 */
	public File getMappingFile() {
		String fileName = "/mapping"+FileExtension.MAPPING.getExtension();
		return getFile(index.getIndexDirectoryPath(), fileName);
	}

	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#metadata}
	 * @param index
	 * @return
	 */
	public File getMetadataFile() {
		String fileName = "/metadata"+FileExtension.METADATA.getExtension();
		return getFile(index.getIndexDirectoryPath(), fileName);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#metadata}
	 * Here file name is ID of the {@link Document}.
	 * @param index
	 * @return
	 */
	public File getDocumentFile(String id) {
		String fileName = "/"+id+FileExtension.DATA_MAP_DOCUMENT.getExtension();
		return getFile(index.getIndexDirectoryPath(), fileName);
	}
	
	private void _deleteDirectoryRecursively(File rootFile) {
		FileUtil.deleteDirectoryRecursively(rootFile);
	}
}
