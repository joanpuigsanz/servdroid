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

package org.servDroid.server;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import org.servDroid.util.Encoding;


public class FileIndexing {


	/**
	 * Get the file indexing document for an specific folder
	 * 
	 * @param path
	 *            The www path
	 * @param fileGet
	 *            The path to indexing
	 * @return File indexing document
	 * @throws UnsupportedEncodingException 
	 */
	public static String getIndexing(String path, String fileGet, String version) throws UnsupportedEncodingException {
		if (version == null){
			version = "";
		}
		return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN/"
				+ "http://www.w3.org/TR/REC-html40/loose.dtd\">"
				+ "<HTML>"
				+ "<HEAD>"
				+ "<TITLE>Index of "
				+ URLDecoder.decode(fileGet, "UTF-8")
				+ "</TITLE>"
				+ "<link href=\"/default_style.css\" rel=\"stylesheet\" type=\"text/css\" />"
				+ "</HEAD>"
				+ "<H1>Index of "
				+ URLDecoder.decode(fileGet, "UTF-8")
				+ "</H1>"
				+ "</PRE><HR>"
				+ "<table><tr><th scope=\"col\">Name</th><th scope=\"col\">Last modified</th><th scope=\"col\">Size</th></tr>"
				+ listPath(path, fileGet)
				+ "</table>"
				+ "</PRE><HR>"
				+ "<ADDRESS><a href=\"http://code.google.com/p/servdroidweb/\">ServDroid.web " + version + "</a></ADDRESS>"
				+ "</BODY></HTML>";
	}

