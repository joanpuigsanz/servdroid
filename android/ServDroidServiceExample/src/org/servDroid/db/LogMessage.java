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

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class LogMessage implements Parcelable {

	private String mIp, mPath, mInfoBegining, mInfoEnd;
	private long mTimeStamp;

	public LogMessage(Parcel in) {
		mIp = in.readString();
		mPath = in.readString();
		mInfoBegining = in.readString();
		mInfoEnd = in.readString();
		mTimeStamp = in.readLong();
	}

	public LogMessage() {
		mIp = "";
		mPath = "";
		mInfoBegining = "";
		mInfoEnd = "";
	}

	public LogMessage(String ip, String path, String infoBegining,
			String infoEnd, long timeStamp) {
		mIp = ip;
		mPath = path;
		mInfoBegining = infoBegining;
		mInfoEnd = infoEnd;
		mTimeStamp = timeStamp;
	}

	public void writeToParcel(Parcel out, int flags) { // 3
		out.writeString(mIp);
		out.writeString(mPath);
		out.writeString(mInfoBegining);
		out.writeString(mInfoEnd);
		out.writeLong(mTimeStamp);
	}

	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<LogMessage> CREATOR = new Parcelable.Creator<LogMessage>() { // 5

		public LogMessage createFromParcel(Parcel source) {
			return new LogMessage(source);
		}

		public LogMessage[] newArray(int size) {
			return new LogMessage[size];
		}

	};

	public String getIp() {
		return mIp;
	}

	public void setIp(String ip) {
		this.mIp = ip;
	}

	public String getPath() {
		return mPath;
	}

	public void setPath(String path) {
		this.mPath = path;
	}

	public String getInfoBegining() {
		return mInfoBegining;
	}

	public void setInfoBegining(String infoBegining) {
		this.mInfoBegining = infoBegining;
	}

	public String getInfoEnd() {
		return mInfoEnd;
	}

	public void setInfoEnd(String infoEnd) {
		this.mInfoEnd = infoEnd;
	}

	public long getTimeStamp() {
		return mTimeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.mTimeStamp = timeStamp;
	}

	@Override
	public String toString() {
		String line = "";

		Date timeStamp = new Date(getTimeStamp());

		line = line + "[" + timeStamp.toGMTString() + "]";

		String begining = getInfoBegining();

		if (begining != null && begining.length() > 0) {
			line = line + " [" + begining + "] ";
		}

		if (getIp() != null && getIp().length() > 0) {
			line = line + " " + getIp();
		}

		if (getPath() != null && getPath().length() > 0) {
			line = line + " \"" + getPath() + "\"";
		}

		String end = getInfoEnd();

		if (end != null && end.length() > 0) {
			line = line + " -- " + end + "";
		}
		return line;
	}
	
}
