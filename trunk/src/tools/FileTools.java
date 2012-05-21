package tools;

import java.io.File;

public class FileTools {

	/**
	 * Return the file extension. This is defined as the characters following the last '.' in the file name as well as the '.' character.
	 * @param f	File to pull from.
	 * @return	Extension if it exists, else empty string.
	 */
	public static String getExtension(File f) {
		String name = f.getName();
		int last = name.lastIndexOf('.');
		
		// No instance of the '.' character.
		if(last < 0) {
			return "";
		}
		
		String extension =  name.substring(last);
		return extension;
	}

	/**
	 * Replace the extension with the desired string.
	 * @param f Replace the file name extension.
	 * @param newExtension Extension including the '.' character.
	 * @return
	 */
	public static File replaceExtension(File f, String newExtension) {
		String name = f.getAbsolutePath();
		int last = name.lastIndexOf('.');
		
		// No instance of the '.' character.
		if(last < 0) {
			return new File(name);
		}
		return new File(name.substring(0, last) + newExtension);
	}
	
	/**
	 * Check if the file is of the given type.
	 * @param f
	 * @param ext
	 * @return
	 */
	public static boolean isType(File f, String ext) {
		
		// Ignore directories.
		if(!f.isFile()) {
			return false;
		}

		// Get the last n characters of the file name. Compare to the extension.
		String name = f.getName();
		String extension = name.substring(name.length()-ext.length());	

		// Return true if equal.
		return ext.equals(extension);
	}
		
	/**
	 * Recursively remove all file extensions from files with a given extension.
	 * @param f
	 * @param ext
	 */
	public static void recursiveRemove(File f, String ext) {
		if(f.isDirectory()) {
			for(File f2 : f.listFiles()) {
				recursiveRemove(f2,ext);
			}
		}
		else if(isType(f, ext)) {
			File fNew = replaceExtension(f, "");
			f.renameTo(fNew);
		}
	}

	/**
	 * Recursively remove all files in this directory.
	 * @param f
	 */
	public static void recursiveDelete(File f) {
		if(f.isDirectory()) {
			for(File f2 : f.listFiles()) {
				recursiveDelete(f2);
			}
		}
		f.delete();
	}
	
	/**
	 * Recursively delete all files in this directory with the given extension.
	 * @param f
	 * @param ext
	 */
	public static void recursiveDelete(File f, String ext) {
		if(f.isDirectory()) {
			for(File f2 : f.listFiles()) {
				recursiveDelete(f2,ext);
			}
		} else {
			if(isType(f, ext)) {
				f.delete();
			}
		}
	}
}
