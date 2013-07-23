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

package org.servDroid.db;

import org.servDroid.util.Logger;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class ServdroidDbAdapter extends Activity {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_HOSTS = "host";
	public static final String KEY_PATH = "path";
	public static final String KEY_TIME = "time";
	public static final String KEY_INFOBEGINING = "info_begining";
	public static final String KEY_INFOEND = "info_end";
	public static final String KEY_SETTINGS_PARAM = "settings_param";
	public static final String KEY_VALUE = "value";

	protected DatabaseHelper mDbHelper;
	protected SQLiteDatabase mDb;

	protected static final String DATABASE_NAME = "servdroid_web";
	protected static final String DATABASE_LOG_TABLE_ = "web_log";
	protected static final String DATABASE_TABLE_BLACKLIST = "black_list";
	protected static final String DATABASE_TABLE_WHITELIST = "white_list";

	// For old versions:
	protected static final String DATABASE_TABLE_SETTINGS = "settings_list";
	//

	protected static final int DATABASE_VERSION = 2;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE_LOG = "create table " + DATABASE_LOG_TABLE_ + " ( "
			+ KEY_ROWID + " integer primary key autoincrement, " + KEY_HOSTS + " text not null, "
			+ KEY_PATH + " text not null, " + KEY_TIME + " long, " + KEY_INFOBEGINING
			+ " text not null, " + KEY_INFOEND + "  text not null);";
	private static final String DATABASE_CREATE_BLACK_LIST = "create table "
			+ DATABASE_TABLE_BLACKLIST + " ( " + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_HOSTS + " text not null);";
	private static final String DATABASE_CREATE_WHITE_LIST = "create table "
			+ DATABASE_TABLE_WHITELIST + " ( " + KEY_ROWID + " integer primary key autoincrement, "
			+ KEY_HOSTS + " text not null);";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE_LOG);
			db.execSQL(DATABASE_CREATE_BLACK_LIST);
			db.execSQL(DATABASE_CREATE_WHITE_LIST);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Logger.w("Upgrading database from version " + oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LOG_TABLE_);
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_BLACKLIST);
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_WHITELIST);
			// db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_SETTINGS);
			onCreate(db);
		}
	}

	/**
	 * Constructor - takes the Context to allow the database to be
	 * opened/created
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	public ServdroidDbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the log entries database. If it cannot be opened, try to create a
	 * new instance of the database. If it cannot be created, throw an exception
	 * to signal the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialisation call)
	 * @throws SQLException
	 *             if the database could not be opened or created
	 */
	public ServdroidDbAdapter open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		if (mDb == null) {
			mDb = mDbHelper.getWritableDatabase();
		}

		return this;
	}

	/**
	 * Close the database
	 */
	public void close() {
		mDbHelper.close();
		mDb.close();
	}

	/**
	 * Delete log table from database
	 */
	public void deleteTableLog() {
		mDb.execSQL("DELETE FROM " + DATABASE_LOG_TABLE_);
	}

	/**
	 * 
	 * NOT IMPLEMENTED. Add an IP to blacklist server.
	 * 
	 * @param rowIdLog
	 *            the id of log entry which contains the IP.
	 * @return rowId or -1 if failed, -2 if it already exists
	 */
	public long addIpBlackList(long rowIdLog) {

		Cursor cursor = fetchEntry(rowIdLog);
		startManagingCursor(cursor);

		String mIp = cursor.getString(cursor.getColumnIndexOrThrow(ServdroidDbAdapter.KEY_HOSTS));

		// Cursor blackListCursor = fetchEntryBlackList(mIp);
		// startManagingCursor(blackListCursor);
		cursor.close();
		stopManagingCursor(cursor);
		return addIpBlackList(mIp);

	}

	/**
	 * NOT IMPLEMENTED. Add an IP to blacklist server.
	 * 
	 * @param ip
	 *            The IP of the client.
	 * @return rowId or -1 if failed, -2 if it already exists
	 */
	public long addIpBlackList(String ip) {

		Cursor blackListCursor = fetchEntryBlackList(ip);
		startManagingCursor(blackListCursor);

		if (blackListCursor.getCount() == 0) {
			Logger.d(ip + " added to blacklist");
			ContentValues initialValues = new ContentValues();
			initialValues.put(KEY_HOSTS, ip);

			blackListCursor.close();
			stopManagingCursor(blackListCursor);
			return mDb.insert(DATABASE_TABLE_BLACKLIST, null, initialValues);

		} else {
			Logger.d("IP already in the list");
			blackListCursor.close();
			stopManagingCursor(blackListCursor);
			return -2;

		}

	}

	/**
	 * Return a Cursor positioned at the log entry that matches the given rowId
	 * 
	 * @param rowId
	 *            id of log entry to retrieve
	 * @return Cursor positioned to matching log entry, if found
	 * @throws SQLException
	 *             if log entry could not be found/retrieved
	 */
	public Cursor fetchEntry(long rowId) throws SQLException {

		Cursor cursor =

		mDb.query(true, DATABASE_LOG_TABLE_, new String[] { KEY_ROWID, KEY_HOSTS, KEY_PATH },
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;

	}

	/**
	 * Return a Cursor positioned at the blacklist IP that matches the given
	 * rowId
	 * 
	 * @param rowId
	 *            id of blacklist IP to retrieve
	 * @return Cursor positioned to matching blacklist IP, if found
	 * @throws SQLException
	 *             if blacklist IP could not be found/retrieved
	 */
	public Cursor fetchEntryBlackList(long rowId) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE_BLACKLIST, new String[] { KEY_ROWID, KEY_HOSTS }, KEY_ROWID
				+ "=" + rowId, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Return a Cursor positioned at the blacklist IP that matches the given
	 * rowId
	 * 
	 * @param ip
	 *            The blacklist IP
	 * @return Cursor positioned to matching blacklist IP, if found
	 * @throws SQLException
	 */
	public Cursor fetchEntryBlackList(String ip) throws SQLException {

		Cursor mCursor = mDb.query(true, DATABASE_TABLE_BLACKLIST, new String[] { KEY_HOSTS },
				KEY_HOSTS + "= \"" + ip + "\"", null, null, null, null, null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the log entry using the details provided. The log entry to be
	 * updated is specified using the rowId, and it is altered to use the title
	 * and body values passed in
	 * 
	 * @param rowId
	 *            id of log entry to update
	 * @param title
	 *            value to set log entry title to
	 * @param body
	 *            value to set log entry body to
	 * @return true if the log entry was updated successfully, false otherwise
	 */
	public boolean updateLogEntry(long rowId, String title, String body) {
		ContentValues args = new ContentValues();
		args.put(KEY_HOSTS, title);
		args.put(KEY_PATH, body);

		return mDb.update(DATABASE_LOG_TABLE_, args, KEY_ROWID + "=" + rowId, null) > 0;
	}

}
