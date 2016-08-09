package com.typeahead.writer;

import java.io.File;
import java.io.IOException;

import com.typeahead.index.Index;
/**
 * Util class used to create/get {@link File} object to read/write data from/to disk.
 * @author ronakkhunt
 *
 */
public class IndexWriterUtil {
	
	/**
	 * Return the file object for path created using prefix+name.<br>
	 * It will also create required nested directories.
	 * @param prefix
	 * @param name
	 * @return
	 */
	private static File getFile(String prefix, String name){
		File f = new File(prefix+name);
		if(!f.exists()){
			try {
				//Creating required parent directory recursively.
				f.getParentFile().mkdirs();

				//Creating required file
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return f;
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#dataMap}
	 * @param index
	 * @return
	 */
	public static File getDataMapFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/dataMap.map");
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#fieldFSTMap}
	 * @param index
	 * @return
	 */
	public static File getFieldFSTMapFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/fieldFSTMap.map");
	}
	
	/**
	 * Returns the {@link File} object to read/write data from/to disk for {@link Index#mapping}
	 * @param index
	 * @return
	 */
	public static File getMappingFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/mapping.map");
	}
}
