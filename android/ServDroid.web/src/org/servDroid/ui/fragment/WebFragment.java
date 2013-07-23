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

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;

public class WebFragment extends RoboSherlockFragment {

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