	/**
	 * For each file/folder generate a HTML line
	 * 
	 * @return HTML ready to append in to the file indexing HTML
	 */
	private static String listPath(String path, String fileGet) {
		File files[];
		DateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy HH:mm", Locale.getDefault());

		File _path = new File(path);
		files = _path.listFiles();

		String text = "";
		String tmp = "/";

		if (!fileGet.equals("/")) {
			text = "<tr><td> <A HREF=\"..\"> <IMG border=\"0\" src=\"/icons/go-back.png\" ALT=\"[<<]\"/> Parent Directory</A></td><td>"
					+ dateFormat.format(_path.lastModified())
					+ "</td><td>-</td></tr>";

		}
		sortFiles(files);
		for (int i = 0, n = files.length; i < n; i++) {

			if (fileGet.endsWith("/")) {
				tmp = "";
			} else {
				tmp = "/";
			}
			String longFileName = fileGet + tmp + files[i].getName();
			String fileName = files[i].getName();
			String fileNameLowerCase = fileName.toLowerCase(Locale.getDefault());
			// "<IMG border=\"0\" src=\"/icons/back.gif\" ALT=\"[DIR]\"> <A HREF=\"/\">Parent Directory</A>        09-Aug-2009 19:22      -</br>"
			// +
			String imgUrl = "";
			String alt = "";
			String size = null;
			
			if (files[i].isDirectory()) {
				imgUrl = "/icons/directory.png";
				alt = "DIR";
				size = "-";
			} else if (fileNameLowerCase.endsWith(".jpg")
					|| fileNameLowerCase.endsWith(".png")
					|| fileNameLowerCase.endsWith(".bmp")
					|| fileNameLowerCase.endsWith(".jpeg")
					|| fileNameLowerCase.endsWith(".gif")) {
				imgUrl = "/icons/picture.png";
				alt = "IMG";
			} else if (fileNameLowerCase.endsWith(".pdf")) {
				imgUrl = "/icons/pdf.png";
				alt = "PDF";

			} else if (fileNameLowerCase.endsWith(".doc")
					|| fileNameLowerCase.endsWith(".docx")
					|| fileNameLowerCase.endsWith(".odt")
					|| fileNameLowerCase.endsWith(".rtf")
					|| fileNameLowerCase.endsWith(".sxw")) {
				imgUrl = "/icons/document.png";
				alt = "DOC";

			} else if (fileNameLowerCase.endsWith(".css")) {
				imgUrl = "/icons/css.png";
				alt = "CSS";

			} else if (fileName.endsWith(".xls")
					|| fileNameLowerCase.endsWith(".xlsx")
					|| fileNameLowerCase.endsWith(".ods")
					|| fileNameLowerCase.endsWith(".sxc")) {
				imgUrl = "/icons/spreadsheet.png";
				alt = "CAL";

			} else if (fileNameLowerCase.endsWith(".exe")) {
				imgUrl = "/icons/executable.png";
				alt = "EXE";

			} else if (fileNameLowerCase.endsWith(".zip")
					|| fileNameLowerCase.endsWith(".rar")
					|| fileNameLowerCase.endsWith(".gz")
					|| fileNameLowerCase.endsWith(".tar")
					|| fileNameLowerCase.endsWith(".jar")
					|| fileNameLowerCase.endsWith(".bz2")
					|| fileNameLowerCase.endsWith(".lzma")
					|| fileNameLowerCase.endsWith(".7z")
					|| fileNameLowerCase.endsWith(".cbz")
					|| fileNameLowerCase.endsWith(".ar")) {
				imgUrl = "/icons/file-archiver.png";
				alt = "PAK";

			} else if (fileNameLowerCase.endsWith(".mp3")
					|| fileNameLowerCase.endsWith(".mp4")
					|| fileNameLowerCase.endsWith(".wmv")
					|| fileNameLowerCase.endsWith(".mpg")
					|| fileNameLowerCase.endsWith(".divx")
					|| fileNameLowerCase.endsWith(".ogg")
					|| fileNameLowerCase.endsWith(".avi")
					|| fileNameLowerCase.endsWith(".aac")
					|| fileNameLowerCase.endsWith(".ogm")
					|| fileNameLowerCase.endsWith(".cda")
					|| fileNameLowerCase.endsWith(".wma")
					|| fileNameLowerCase.endsWith(".wav")
					|| fileNameLowerCase.endsWith(".mid")
					|| fileNameLowerCase.endsWith(".midi")
					|| fileNameLowerCase.endsWith(".mkv")
					|| fileNameLowerCase.endsWith(".mov")
					|| fileNameLowerCase.endsWith(".3gp")
					|| fileNameLowerCase.endsWith(".asf")) {
				imgUrl = "/icons/multimedia.png";
				alt = "MUL";

			} else if (fileNameLowerCase.endsWith(".html")
					|| fileNameLowerCase.endsWith(".htm")) {
				imgUrl = "/icons/html.png";
				alt = "HTM";

			} else if (fileNameLowerCase.endsWith(".sh")
					|| fileNameLowerCase.endsWith(".vbs")
					|| fileNameLowerCase.endsWith(".py")
					|| fileNameLowerCase.endsWith(".pyc")
					|| fileNameLowerCase.endsWith(".pyd")
					|| fileNameLowerCase.endsWith(".pyo")
					|| fileNameLowerCase.endsWith(".pyw")) {
				imgUrl = "/icons/script.png";
				alt = "SCR";

			} else {
				imgUrl = "/icons/file.png";
				alt = "FILE";
			}
			if (size == null){
				size = pharseFileSize(files[i].length());
			}
			longFileName = Encoding.encode(longFileName);
			text = text
					+ "<tr><td><IMG border=\"0\" src=\"" + imgUrl +"\" ALT=\"[" + alt + "]\"> <A HREF=\""
					+ longFileName + "\">" + fileName + "</A> "
					+ "</td><td>"
					+ dateFormat.format(files[i].lastModified())
					+ "</td><td>" + size
					+ "</td></tr>";
			
		}
		return text;
	}

	/**
	 * Convert the length (in Bytes) to String
	 * 
	 * @param lengthBytes
	 * @return The length ready to be showed
	 */
	private static String pharseFileSize(long lengthBytes) {
		String size;

		if (lengthBytes <= 1024) {
			return "1 k";
		}
		long m = lengthBytes / 1024;
		int cont = 1;
		DecimalFormat numberFormat = new DecimalFormat("0");

		while (m >= 1024) {
			cont++;
			m = m / 1024;

		}

		switch ((int) cont) {
		case 1:// KB
			size = " KB";
			break;
		case 2: // MB
			size = " MB";
			break;

		case 3: // GB
			size = " GB";
			break;

		case 4: // TB
			size = " TB";
			break;

		default:
			size = " B";
			return numberFormat.format(lengthBytes) + size;
		}
		return numberFormat.format((lengthBytes / (Math.pow(1024, cont))))
				+ size;
	}
	
	private static void sortFiles(File[] files) {
	Arrays.sort(files, new Comparator<File>() {
		@Override
		public int compare(File s1, File s2) {

			String file1 = s1.getName().toLowerCase(Locale.getDefault());
			String file2 = s2.getName().toLowerCase(Locale.getDefault());

			return file1.compareTo(file2);
		}
	});
}

}
