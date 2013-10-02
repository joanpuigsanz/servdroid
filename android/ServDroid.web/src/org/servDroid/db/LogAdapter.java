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

package org.servDroid.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
@Singleton
public class LogAdapter extends ServdroidDbAdapter {

	@Inject
	public LogAdapter(Context ctx) {
		super(ctx);
		if (mDbHelper == null | mDb == null) {
			open();
		}
	}

	/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is added successfully return the new rowId for
	 * that log entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * @param infoBeginning
	 *            Additional information to append at the beginning
	 * @param infoEnd
	 *            Additional information to append at the end
	 * @return rowId or -1 if failed
	 */
	public synchronized long addLog(String ip, String path, String infoBeginning, String infoEnd) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_HOSTS, ip);
		initialValues.put(KEY_PATH, path);

		initialValues.put(KEY_TIME, (new java.util.Date().getTime()));
		initialValues.put(KEY_INFOBEGINING, infoBeginning);
		initialValues.put(KEY_INFOEND, infoEnd);

		return mDb.insert(DATABASE_LOG_TABLE_, null, initialValues);
	}

	/**
	 * Create a new log entry using the the IP, request path, some extra
	 * information. If the log is added successfully return the new rowId for
	 * that log entry, otherwise return a -1 to indicate failure.
	 * 
	 * @param msg
	 *            The message to be stored in the log
	 * 
	 * @return rowId or -1 if failed
	 */
	public synchronized long addLog(LogMessage msg) {
		return addLog(msg.getIp(), msg.getPath(), msg.getInfoBegining(), msg.getInfoEnd());
	}

	/**
	 * Delete the log entry with the given rowId
	 * 
	 * @param rowId
	 *            id of log entry to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteLogRow(long rowId) {

		return mDb.delete(DATABASE_LOG_TABLE_, KEY_ROWID + "=" + rowId, null) > 0;
	}

	/**
	 * Return a Cursor over the list of the specified log entry in the database
	 * 
	 * @return Cursor over the specified log entries
	 */
	public Cursor fetchLog(int numRows) {

		return mDb.query(DATABASE_LOG_TABLE_, new String[] { KEY_ROWID, KEY_HOSTS, KEY_PATH,
				KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND }, null, null, null, null, KEY_ROWID
				+ " DESC", "" + numRows);
	}

	/**
	 * Return a Cursor over the list of all log entries in the database
	 * 
	 * @return Cursor over all log entries
	 */
	public Cursor fetchAllLog() {

		return mDb.query(DATABASE_LOG_TABLE_, new String[] { KEY_ROWID, KEY_HOSTS, KEY_PATH,
				KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND }, null, null, null, null, KEY_ROWID
				+ " DESC", null);
	}

	/**
	 * Return the ArrayList which contains the log list
	 * 
	 * @param numRows
	 *            The number of rows to get
	 * 
	 * @return ArrayList with the log entries
	 */
	public List<LogMessage> fetchLogList(int numRows) {
		if (numRows < 0) {
			return null;
		}
		Cursor c = fetchLog(numRows);

		c.moveToFirst();
		int indexIp = c.getColumnIndex(KEY_HOSTS);
		int indexPath = c.getColumnIndex(KEY_PATH);
		int indexTimeStamp = c.getColumnIndex(KEY_TIME);
		int indexInfoBegining = c.getColumnIndex(KEY_INFOBEGINING);
		int indexInfoEnd = c.getColumnIndex(KEY_INFOEND);

		int counts = c.getCount();
		ArrayList<LogMessage> locals = new ArrayList<LogMessage>();

		LogMessage log;

		for (int i = 0; i < counts; i++) {
			log = new LogMessage();
			log.setIp(c.getString(indexIp));
			log.setPath(c.getString(indexPath));
			log.setTimeStamp(c.getLong(indexTimeStamp));
			log.setInfoBegining(c.getString(indexInfoBegining));
			log.setInfoEnd(c.getString(indexInfoEnd));
			locals.add(log);
			c.moveToNext();
		}
		c.close();
		return locals;
	}

	/**
	 * Create a new log entry using the IP provided. If the log entry is created
	 * successfully return the new rowId for that log entry, otherwise return a
	 * -1 to indicate failure.
	 * 
	 * @param ip
	 *            the IP of the request
	 * @param path
	 *            the requested path
	 * 
	 * @return rowId or -1 if failed
	 */
	public long addLog(String ip, String path) {
		return addLog(ip, path, "", "");
	}

	/**
	 * Get the String line of a log entry.
	 * 
	 * @param id
	 *            The id of log entry
	 * 
	 * @return value log line
	 */
	public String getLogLine(int id) {
		Cursor c = mDb.query(true, DATABASE_LOG_TABLE_, new String[] { KEY_ROWID, KEY_HOSTS,
				KEY_PATH, KEY_TIME, KEY_INFOBEGINING, KEY_INFOEND }, KEY_ROWID + " = " + id + "",
				null, null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		// startManagingCursor(mCursor);

		if (c.getCount() == 0) {
			return "";
		}

		Date timeStamp;

		String line = "";
		line = line + c.getString(c.getColumnIndexOrThrow(KEY_HOSTS)) + " ";
		timeStamp = new Date(c.getLong(c.getColumnIndexOrThrow(KEY_TIME)));
		
		SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
	    String asGmt = df.format(timeStamp) + " GMT";
	    
		line = line + "[" + asGmt + "] ";

		line = line + "\"" + c.getString(c.getColumnIndexOrThrow(KEY_PATH)) + "\"";

		c.close();
		return line;

	}

}
