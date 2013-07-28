/*
 * Copyright (C) 2013 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import org.servDroid.web.R;

import android.content.Context;
import android.os.Environment;

public class FilesChecker {

	private static final String TAG = "FilesGenHelper";

	public static boolean checkWwwPath(String path, Context context) {
		if (null == context && path == null) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ context.getResources().getString(R.string.default_www_path);
		}
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		boolean exist = folder.exists();
		boolean isDirectory = folder.isDirectory();
		if (!exist || (exist && !isDirectory)) {
			try {
				folder.mkdirs();
				File file = new File(path + "/index.html");
				if (!file.exists()) {
					try {
						// TODO: Move this htML content in to a file in assets?
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">"
								+ "<html><head><meta content=\"text/html; charset=UTF8\" http-equiv=\"content-type\"><title>Hello</title></head><body>"
								+ "<div style=\"text-align: center;\"><big><big><big><span style=\"font-weight: bold;\">ServDroid:<br>"
								+ "It works!<br>"
								+ "</span></big></big></big></div>"
								+ "</body></html>");
						out.close();
					} catch (Exception e) {
						Logger.e(TAG, "Error: Writing default index.html", e);
						return false;
					}
				}

			} catch (Exception e) {
				Logger.e(TAG, "Error creating folder " + folder.getAbsolutePath(), e);
				return false;
			}
		}
		return true;
	}

	public static boolean checkLogPath(String path, Context context) {
		if (null == context && path == null) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ context.getResources().getString(R.string.default_log_path);
		}
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() || (folder.exists() && !folder.isDirectory())) {
			try {
				folder.mkdir();

			} catch (Exception e) {
				Logger.e(TAG, "Error creating folder " + folder.getAbsolutePath(), e);
				return false;
			}
		}
		return true;
	}

	public static boolean checkErrorPath(String path, Context context) {
		if (null == context && path == null) {
			return false;
		}
		if (path == null) {
			path = Environment.getExternalStorageDirectory()
					+ context.getResources().getString(R.string.default_error_path);
		}
		if (path.contains("\n")) {
			return false;
		}
		File folder = new File(path);
		if (!folder.exists() || (folder.exists() && !folder.isDirectory())) {
			try {
				if (!folder.mkdirs()) {
					Logger.e(TAG, "ERROR creating th folder: " + path);
				}
				File file = new File(path + "/404.html");
				if (!file.exists()) {
					try {
						// TODO: Move this htML content in to a file in assets?
						FileWriter fstream = new FileWriter(file);
						BufferedWriter out = new BufferedWriter(fstream);
						out.write("<HTML>"
								+ "<HEAD><title>404 Not Found</title>"
								+ "</head><body> <div style=\"text-align: center;\">"
								+ "<big><big><big><span style=\"font-weight: bold;\">"
								+ "<br>ERROR 404: Document not Found<br></span></big></big></big></div>"
								+ "</BODY></HTML>");
						out.close();
					} catch (Exception e) {
						Logger.e(TAG, "Error: Writing default index.html", e);
						return false;
					}
				}

			} catch (Exception e) {
				Logger.e(TAG, "Error creating folder " + folder.getAbsolutePath(), e);
				return false;
			}
		}
		return true;
	}
}
