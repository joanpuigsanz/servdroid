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

package org.servDroid.ui.adapter;

import java.util.List;

import org.servDroid.web.R;
import org.servDroid.db.LogMessage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class LogListAdapter extends ArrayAdapter<LogMessage> {

	private LayoutInflater mLayoutInflater;
	private List<LogMessage> mItems;

	public LogListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	/**
	 * Set items
	 * 
	 * @param items
	 */
	public void setItems(List<LogMessage> items) {
		mItems = items;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mLayoutInflater.inflate(R.layout.row_log, null);
		}
		LogMessage o = mItems.get(position);
		if (o != null) {

			TextView tt = (TextView) v.findViewById(R.id.textLog);
			if (tt != null) {
				String line = o.toString();

				tt.setText(line);
			}
		}
		return v;
	}
}
