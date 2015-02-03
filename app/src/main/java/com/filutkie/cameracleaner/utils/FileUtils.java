package com.filutkie.cameracleaner.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class FileUtils {

	private static final String ROOT_PATH = Environment.getExternalStorageDirectory().toString();
	private static final String DIR_ANDROID = "/Android";
	private static final String DIR_DATA = "/data";
    private static final String DIR_CACHE = "/cache";

    public static final String DIR_CAMERA = "/com.google.android.GoogleCamera";
	public static final String DIR_PANORAMA_SESSIONS = "/panorama_sessions";
	public static final String DIR_TEMP_SESSIONS = "/TEMP_SESSIONS";

	public static final String PATH_CAMERA_CACHE_FOLDER = ROOT_PATH + DIR_ANDROID + DIR_DATA
            + DIR_CAMERA + DIR_CACHE;
	public static final String PATH_CAMERA_TEMP_FOLDER = PATH_CAMERA_CACHE_FOLDER
            + DIR_TEMP_SESSIONS;
	public static final String PATH_CAMERA_PANORAMA_FOLDER = PATH_CAMERA_CACHE_FOLDER
            + DIR_PANORAMA_SESSIONS;

	/**
	 * Deletes folder and all it content recursively.
	 * 
	 * @param file
	 *            - path to file or dir.
	 * @return true or false - if succeeded.
	 */
	public static boolean deleteDir(File file) {
		if (file.isDirectory())
			for (File child : file.listFiles())
				deleteDir(child);
		return file.delete();
	}

	/**
	 * Return the size of a directory in bytes
	 * 
	 * @param dir
	 * @return <b>long</b> value of bytes.
	 */
	public static long getDirSize(File dir) {
		if (dir.exists()) {
			long result = 0;
			File[] fileList = dir.listFiles();
            for (File file : fileList) {
                // Recursive call if it's a directory
                if (file.isDirectory()) {
                    result += getDirSize(file);
                } else {
                    // Sum the file size in bytes
                    result += file.length();
                }
            }
			return result;
		}
		return 0;
	}

	/**
	 * 
	 * @param bytes
	 *            to be processed
	 * @param si
	 *            - if <i>true</i>, then byte equals 1000, not 1024. Should be
	 *            false for the most cases.
	 * @return String with size in human readable form in Kb, Mb, Gb etc.
	 */
	public static String getHumanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1)
				+ (si ? "" : "");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Get path to Google Camera /cache/ folder.
	 * 
	 * @return String - something like
	 *         <i>/storage/emulated/0/Android/data/com.google
	 *         .android.GoogleCamera</i>
	 * 
	 */
	private static String getPathToCameraCacheFolder() {
        return ROOT_PATH + DIR_ANDROID + DIR_DATA + DIR_CAMERA + DIR_CACHE;
	}

	/**
	 * Prints list of files in directory to LogCat.
	 * 
	 * @param f
	 *            - path to folder which contains files
	 */
	public static void getDirContent(File f) {
		if (f.exists()) {
			if (f.isDirectory()) {
				Log.v("FileUtlis", f.getName() + " is directory.");
			}
			File file[] = f.listFiles();
			Log.d("Files", "Size: " + file.length);
            for (File aFile : file) {
                Log.v("Files", "FileName:" + aFile.getName());
            }
		}
	}
}
