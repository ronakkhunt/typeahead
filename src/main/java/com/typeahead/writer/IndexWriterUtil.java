package com.typeahead.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.typeahead.exceptions.IndexAlreadyExistException;
import com.typeahead.index.Index;
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
	public boolean doesIndexExistance() {
		String rootPath = index.getDataDirectory() + index.getName();
		
		File rootDirectory = new File(rootPath);
		if(rootDirectory.exists() && rootDirectory.isDirectory() ) { 
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

		if(doesIndexExistance()) {
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
	private static void _createIndexFiles(String rootPath) throws IOException {
		List<String> filesList = new ArrayList<String>();
		filesList.add("/dataMap.map");
		filesList.add("/fieldFSTMap_1.map");
		filesList.add("/mapping.map");
		
		for(String fileName: filesList){
			File f = new File(rootPath+fileName);
			f.createNewFile();
		}
	}
	
	/**
	 * Return the {@link File} object for path created using prefix+name.<br>
	 * @param prefix
	 * @param name
	 * @return
	 */
	private File getFile(String prefix, String name){
		//TODO: Even ther should be some check here, what if this file does not exist?
		return new File(prefix+name);
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#dataMap}
	 * @param index
	 * @return
	 */
	public File getDataMapFile() {
		return getFile(index.getDataDirectory()+index.getName(), "/dataMap.map");
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#fieldFSTMap}
	 * @param index
	 * @return
	 */
	public File getFieldFSTMapFile() {
		return getFile(index.getDataDirectory()+index.getName(), "/fieldFSTMap.map");
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#mapping}
	 * @param index
	 * @return
	 */
	public File getMappingFile() {
		return getFile(index.getDataDirectory()+index.getName(), "/mapping.map");
	}
	
}
