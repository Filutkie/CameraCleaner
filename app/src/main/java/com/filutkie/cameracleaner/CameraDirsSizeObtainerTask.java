package com.filutkie.cameracleaner;

import android.os.AsyncTask;
import android.util.Log;

import com.filutkie.cameracleaner.utils.FileUtils;

import java.io.File;
import java.util.HashMap;


public class CameraDirsSizeObtainerTask extends
		AsyncTask<Void, Void, HashMap<String, String>> {

	private HashMap<String, String> map = new HashMap<String, String>();
	private OnTaskCompleted listener;

	public CameraDirsSizeObtainerTask(OnTaskCompleted listener) {
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	/**
	 * Computes size of every single folder and returns map with human-readable
	 * strings sizes.
	 */
	@Override
	protected HashMap<String, String> doInBackground(Void... params) {
		File cameraPath = new File(FileUtils.PATH_CAMERA_CACHE_FOLDER);
		File panoramaSessionsPath = new File(FileUtils.PATH_CAMERA_PANORAMA_FOLDER);
		File tempSessionsPath = new File(FileUtils.PATH_CAMERA_TEMP_FOLDER);

		FileUtils.getDirContent(cameraPath);

		long panoramaSessionsBytes = FileUtils.getDirSize(panoramaSessionsPath);
		long tempSessionsBytes = FileUtils.getDirSize(tempSessionsPath);
		long fullBytes = panoramaSessionsBytes + tempSessionsBytes;

		String panoSize = FileUtils.getHumanReadableByteCount(
				panoramaSessionsBytes, false);
		String tempSize = FileUtils.getHumanReadableByteCount(
				tempSessionsBytes, false);
		String fullSize = FileUtils.getHumanReadableByteCount(fullBytes, false);

		map.put(Consts.FULL_SIZE, fullSize);
		map.put(Consts.PANORAMA_SIZE, panoSize);
		map.put(Consts.TEMP_SIZE, tempSize);
		Log.v("TASK", "do in background.");
		return map;
	}

	@Override
	protected void onPostExecute(HashMap<String, String> result) {
		super.onPostExecute(result);
		listener.onTaskCompleted(map);
		Log.v("TASK", "Post execute.");
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		Log.d("Task", "Cancel");
	}
}
