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

package org.servDroid.ui.fragment;

import org.servDroid.ui.adapter.MainOptionsAdapter;
import org.servDroid.ui.option.MainOptionList;
import org.servDroid.web.R;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.google.inject.Inject;

public class OptionsFragment extends ServDroidBaseFragment implements OnItemClickListener {

	@InjectView(R.id.listViewMainFragment)
	ListView mListOption;

	@Inject
	private Context mContex;

	private MainOptionsAdapter mAdapter;

	@Inject
	private MainOptionList mOptions;

	private OnOptionClickListener mOptionClickListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mAdapter = new MainOptionsAdapter(mContex, mOptions.getMainOptions());

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnOptionClickListener) {
			mOptionClickListener = (OnOptionClickListener) activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.options_fragment, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mListOption.setAdapter(mAdapter);

		mListOption.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
		if (mOptionClickListener != null) {
			mOptionClickListener.onOptionClick((int) id);
		}
	}

	public interface OnOptionClickListener {
		public void onOptionClick(int id);
	}
}
