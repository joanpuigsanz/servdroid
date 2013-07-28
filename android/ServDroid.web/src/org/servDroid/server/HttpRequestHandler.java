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

package org.servDroid.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.servDroid.db.LogAdapter;
import org.servDroid.server.service.params.ServerParams;
import org.servDroid.util.Logger;

/**
 * 
 * @author Joan Puig Sanz and Jan Dunkerbeck
 * 
 */
public class HttpRequestHandler implements Runnable {

	private static final String HTTP_HEADER_IF_MODIFIED = "If-Modified-Since:";
	private static final String INDEX_FILE = "index.html";

	// Date Format pattern for HTTP headers
	private static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

	private static final String TAG = "ServDroid";

	private String mServerVersion = null;

	final static String CRLF = "\r\n";

	private Socket mSocket;
	private OutputStream mOutput;
	private BufferedReader mBr;

	private ServerParams mServerParams;
	private LogAdapter mLogAdapter;

	// SimpleDateFormat is not threadsafe, so we need an instance per thread
	private DateFormat mHttpDate = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);

	private static Map<String, String> mimeTypes;
	static {
		// Maybe there is a /etc/mime-types available?
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("htm", "text/html");
		mimeTypes.put("css", "text/css");
		mimeTypes.put("html", "text/html");
		mimeTypes.put("xhtml", "text/xhtml");
		mimeTypes.put("txt", "text/html");
		mimeTypes.put("pdf", "application/pdf");
		mimeTypes.put("jpg", "image/jpeg");
		mimeTypes.put("gif", "image/gif");
		mimeTypes.put("png", "image/png");
	}

	/**
	 * Create the object to manage the request;
	 * 
	 * @param socket
	 *            The socket were the connection is.
	 * @param serverVersion
	 *            The server version. This version will be printed in the auto
	 *            generated files (like file indexing)
	 * @throws Exception
	 */
	public HttpRequestHandler(Socket socket, LogAdapter logAdapter, ServerParams serverParams,
			String serverVersion) throws Exception {
		mServerParams = serverParams;
		mLogAdapter = logAdapter;
		mSocket = socket;
		mOutput = socket.getOutputStream();
		mBr = new BufferedReader(new InputStreamReader(socket.getInputStream()), 2 * 1024);
		mServerVersion = serverVersion;

		if (null == mServerVersion) {
			mServerVersion = "";
		} else {
			mServerVersion = "v" + mServerVersion;
		}
	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			Logger.e(TAG, "ERROR, Can not run the handler thread", e);
		}
	}

	/**
	 * Function to process the request
	 * 
	 * @throws Exception
	 */
	private void processRequest() throws Exception {

		Map<String, String> requestHeader = new HashMap<String, String>();
		Map<String, String> responseHeader = new LinkedHashMap<String, String>();

		String statusLine = null;
		String httpRequest = "";
		String error = null;
		String info = null;
		String entityBody = null;
		FileInputStream fis = null;
		boolean sendBody = true;

		try {
			// Analyze the HTTP-Request
			httpRequest = mBr.readLine();

			StringTokenizer s = new StringTokenizer(httpRequest);
			String httpCommand = s.nextToken();
			String fileGet = URLDecoder.decode(s.nextToken(), "UTF-8");
			String fileName = URLDecoder.decode(mServerParams.getWwwPath() + fileGet, "UTF-8");

			// Analyze all HTTP-Request-Headers
			while (true) {

				String headerLine = mBr.readLine();

				if (headerLine.equals(CRLF) || headerLine.equals("")) {
					break;
				}

				int idx = headerLine.indexOf(" ");
				if (idx >= 1)
					requestHeader.put(headerLine.substring(0, idx), headerLine.substring(idx + 1));

				// Logger.d(TAG, "Header line: " + headerLine);
			}

			responseHeader.put("Server:", TAG + " server");

			// Perform a GET or HEAD-Request
			if (httpCommand.equals("GET") || httpCommand.equals("HEAD")) {

				File file = new File(fileName);

				boolean fileExists = true;
				boolean isDirectory = false;

				if (file.exists()) {
					if (file.isDirectory()) {

						if (!fileName.endsWith("/")) {
							// Directories require a trailing slash. Otherwise
							// HTML links could be broken.
							statusLine = "303 See Other";
							responseHeader.put("Location:", fileGet + "/");
							sendBody = false;
							fileExists = false;
						} else {
							file = new File(fileName + INDEX_FILE);
							isDirectory = true;

							if (!file.exists()) {
								fileExists = false;
							} else {
								responseHeader.put("Content-Location:", fileGet + INDEX_FILE);
								try {
									fis = new FileInputStream(file);

								} catch (FileNotFoundException e) {
									fileExists = false;
								}
							}
						}
					} else {
						// regular file
						fis = new FileInputStream(file);
					}

				} else {
					fileExists = false;
				}

				if (fileExists && fis == null) {
					fis = new FileInputStream(file);
				}

				responseHeader.put("Date:", mHttpDate.format(System.currentTimeMillis()));
				boolean notModified = false;

				if (!sendBody) {
					;
				} else if (fileExists) {
					responseHeader.put("Last-Modified:", mHttpDate.format(file.lastModified()));

					if (requestHeader.containsKey(HTTP_HEADER_IF_MODIFIED)) {
						try {
							long ifModifiedSince = mHttpDate.parse(
									requestHeader.get(HTTP_HEADER_IF_MODIFIED)).getTime();
							if (ifModifiedSince >= file.lastModified()) {
								notModified = true;
								statusLine = "304 Not Modified";
								sendBody = false;
							}
						} catch (ParseException e) {
							// if-modified-since-header has a defective value.
							// we continue as the header is not present.
						}
					}

					if (!notModified) {
						statusLine = "200 OK";
						responseHeader.put("Content-type:", contentType(file.getName()));

						if (mServerParams.getCacheTime() > 0) {
							responseHeader.put(
									"Expires:",
									mHttpDate.format(System.currentTimeMillis()
											+ (mServerParams.getCacheTime() * 60 * 1000)));
						}
					}

				} else if (isDirectory && !fileExists && mServerParams.isFileIndexing()) { // Indexing

					statusLine = "200 OK";
					responseHeader.put("Content-type:", "text/html");

					entityBody = FileIndexing.getIndexing(fileName, fileGet, mServerVersion);

					info = "File Indexig";

				} else {
					try {
						fileName = mServerParams.getErrorPath() + "/404.html";
						fis = new FileInputStream(fileName);
						fileExists = true;
						statusLine = "404 Not Found";
						error = String.format("Object %s not found", fileGet);
						responseHeader.put("Content-type:", contentType(fileName));
						info = "File not found";

					} catch (FileNotFoundException e) {
						fis = null;
						statusLine = "404 Not Found";
						responseHeader.put("Content-type:", "text/html");
						error = String.format("Object %s not found", fileGet);
						info = "File 404.html not found";
					}

				}

				if (httpCommand.equals("HEAD")) {
					// Do not send the body. But we had to send the
					// content-length-header "as it would be"
					sendBody = false;
				}

			} else if (httpCommand.equals("POST")) {
				statusLine = "405 Method Not Allowed";
				error = "Method Not Allowed";
				responseHeader.put("Allow:", "HEAD, GET");

			} else {
				// HTTP 1.0 only defines HEAD, GET, POST.
				statusLine = "400 Bad Request";
				error = "Bad Request";
			}

		} catch (Exception e) {
			// Something happened while creating the response.
			// Ok, now HTTP 500 is the right way to inform the client.
			Logger.e(TAG, "Internal Server error", e);
			statusLine = "500 Internal Server Error";
			responseHeader.put("Content-type:", "text/html");
			error = "Internal Server Error";

			Logger.e(TAG, "Internal Server Error", e);
		}

		// Send the status line.
		mOutput.write(("HTTP/1.0 " + statusLine + CRLF).getBytes());

		if (error != null && fis == null) {

			entityBody = "<HTML>"
					+ "<HEAD><title>"
					+ statusLine
					+ "</title>"
					+ "</head><body>"
					+ "<h1>"
					+ statusLine
					+ "</h1>"
					+ "<p>"
					+ error
					+ "</p>"
					+ "<hr><ADDRESS><a href=\"http://code.google.com/p/servdroidweb/\">ServDroid.web "
					+ mServerVersion + "</a></ADDRESS>" + "</BODY></HTML>";
		}

		// lets try to find out the content-length.
		if (entityBody != null) {
			responseHeader.put("Content-Length:",
					 Integer.valueOf(entityBody.getBytes().length).toString());
		} else if (fis != null) {
			int lenght = fis.available();
			if (lenght > 0) {
				responseHeader.put("Content-Length:", Integer.valueOf(lenght).toString());
			}
		}

		// Output all response headers
		for (Entry<String, String> header : responseHeader.entrySet())
			mOutput.write((header.getKey() + " " + header.getValue() + CRLF).getBytes());

		// Send a blank line to indicate the end of the header
		// lines.
		mOutput.write(CRLF.getBytes());

		// Send the entity body.
		if (!sendBody) {
			// do not send the body.
		} else if (entityBody != null) {
			mOutput.write(entityBody.getBytes());
		} else if (fis != null) {
			sendBytes(fis, mOutput);
			fis.close();
		} else {
			// no content
		}

		if (mLogAdapter != null) {
			mLogAdapter.addLog(("" + mSocket.getInetAddress()).replace("/", ""), httpRequest,
					statusLine, info != null ? info : "");
		}

		try {
			mOutput.close();
			mBr.close();
			mSocket.close();
		} catch (Exception e) {

			Logger.e(TAG, "ERROR closing socket", e);
		}
	}

	/**
	 * Send bytes for the request
	 * 
	 * @param fis
	 *            fileInputStream to send
	 * @param os
	 *            The output Stream to use for sending
	 * @throws Exception
	 */
	private void sendBytes(FileInputStream fis, OutputStream os) throws Exception {

		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	/**
	 * Get content type
	 * 
	 * @param fileName
	 *            The file
	 * @return Content type
	 */
	private String contentType(String fileName) {

		String ext = "";
		int idx = fileName.lastIndexOf(".");
		if (idx >= 0) {
			ext = fileName.substring(idx + 1);
		}

		if (mimeTypes.containsKey(ext))
			return mimeTypes.get(ext);
		else
			return "application/octet-stream";
	}
}