package com.filutkie.cameracleaner.utils;

import android.os.Environment;

import com.filutkie.cameracleaner.model.Folder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * File specific utils.
 */
public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();

    private static final String ROOT_SD_PATH = Environment.getExternalStorageDirectory().toString();
    private static final String DIR_ANDROID = "/Android";
    private static final String DIR_DATA = "/data";
    private static final String DIR_CACHE = "/cache";

    public static final String DIR_GOOGLE_CAMERA = "/com.google.android.GoogleCamera";
    public static final String DIR_PANORAMA_SESSIONS = "/panorama_sessions";
    public static final String DIR_TEMP_SESSIONS = "/TEMP_SESSIONS";

    public static final String PATH_CAMERA_CACHE_FOLDER = ROOT_SD_PATH + DIR_ANDROID + DIR_DATA
            + DIR_GOOGLE_CAMERA + DIR_CACHE;
    public static final String PATH_CAMERA_TEMP_FOLDER = PATH_CAMERA_CACHE_FOLDER
            + DIR_TEMP_SESSIONS;
    public static final String PATH_CAMERA_PANORAMA_FOLDER = PATH_CAMERA_CACHE_FOLDER
            + DIR_PANORAMA_SESSIONS;

    /**
     * Deletes folder and all it content recursively.
     *
     * @param file path to file or dir.
     * @return true or false whether succeeded.
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
     * @param dir file that is dir
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
     * @param bytes to be processed
     * @return String with size in human readable form in Kb, Mb, Gb etc.
     */
    public static String getHumanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * @return List with folders name and size.
     */
    public static List<Folder> getFoldersList() {
        File panoramaSessionsPath = new File(FileUtils.PATH_CAMERA_PANORAMA_FOLDER);
        File tempSessionsPath = new File(FileUtils.PATH_CAMERA_TEMP_FOLDER);
        long panoramaSessionsBytes = FileUtils.getDirSize(panoramaSessionsPath);
        long tempSessionsBytes = FileUtils.getDirSize(tempSessionsPath);
        List<Folder> folderList = new ArrayList<>();
        folderList.add(new Folder("panorama_sessions", panoramaSessionsBytes));
        folderList.add(new Folder("temp_sessions", tempSessionsBytes));
        return folderList;
    }

    public static long getListFullSizeInBytes(List<Folder> folderList) {
        long fullBytes = 0;
        for (Folder f : folderList)
            fullBytes += f.getSize();
        return fullBytes;
    }

    public static File getCorrespondingDir(String parentDirPath, String endingDate) {
        File parentDir = new File(parentDirPath);
        File found = null;
        for (File file : parentDir.listFiles()) {
            if (file.getName().endsWith(endingDate)) {
                found = new File(file.getPath());
            }
        }
        return found;
    }
}
