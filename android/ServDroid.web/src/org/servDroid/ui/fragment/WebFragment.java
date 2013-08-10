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

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IServiceHelper;
import org.servDroid.ui.activity.ServDroidBaseFragmentActivity;
import org.servDroid.ui.activity.ServDroidBaseFragmentActivity.OnActivityKeyUp;
import org.servDroid.web.R;

import roboguice.inject.InjectView;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.inject.Inject;

public class WebFragment extends ServDroidBaseFragment implements OnActivityKeyUp {

	@Inject
	IPreferenceHelper preferenceHelper;

	@InjectView(R.id.webView)
	private WebView mWebView;

	@InjectView(R.id.urlText)
	private EditText mUrlTextView;

	@Inject
	protected IServiceHelper serviceHelper;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.web_fragment, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mWebView.setWebViewClient(new ServDroidWebViewClient());

		mUrlTextView.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					mWebView.loadUrl(mUrlTextView.getText().toString());
					return true;
				}
				return false;
			}
		});

		mUrlTextView.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					mWebView.loadUrl(mUrlTextView.getText().toString());
				}
				return false;
			}
		});

		mUrlTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mUrlTextView.setSelection(mUrlTextView.getText().length());
				}
			}
		});
		mWebView.clearCache(true);
		mWebView.loadUrl("http://localhost:" + preferenceHelper.getPort());
		mWebView.requestFocus();
	}

	@Override
	public void onResume() {
		super.onResume();
		Activity activity = getActivity();
		if (activity instanceof ServDroidBaseFragmentActivity) {
			((ServDroidBaseFragmentActivity) activity).setOnKeyListener(this);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Activity activity = getActivity();
		if (activity instanceof ServDroidBaseFragmentActivity) {
			((ServDroidBaseFragmentActivity) activity).setOnKeyListener(null);
		}
	}

	private class ServDroidWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO: if open external URL open link in the external browser
			view.loadUrl(url);
			return false;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			mUrlTextView.setText(url);
			if (mUrlTextView.hasFocus()) {
				mUrlTextView.setSelection(mUrlTextView.getText().length());
			}
		}
	}

	@Override
	public boolean OnKeyUp(ServDroidBaseFragmentActivity activity, int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_UP) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mWebView.canGoBack() == true) {
					mWebView.goBack();
					return true;
				}
			}
		}
		return false;
	}

}
