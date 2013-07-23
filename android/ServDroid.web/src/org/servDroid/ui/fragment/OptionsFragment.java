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

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

public class OptionsFragment extends RoboSherlockFragment implements OnItemClickListener {

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
