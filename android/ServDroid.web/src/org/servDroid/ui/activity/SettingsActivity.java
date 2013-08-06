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

package org.servDroid.ui.activity;

import org.servDroid.helper.IPreferenceHelper;
import org.servDroid.helper.IStoreHelper;
import org.servDroid.helper.PreferenceUiHelper;
import org.servDroid.module.AppModule;
import org.servDroid.web.R;

import com.actionbarsherlock.view.MenuItem;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockPreferenceActivity;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.content.Intent;
import android.os.Bundle;

public class SettingsActivity extends RoboSherlockPreferenceActivity {

	@Inject
	private IPreferenceHelper mPrefereneceHelper;

	@Inject
	IStoreHelper mStoreHelper;

	@Inject
	@Named(AppModule.APP_VERSION_NAME)
	protected String mVersion;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.preferences);

		Runnable invalidateUi = new Runnable() {
			@Override
			public void run() {
				// This is a hack to refresh the UI when the preferences are
				// reseted
				Intent intent = getIntent();
				overridePendingTransition(0, 0);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				finish();
				overridePendingTransition(0, 0);
				startActivity(intent);
			}
		};
		new PreferenceUiHelper(getPreferenceScreen(), this, mPrefereneceHelper, mVersion,
				invalidateUi, mStoreHelper);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
