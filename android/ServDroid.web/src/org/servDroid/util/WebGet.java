/*
 * Copyright (C) 2010 Joan Puig Sanz
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class WebGet {

	public WebGet() {

	}

	/**
	 * Get file from an URL
	 * 
	 * @param urlFile
	 *            The file to get
	 * @param path
	 *            Where the file will be saved
	 * @return true if the file was downloaded successful
	 */
	public boolean getURL(String urlFile, String path) {
		try {
			URL url = new URL(urlFile);
			System.out.println("Opening connection to " + urlFile + "...");
			//URLConnection urlC = url.openConnection();
			// Copy resource to local file, use remote file
			// if no local file name specified
			InputStream is = url.openStream();
			// Print info about resource
			System.out.flush();
			FileOutputStream fos = null;

			String localFile = null;
			// Get only file name
			StringTokenizer st = new StringTokenizer(url.getFile(), "/");
			while (st.hasMoreTokens())
				localFile = st.nextToken();
			fos = new FileOutputStream(path + "/" + localFile);

			int oneChar;//, count = 0;
			while ((oneChar = is.read()) != -1) {
				fos.write(oneChar);
				//count++;
			}
			is.close();
			fos.close();
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
