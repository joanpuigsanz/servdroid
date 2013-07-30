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
import org.servDroid.web.R;

import roboguice.inject.InjectView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.inject.Inject;

public class WebFragment extends ServDroidBaseFragment {

	@Inject
	IPreferenceHelper preferenceHelper;
	
	@InjectView(R.id.webView)
	WebView mWebView;
	
	@Inject
	protected IServiceHelper serviceHelper;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.web_fragment, container, false);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mWebView.setWebViewClient(new WebViewClient() {
	        @Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        	//TODO: if open external URL open link in the external browser
	            view.loadUrl(url);
	            return false;
	        }
	    });
	}
	
	@Override
	public void onResume() {
		mWebView.clearCache(true);
		mWebView.loadUrl("http://localhost:" + preferenceHelper.getPort());
		super.onResume();
	}

}
