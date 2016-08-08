package com.typeahead.writer;

import java.io.File;
import java.io.IOException;

import com.typeahead.index.Index;

public class IndexWriterUtil {
	
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
	
	public static File getDataMapFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/dataMap.map");
	}
	
	public static File getFieldFSTMapFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/fieldFSTMap.map");
	}
	
	public static File getMappingFile(Index index) {
		return getFile(index.getDataDirectory()+index.getName(), "/mapping.map");
	}
}
