package com.typeahead.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
/**
 * Helper class for Files and Directories 
 * @author ronakkhunt
 *
 */
public class FileUtil {
	
	/**
	 * Recursive function to delete Directory having content.
	 * @param rootFile
	 */
	public static void deleteDirectoryRecursively(File rootFile) {
		File []files = rootFile.listFiles();
		if( files != null) {
			for(File file: files) {
				if(file.isFile()) {
					file.delete();
				}else if(file.isDirectory()) {
					deleteDirectoryRecursively(file);
				}
			}
		}
		rootFile.delete();
	}
	/**
	 * Returns an array of {@link File} objects of Directories in given path.
	 * @param path
	 * @return
	 */
	public static File[] getAllDirectories(String path) {
		File rootDirectory = new File(path);
		List<File> returnList = new ArrayList<File>();
		for(File file: rootDirectory.listFiles()) {
			if(file.isDirectory())
				returnList.add(file);
		}
		
		return returnList.toArray(new File[]{});
	}
	
	/**
	 * Return an array of {@link File} objects ending with given extension
	 * @param extension
	 * @return
	 */
	public static File[] getAllFilesEndingWith(String path, final String extension) {
		File rootDirectory = new File(path);
		return rootDirectory.listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(extension);
			}
		});
	}
}
