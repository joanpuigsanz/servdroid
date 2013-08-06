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
import org.servDroid.module.AppModule;
import org.servDroid.ui.util.DialogFactory;
import org.servDroid.util.FilesChecker;

import roboguice.activity.RoboActivity;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends RoboActivity {

	public static final String KEY_NEW_VERSION = "NEW_VERSION";

	@Inject
	@Named(AppModule.HAS_TWO_PANES)
	protected boolean hasTwoPanes;

	@Inject
	@Named(AppModule.APP_VERSION_NAME)
	private String version;

	@Inject
	private IPreferenceHelper mPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent;
		if (hasTwoPanes) {
			intent = new Intent(this, MainActivityTwoPanes.class);
		} else {
			intent = new Intent(this, MainActivityOnePane.class);
		}

		String previousVersion = mPreferences.getPreviousVersion();
		// new version?
		if (previousVersion == null || previousVersion.length() < 4) {
			FilesChecker.checkLogPath(mPreferences.getLogPath(), null);
			FilesChecker.checkWwwPath(mPreferences.getWwwPath(), null);
			FilesChecker.checkErrorPath(mPreferences.getErrorPath(), null);
			intent.putExtra(KEY_NEW_VERSION, true);
		} else if (previousVersion != version) {
			// Do update process if needed
		}

		DialogFactory.showAboutDialog(this, previousVersion, null);

		mPreferences.setPreviousVersion(version);

		startActivity(intent);

	}

}
