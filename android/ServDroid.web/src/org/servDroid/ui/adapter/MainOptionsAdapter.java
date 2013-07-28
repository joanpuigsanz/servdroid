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

package org.servDroid.ui.adapter;

import java.util.List;

import org.servDroid.ui.option.IMainOption;
import org.servDroid.web.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MainOptionsAdapter extends ArrayAdapter<IMainOption> {

	private List<IMainOption> mElements;

	private LayoutInflater mInflater;

	private Context mContext;

	public MainOptionsAdapter(Context context, List<IMainOption> element) {
		super(context, R.layout.list_row_options, element);
		mElements = element;
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		if (mElements == null) {
			return 0;
		}
		return mElements.size();
	}

	@Override
	public IMainOption getItem(int position) {
		if (mElements== null || position < 0 || position >= mElements.size()) {
			return null;
		}
		return mElements.get(position);
	}

	@Override
	public long getItemId(int position) {
		if (mElements== null || position < 0 || position >= mElements.size()) {
			return -1;
		}
		return mElements.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null)
			view = mInflater.inflate(R.layout.list_row_options, null);

		IMainOption option = getItem(position);
		if (option == null) {
			return null;
		}

		TextView text = (TextView) view.findViewById(R.id.textViewListRowOption);

		text.setText(option.getName());
		text.setCompoundDrawablesWithIntrinsicBounds(
				mContext.getResources().getDrawable(option.getResourceImage()), null,
				null, null);

		return view;
	}

}
