package com.blz.employeepayrolljavaio;

import java.io.File;

public class FileUtils {
	public static boolean deleteFiles(File contentsToDelete) {
		File[] allContents = contentsToDelete.listFiles();
		if (allContents != null) {
			for (File eachFile : allContents) {
				deleteFiles(eachFile);
			}
		}
		return contentsToDelete.delete();
	}
}
