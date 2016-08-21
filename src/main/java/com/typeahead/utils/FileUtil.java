package com.typeahead.utils;

import java.io.File;
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
}
