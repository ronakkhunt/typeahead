package com.typeahead.utils;

import java.io.File;
import java.io.FilenameFilter;
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
