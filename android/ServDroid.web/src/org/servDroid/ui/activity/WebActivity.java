package org.servDroid.ui.activity;

import org.servDroid.web.R;

import com.actionbarsherlock.view.Menu;

import roboguice.inject.InjectFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class WebActivity extends ServDroidBaseFragmentActivity {

	@InjectFragment(R.id.webFragment)
	private Fragment mWebFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.web_activity);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void createMainMenus(Menu menu) {
		super.createMainMenus(menu);
	}
}
